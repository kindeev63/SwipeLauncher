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

class AppsRepository(private val context: Context) {
    private val launcherApps: LauncherApps =
        context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    private val _applications = MutableStateFlow<List<ApplicationInfo>>(emptyList())

    val applications: StateFlow<List<ApplicationInfo>>
        get() = _applications.asStateFlow()

    suspend fun loadApplications() = withContext(Dispatchers.IO) {
        launcherApps.getActivityList(null, Process.myUserHandle())
            .map { activityInfo ->
                ApplicationInfo(
                    title = activityInfo.label.toString(),
                    packageName = activityInfo.componentName.packageName,
                    componentName = activityInfo.componentName
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.title.lowercase() }
            .also {
                _applications.value = it
            }
    }

    fun isInstalled(packageName: String): Boolean =
        launcherApps.getActivityList(packageName, Process.myUserHandle()).isNotEmpty()

    fun delete(packageName: String) = context.startActivity(
        Intent(
            Intent.ACTION_DELETE,
            "package:$packageName".toUri()
        )
    )

    fun open(applicationInfo: ApplicationInfo) = isInstalled(applicationInfo.packageName).also { installed ->
        if (installed) {
            launcherApps.startMainActivity(
                applicationInfo.componentName,
                Process.myUserHandle(),
                null,
                null
            )
        }
    }

    fun openAppDetails(packageName: String) = isInstalled(packageName).also { installed ->
        if (installed) {

            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                ).apply {
                    data = "package:$packageName".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }
}