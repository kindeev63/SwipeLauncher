package com.kindeev.swipelauncher.di

import android.content.Context
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.data.applications.ApplicationsManager
import com.kindeev.swipelauncher.data.applications.AppsObserver
import com.kindeev.swipelauncher.data.applications.AppsRepository
import com.kindeev.swipelauncher.data.backup.ExportCircleMenusUseCase
import com.kindeev.swipelauncher.data.backup.ImportCircleMenusUseCase
import com.kindeev.swipelauncher.data.coil.CoilLoaderManager
import com.kindeev.swipelauncher.data.coil.prefetchApplicationImages
import com.kindeev.swipelauncher.data.database.AppDataBase
import com.kindeev.swipelauncher.data.database.getRepository
import com.kindeev.swipelauncher.data.userImages.UserImagesRepository
import com.kindeev.swipelauncher.data.userImages.UserImagesStorage
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.useCases.CheckCircleMenuUseCase
import com.kindeev.swipelauncher.domain.utils.getMinScreenLength
import com.kindeev.swipelauncher.presentation.mappers.CircleMenuForUIMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val checkCircleMenuUseCase = CheckCircleMenuUseCase()

    private val coilLoaderManager = CoilLoaderManager(appContext)
    val dataRepository = AppDataBase.getDataBase(appContext).getRepository()

    private val appsRepository = AppsRepository(appContext)
    private val appsObserver = AppsObserver(appContext, appsRepository, coilLoaderManager)

    val applicationsManager: ApplicationsManager
        get() = appsRepository

    private val userImagesStorage = UserImagesStorage(appContext)

    val userImagesRepository = UserImagesRepository(userImagesStorage, coilLoaderManager)

    val importCircleMenusUseCase = ImportCircleMenusUseCase(
        userImagesRepository,
        dataRepository,
        checkCircleMenuUseCase,
        applicationsManager,
        appContext
    )

    val exportCircleMenusUseCase = ExportCircleMenusUseCase(userImagesRepository, appContext)

    val circleMenuForUIMapper = CircleMenuForUIMapper(userImagesRepository, appContext)

    val circleMenus = dataRepository.getAllCircleMenus().stateIn(
        scope = appScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val circleMenusForUI =
        circleMenus.map { it.map { menu -> circleMenuForUIMapper.map(menu) } }.stateIn(
            scope = appScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val settings = dataRepository.getSettings().stateIn(
        scope = appScope,
        started = SharingStarted.Eagerly,
        initialValue = Constants.defaultSettings
    )

    var flashLightCondition = false

    init {
        Constants.minScreenLength = appContext.getMinScreenLength()
        Constants.settingsTextSize = Constants.minScreenLength.sp / 20

        appScope.launch(Dispatchers.IO) {
            launch {
                val applications = appsRepository.loadApplications()
                coilLoaderManager.prefetchApplicationImages(applications.map { it.packageName })
                appsObserver.start(
                    onChange = {
                        if (appsRepository.applications.value.isNotEmpty()) {
                            appScope.launch {
                                dataRepository.insertCircleMenus(
                                    checkCircleMenuUseCase.getOnlyChanged(
                                        circleMenus = circleMenus.value,
                                        allPackageNames = appsRepository.applications.value.map { it.packageName },
                                        userImageIds = userImagesRepository.getAllIds()
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    }

}