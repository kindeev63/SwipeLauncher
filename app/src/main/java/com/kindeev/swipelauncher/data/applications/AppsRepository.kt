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

/**
 * AppsRepository позволяет работать с приложениями на устройстве.
 * Он содержит список всех приложений в формате StateFlow.
 */
class AppsRepository(context: Context) : ApplicationsManager {
    private val appContext = context.applicationContext
    private val launcherApps: LauncherApps =
        appContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    private val userHandle = Process.myUserHandle()

    private val _applications = MutableStateFlow<List<ApplicationInfo>>(emptyList())

    override val applications: StateFlow<List<ApplicationInfo>>
        get() = _applications.asStateFlow()

    override fun getApplication(packageName: String) = _applications.value.find { it.packageName == packageName }

    /**
     * Загружает все приложения в applications - список всех приложений
     */
    suspend fun loadApplications() = withContext(Dispatchers.IO) {
        launcherApps.getActivityList(null, userHandle)
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

    private fun isInstalled(packageName: String): Boolean =
        launcherApps.getActivityList(packageName, userHandle).isNotEmpty()

    /**
     * Отправляет Intent на удаление приложения
     */
    override fun delete(packageName: String) =
        appContext.startActivity(
            Intent(
                Intent.ACTION_DELETE,
                "package:$packageName".toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )

    /**
     * Пробует открыть MainActivity приложения
     */
    override fun open(packageName: String) {
        launcherApps.getActivityList(packageName, userHandle)
            .firstOrNull()?.componentName?.let { componentName ->
                launcherApps.startMainActivity(
                    componentName,
                    Process.myUserHandle(),
                    null,
                    null
                )
            }
    }

    /**
     * Отправляет Intent на переход к activity деталей приложения (в настройках)
     */
    override fun openAppDetails(packageName: String) {
        if (isInstalled(packageName)) {
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

    /**
     * При установке нового приложения
     * Добавляет его в список всех приложений
     */
    fun onAppAdded(packageName: String) {
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

    /**
     * При удалении приложения
     * Удаляет его из списка всех приложений
     */
    fun onAppRemoved(packageName: String) {
        _applications.value = _applications.value
            .filterNot { it.packageName == packageName }
    }

    /**
     * При обновлении приложения
     * Сначала удаляет старые данные, затем загружает новые
     */
    fun onAppChanged(packageName: String) {
        onAppRemoved(packageName)
        onAppAdded(packageName)
    }
}