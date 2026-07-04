package com.kindeev.swipelauncher.data.applications

import android.content.Context
import android.content.pm.LauncherApps
import android.os.Handler
import android.os.Looper
import android.os.UserHandle
import com.kindeev.swipelauncher.data.coil.CoilLoaderManager
import com.kindeev.swipelauncher.data.coil.appImageUri

class AppsObserver(
    context: Context,
    private val repository: AppsRepository,
    private val coilLoaderManager: CoilLoaderManager,
) {
    private val launcherApps = context.applicationContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    private fun getCallback(onChange: () -> Unit) = object : LauncherApps.Callback() {
        override fun onPackageAdded(packageName: String, user: UserHandle) {
            repository.onAppAdded(packageName)
            coilLoaderManager.prefetch(
                appImageUri(packageName),
                "app_image_$packageName"
            )
        }

        override fun onPackageRemoved(packageName: String, user: UserHandle) {
            repository.onAppRemoved(packageName)
            coilLoaderManager.remove("app_image_$packageName")
            onChange()
        }

        override fun onPackageChanged(packageName: String, user: UserHandle) {
            repository.onAppChanged(packageName)
            coilLoaderManager.remove("app_image_$packageName")
            coilLoaderManager.prefetch(
                appImageUri(packageName),
                "app_image_$packageName"
            )
            onChange()
        }

        override fun onPackagesAvailable(packageNames: Array<out String>, user: UserHandle, replacing: Boolean) {
            packageNames.forEach { packageName ->
                repository.onAppAdded(packageName)
                coilLoaderManager.prefetch(
                    appImageUri(packageName),
                    "app_image_$packageName"
                )
            }
        }

        override fun onPackagesUnavailable(packageNames: Array<out String>, user: UserHandle, replacing: Boolean) {
            packageNames.forEach { packageName ->
                repository.onAppRemoved(packageName)
                coilLoaderManager.remove("app_image_$packageName")
            }
            onChange()
        }
    }

    fun start(onChange: () -> Unit) {
        launcherApps.registerCallback(getCallback(onChange), Handler(Looper.getMainLooper()))
    }

}