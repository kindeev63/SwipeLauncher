package com.kindeev.swipelauncher.domain.useCases

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Process
import android.provider.Settings
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.domain.entities.ApplicationData
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import androidx.core.net.toUri
import com.kindeev.swipelauncher.di.container

class ApplicationsUseCase(
    private val context: Context
) {

    private val launcherApps: LauncherApps =
        context.applicationContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    fun getAllApplicationInfo(): List<ApplicationInfo> =
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

    fun isAppInstalled(packageName: String): Boolean {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            packageInfo.packageName == packageName
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun getAppDetails(packageName: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        context.startActivity(intent)
    }

    fun getApplicationInfo(packageName: String): ApplicationInfo {
        context.container.applicationsInfo.value
            .find { it.packageName == packageName }
            ?.let { return it }

        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
        val activities = launcherApps.getActivityList(packageName, Process.myUserHandle())

        val activityInfo = activities.first()

        return ApplicationInfo(
            title = activityInfo.label.toString(),
            packageName = activityInfo.componentName.packageName,
            componentName = activityInfo.componentName
        )
    }

    fun getThisAppIcon() =
        context.packageManager.getApplicationInfo(context.packageName, 0)
            .loadIcon(context.packageManager).toBitmap()
            .asImageBitmap()

    fun deleteApp(packageName: String) {
        val packageUri = "package:$packageName".toUri()
        val uninstallIntent = Intent(Intent.ACTION_DELETE, packageUri)
        context.startActivity(uninstallIntent)
    }

    fun openApp(packageName: String) {
        if (isAppInstalled(packageName)) {
            val intent =
                context.packageManager.getLaunchIntentForPackage(packageName)
            intent?.let { context.startActivity(it) }
        }
    }

    suspend fun check(
        applicationsData: List<ApplicationData>,
        allApplicationInfo: List<ApplicationInfo>
    ) {
        val applicationInfoPackageNames = allApplicationInfo.map { it.packageName }
        val allNotMaskApplicationData = getAllNotMaskAppData(allApplicationInfo)
        val toDelete = applicationsData.filter {
            it.packageName !in applicationInfoPackageNames ||
                    (it.image is AppImage
                            && it.image.packageName !in applicationInfoPackageNames
                            && it.copy(image = AppImage(it.packageName)) in allNotMaskApplicationData
                            )
        }
        context.container.dataRepository.deleteApplicationsData(toDelete)
        context.container.dataRepository.insertApplicationsData(
            applicationsData
                .filter { it !in toDelete }
                .filter { it.image is AppImage && it.image.packageName !in applicationInfoPackageNames }
                .map {
                    it.copy(image = AppImage(it.packageName))
                }
        )
    }

    suspend fun showApp(packageName: String) {
        context.container.applicationsData.value.find { it.packageName == packageName }
            ?.let { applicationData ->
                val notMaskAppData = getNotMaskApplicationData(applicationData.packageName)
                if (notMaskAppData == applicationData.copy(hidden = false)) {
                    context.container.dataRepository.deleteApplicationData(applicationData)
                } else {
                    context.container.dataRepository.insertApplicationData(applicationData.copy(hidden = false))
                }
            }
    }

    suspend fun changeApp(applicationData: ApplicationData) {
        val notMaskAppData = getNotMaskApplicationData(applicationData.packageName)
        if (applicationData == notMaskAppData) {
            context.container.dataRepository.deleteApplicationDataByPackageName(applicationData.packageName)
        } else {
            context.container.dataRepository.insertApplicationData(applicationData)
        }
    }

    fun getNotHidden(applicationsInfo: List<ApplicationInfo>): List<ApplicationInfo> {
        val hidden =
            context.container.applicationsData.value.filter { it.hidden }.map { it.packageName }
        return applicationsInfo.filter { it.packageName !in hidden }
    }

    fun getHidden(
        applicationData: List<ApplicationData>,
        applicationsInfo: List<ApplicationInfo>,
    ): List<ApplicationInfo> {
        val hiddenPackageNames = applicationData.filter { it.hidden }.map { it.packageName }
        return  applicationsInfo.filter { it.packageName in hiddenPackageNames }
    }


    fun getNotMaskApplicationData(packageName: String): ApplicationData {
        return getNotMaskApplicationData(getApplicationInfo(packageName))
    }

    fun getNotMaskApplicationData(applicationInfo: ApplicationInfo): ApplicationData {
        return ApplicationData(
            title = applicationInfo.title,
            image = AppImage(applicationInfo.packageName),
            packageName = applicationInfo.packageName
        )
    }

    fun getAllApplicationData(allApplicationInfo: List<ApplicationInfo>): List<ApplicationData> {
        return allApplicationInfo.map { getApplicationData(it) }
    }

    fun getAllNotMaskAppData(allApplicationInfo: List<ApplicationInfo>): List<ApplicationData> {
        return allApplicationInfo.map { getNotMaskApplicationData(it) }
    }

    fun getApplicationData(packageName: String): ApplicationData {
        return context.container.applicationsData.value.find { it.packageName == packageName }
            ?: getApplicationData(getApplicationInfo(packageName))
    }

    fun getApplicationData(applicationInfo: ApplicationInfo): ApplicationData {
        return context.container.applicationsData.value.find { it.packageName == applicationInfo.packageName }
            ?: ApplicationData(
                packageName = applicationInfo.packageName,
                title = applicationInfo.title,
                image = AppImage(packageName = applicationInfo.packageName),
                hidden = false
            )
    }
}