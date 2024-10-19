package com.kindeev.swipelauncher.domain.useCases

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.ApplicationData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo

class ApplicationsUseCase(
    private val context: Context,
    private val getItemImageUseCase: GetItemImageUseCase
) {
    fun getAllApplicationInfo(): List<ApplicationInfo> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val allAppInfo = context.packageManager.queryIntentActivities(intent, 0)
            .map { it.activityInfo.applicationInfo }
            .map {
                ApplicationInfo(
                    title = it.loadLabel(context.packageManager).toString(),
                    icon = it.loadIcon(context.packageManager).toBitmap().asImageBitmap(),
                    packageName = it.packageName
                )
            }
        val mutableAllApplicationInfo = allAppInfo.toMutableList()
        allAppInfo.forEach { applicationData ->
            if (mutableAllApplicationInfo.count { it.packageName == applicationData.packageName } > 1) {
                mutableAllApplicationInfo.remove(applicationData)
            }
        }

        val allApplicationData = LauncherData.allApplicationData.value ?: emptyList()
        mutableAllApplicationInfo.replaceAll { applicationInfo ->
            allApplicationData.find { it.packageName == applicationInfo.packageName }
                ?.let { applicationData ->
                    ApplicationInfo(
                        title = applicationData.title,
                        icon = getItemImageUseCase.getItemImage(applicationData.image)
                            ?: throw IllegalArgumentException("Illegal image"),
                        packageName = applicationData.packageName
                    )
                } ?: applicationInfo
        }
        return mutableAllApplicationInfo.sortedBy { it.title }
    }

    fun getApplications(): List<ApplicationInfo> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val allAppInfo = context.packageManager.queryIntentActivities(intent, 0)
            .map { it.activityInfo.applicationInfo }
            .map {
                ApplicationInfo(
                    title = it.loadLabel(context.packageManager).toString(),
                    icon = it.loadIcon(context.packageManager).toBitmap().asImageBitmap(),
                    packageName = it.packageName
                )
            }
        val mutableAllApplicationInfo = allAppInfo.toMutableList()
        allAppInfo.forEach { applicationData ->
            if (mutableAllApplicationInfo.count { it.packageName == applicationData.packageName } > 1) {
                mutableAllApplicationInfo.remove(applicationData)
            }
        }
        return mutableAllApplicationInfo.sortedBy { it.title }
    }

    fun isAppInstalled(packageName: String): Boolean {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            packageInfo.packageName == packageName
        } catch (e: PackageManager.NameNotFoundException) {
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
        val applicationInfo =
            LauncherData.allApplicationInfo.value?.find { it.packageName == packageName }
        return if (applicationInfo == null) {
            val appInfo =
                context.packageManager.getApplicationInfo(packageName, 0)
            ApplicationInfo(
                title = appInfo.loadLabel(context.packageManager).toString(),
                icon = appInfo.loadIcon(context.packageManager).toBitmap().asImageBitmap(),
                packageName = appInfo.packageName
            )
        } else {
            applicationInfo
        }
    }

    fun getThisAppIcon() =
        context.packageManager.getApplicationInfo(context.packageName, 0).loadIcon(context.packageManager).toBitmap()
            .asImageBitmap()

    fun getApplicationData(packageName: String): ApplicationData {
        val applicationData =
            LauncherData.allApplicationData.value?.find { it.packageName == packageName }
        return if (applicationData == null) {
            val applicationInfo =
                context.packageManager.getApplicationInfo(packageName, 0)
            ApplicationData(
                title = applicationInfo.loadLabel(context.packageManager).toString(),
                image = AppImage(packageName),
                packageName = applicationInfo.packageName
            )
        } else {
            applicationData
        }
    }

    fun deleteApp(packageName: String) {
        val packageUri = Uri.parse("package:$packageName")
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
        val allPackageNames = allApplicationInfo.map { it.packageName }
        val toDelete = mutableListOf<ApplicationData>()
        val toChange = mutableListOf<ApplicationData>()
        applicationsData.forEach { applicationData ->
            if (applicationData.packageName !in allApplicationInfo.map { it.packageName }) {
                toDelete.add(applicationData)
            } else {
                if (applicationData.image is AppImage) {
                    if (applicationData.image.packageName !in allPackageNames) {
                        val appInfo = getNotMaskApplicationData(applicationData.packageName)
                        if (appInfo.title == applicationData.title && !applicationData.hidden) {
                            toDelete.add(applicationData)
                        } else {
                            toChange.add(
                                applicationData.copy(
                                    image = AppImage(applicationData.packageName)
                                )
                            )
                        }
                    }
                }
            }
        }
        LauncherData.insertApplicationsData(toChange)
        LauncherData.deleteApplicationsData(toDelete)
    }

    suspend fun hideApp(packageName: String) {
        LauncherData.insertApplicationData(
            LauncherData.allApplicationData.value?.find { it.packageName == packageName }
                ?.copy(hidden = true)
                ?: getApplicationData(packageName).copy(hidden = true)
        )
    }

    suspend fun showApp(packageName: String) {
        LauncherData.allApplicationData.value?.find { it.packageName == packageName }
            ?.let { applicationData ->
                val appInfo = getNotMaskApplicationData(applicationData.packageName)
                if (applicationData.title == appInfo.title && applicationData.image is AppImage && applicationData.image.packageName == appInfo.packageName
                ) {
                    LauncherData.deleteApplicationData(applicationData)
                } else {
                    LauncherData.insertApplicationData(applicationData.copy(hidden = false))
                }
            }
    }

    suspend fun changeApp(applicationData: ApplicationData) {
        val appInfo = getNotMaskApplicationData(applicationData.packageName)
        if (applicationData.title == appInfo.title && applicationData.image is AppImage && applicationData.image.packageName == appInfo.packageName
        ) {
            LauncherData.deleteApplicationDataByPackageName(applicationData.packageName)
        } else {
            LauncherData.insertApplicationData(
                LauncherData.allApplicationData.value?.find { it.packageName == applicationData.packageName }
                    ?.copy(title = applicationData.title, image = applicationData.image)
                    ?: applicationData
            )
        }
    }

    fun getNotHidden(applicationsInfo: List<ApplicationInfo>): List<ApplicationInfo> {
        val result = applicationsInfo.toMutableList()
        val hidden = LauncherData.allApplicationData.value?.filter { it.hidden }?.map { it.packageName }
            ?: emptyList()
        applicationsInfo.forEach {
            if (it.packageName in hidden) {
                result.remove(it)
            }
        }
        return result.toList()
    }

    fun getHidden(
        applicationsInfo: List<ApplicationInfo>
    ): List<ApplicationInfo> {
        val result = mutableListOf<ApplicationInfo>()
        val hidden = LauncherData.allApplicationData.value?.filter { it.hidden }?.map { it.packageName }
            ?: emptyList()
        applicationsInfo.forEach {
            if (it.packageName in hidden) {
                result.add(it)
            }
        }
        return result
    }


    fun getNotMaskApplicationData(packageName: String): ApplicationData {
        val applicationInfo =
            context.packageManager.getApplicationInfo(packageName, 0)
        return ApplicationData(
            title = applicationInfo.loadLabel(context.packageManager).toString(),
            image = AppImage(packageName),
            packageName = applicationInfo.packageName
        )
    }
}