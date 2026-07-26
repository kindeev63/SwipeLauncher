package com.kindeev.swipelauncher.data.applications

import android.content.Context
import android.content.pm.LauncherApps
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import com.kindeev.swipelauncher.data.coil.CoilLoaderManager
import com.kindeev.swipelauncher.data.coil.appImageUri
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import com.kindeev.swipelauncher.domain.useCases.CheckCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuImageToImageBitmapUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppsObserver(
    private val applicationsRepository: AppsRepository,
    private val coilLoaderManager: CoilLoaderManager,
    private val dataRepository: DataRepository,
    private val checkCircleMenuUseCase: CheckCircleMenuUseCase,
    private val circleMenuStateFlowUseCase: CircleMenuStateFlowUseCase,
    private val userImagesRepository: UserImagesRepository,
    private val circleMenuImageToImageBitmapUseCase: CircleMenuImageToImageBitmapUseCase,
    private val ioScope: CoroutineScope,
    context: Context
) {
    private val launcherApps =
        context.applicationContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    private fun launcherAppsCallback() = object : LauncherApps.Callback() {
        override fun onPackageAdded(packageName: String, user: UserHandle) {
            applicationsRepository.addAppByPackageName(packageName)
            coilLoaderManager.prefetch(
                appImageUri(packageName),
                "app_image_$packageName"
            )
        }

        override fun onPackageRemoved(packageName: String, user: UserHandle) {
            applicationsRepository.removeAppByPackageName(packageName)
            coilLoaderManager.remove("app_image_$packageName")
            ioScope.launch {
                updateCircleMenus()
            }
        }

        override fun onPackageChanged(packageName: String, user: UserHandle) {
            applicationsRepository.reloadAppByPackageName(packageName)
            coilLoaderManager.remove("app_image_$packageName")
            coilLoaderManager.prefetch(
                appImageUri(packageName),
                "app_image_$packageName"
            )
            ioScope.launch {
                updateCircleMenuImageForPackageName(packageName)
            }
        }

        override fun onPackagesAvailable(
            packageNames: Array<out String>,
            user: UserHandle,
            replacing: Boolean
        ) {
            packageNames.forEach { packageName ->
                applicationsRepository.addAppByPackageName(packageName)
                coilLoaderManager.prefetch(
                    appImageUri(packageName),
                    "app_image_$packageName"
                )
            }
        }

        override fun onPackagesUnavailable(
            packageNames: Array<out String>,
            user: UserHandle,
            replacing: Boolean
        ) {
            packageNames.forEach { packageName ->
                applicationsRepository.removeAppByPackageName(packageName)
                coilLoaderManager.remove("app_image_$packageName")
            }
            ioScope.launch {
                updateCircleMenus()
            }
        }
    }

    private suspend fun updateCircleMenuImageForPackageName(packageName: String) {
        circleMenuImageToImageBitmapUseCase.updateImageForPackageName(packageName)
    }

    private suspend fun updateCircleMenus() {
        if (applicationsRepository.applications.value.isNotEmpty()) {
            val changedCircleMenus =
                checkCircleMenuUseCase.getOnlyChanged(
                    circleMenus = circleMenuStateFlowUseCase.circleMenus.value,
                    allPackageNames = applicationsRepository.applications.value.map { it.packageName },
                    userImageIds = userImagesRepository.getAllIds()
                )
            if (changedCircleMenus.isNotEmpty())
                dataRepository.insertCircleMenus(changedCircleMenus)
        }
    }

    fun start() {
        launcherApps.registerCallback(
            launcherAppsCallback(),
            Handler(Looper.getMainLooper())
        )
    }
}