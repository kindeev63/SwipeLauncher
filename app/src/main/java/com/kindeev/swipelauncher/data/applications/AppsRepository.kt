package com.kindeev.swipelauncher.data.applications

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.os.Process
import android.provider.Settings
import androidx.core.net.toUri
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AppsRepository(context: Context) : ApplicationsManager {
    private val appContext = context.applicationContext
    private val launcherApps: LauncherApps =
        appContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    private val userHandle = Process.myUserHandle()

    private val _applications = MutableStateFlow<List<ApplicationInfo>>(emptyList())

    override val applications: StateFlow<List<ApplicationInfo>>
        get() = _applications.asStateFlow()

    suspend fun loadAllApplicationsToStateFlow() = withContext(Dispatchers.IO) {
        val allApplications = launcherApps.getActivityList(null, userHandle)
            .distinctBy { it.applicationInfo.packageName }
            .map { activityInfo ->
                ApplicationInfo(
                    title = activityInfo.label.toString(),
                    packageName = activityInfo.componentName.packageName,
                    componentName = activityInfo.componentName
                )
            }
            .sortedBy { it.title.lowercase() }
        _applications.value = allApplications
    }

    override fun getApplication(packageName: String): ApplicationInfo? =
        _applications.value.find { it.packageName == packageName }

    override fun open(packageName: String) {
        val applicationInfo = applications.value.find { it.packageName == packageName } ?: return
        launcherApps.startMainActivity(
            applicationInfo.componentName,
            userHandle,
            null,
            null
        )
    }

    override fun delete(packageName: String) {
        if (isAppInstalled(packageName)) {
            appContext.startActivity(
                Intent(
                    Intent.ACTION_DELETE,
                    "package:$packageName".toUri()
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }

    override fun openAppDetails(packageName: String) {
        if (isAppInstalled(packageName)) {
            appContext.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                ).apply {
                    data = "package:$packageName".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }

    private fun isAppInstalled(packageName: String): Boolean =
        applications.value.any { it.packageName == packageName }

    fun addAppByPackageName(packageName: String) {
        val activityInfo = launcherApps
            .getActivityList(packageName, userHandle)
            .firstOrNull() ?: return

        val newApp = ApplicationInfo(
            title = activityInfo.label.toString(),
            packageName = packageName,
            componentName = activityInfo.componentName
        )
        _applications.value = (_applications.value + newApp)
            .sortedBy { it.title.lowercase() }
    }

    fun removeAppByPackageName(packageName: String) {
        _applications.value = _applications.value
            .filterNot { it.packageName == packageName }
    }

    fun reloadAppByPackageName(packageName: String) {
        removeAppByPackageName(packageName)
        addAppByPackageName(packageName)
    }
}