package com.kindeev.swipelauncher.presentation

import android.app.Application
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.data.applications.AppsObserver
import com.kindeev.swipelauncher.data.applications.AppsRepository
import com.kindeev.swipelauncher.data.coil.CoilLoaderManager
import com.kindeev.swipelauncher.data.coil.prefetchApplicationImages
import com.kindeev.swipelauncher.data.userImages.getUsedImagesIds
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import com.kindeev.swipelauncher.domain.useCases.CheckCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.domain.utils.getMinScreenLength
import com.kindeev.swipelauncher.domain.utils.setActionAndImageTypes
import com.kindeev.swipelauncher.presentation.activities.ErrorActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalExceptionHandler.initialize(this, ErrorActivity::class.java)
        DI.init(this)
        initApp()
    }

    private fun initApp() {
        Constants.minScreenLength = getMinScreenLength()
        Constants.settingsTextSize = Constants.minScreenLength.sp / 20
        setActionAndImageTypes()
        val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val appsRepository = DI.container.getSingle<AppsRepository>()
        val coilLoaderManager = DI.container.getSingle<CoilLoaderManager>()
        val appsObserver = DI.container.getSingle<AppsObserver>()
        val circleMenuStateFlowUseCase = DI.container.getSingle<CircleMenuStateFlowUseCase>()
        val userImagesRepository = DI.container.getSingle<UserImagesRepository>()
        val dataRepository = DI.container.getSingle<DataRepository>()
        val checkCircleMenuUseCase = DI.container.getSingle<CheckCircleMenuUseCase>()
        ioScope.launch {
            launch {
                appsRepository.loadAllApplicationsToStateFlow()
                coilLoaderManager.prefetchApplicationImages(appsRepository.applications.value.map { it.packageName })
                launch {
                    appsObserver.start()
                }
                launch {
                    ioScope.launch {
                        val changedCircleMenus =
                            checkCircleMenuUseCase.getOnlyChanged(
                                circleMenus = circleMenuStateFlowUseCase.circleMenus.value,
                                allPackageNames = appsRepository.applications.value.map { it.packageName },
                                userImageIds = userImagesRepository.getAllIds()
                            )
                        if (changedCircleMenus.isNotEmpty())
                            dataRepository.insertCircleMenus(changedCircleMenus)
                    }
                }
            }
            launch {
                circleMenuStateFlowUseCase.circleMenus.collect { allCircleMenus ->
                    if (allCircleMenus.isEmpty()) return@collect
                    userImagesRepository.removeUnused(
                        allCircleMenus.getUsedImagesIds().toSet()
                    )
                }
            }
        }
    }
}