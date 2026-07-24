package com.kindeev.swipelauncher.presentation

import android.app.Application
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.data.applications.AppsObserver
import com.kindeev.swipelauncher.data.applications.AppsRepository
import com.kindeev.swipelauncher.data.coil.CoilLoaderManager
import com.kindeev.swipelauncher.data.coil.prefetchApplicationImages
import com.kindeev.swipelauncher.data.userImages.getUsedImagesIds
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
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
        val appsRepository = DI.container.getDependency<AppsRepository>()
        val coilLoaderManager = DI.container.getDependency<CoilLoaderManager>()
        val appsObserver = DI.container.getDependency<AppsObserver>()
        val circleMenuStateFlowUseCase = DI.container.getDependency<CircleMenuStateFlowUseCase>()
        val userImagesRepository = DI.container.getDependency<UserImagesRepository>()
        ioScope.launch {
            launch {
                appsRepository.loadAllApplicationsToStateFlow()
                coilLoaderManager.prefetchApplicationImages(appsRepository.applications.value.map { it.packageName })
                appsObserver.start()
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