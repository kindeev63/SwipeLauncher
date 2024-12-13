package com.kindeev.swipelauncher.domain.useCases

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.provider.Telephony
import android.telecom.TelecomManager
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenSettingsAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImages

class GetRootCircleMenuUseCase(private val context: Context) {
    fun get(title: String): CircleMenu {
        val items = mutableListOf<CircleMenuItem>()
        getCircleMenuItemByPackageName(getDefaultCameraApp(context))
            ?.let { items.add(it) }
        getCircleMenuItemByPackageName(getDefaultGalleryApp(context))
            ?.let { items.add(it) }
        getCircleMenuItemByPackageName(getDefaultBrowserApp(context))
            ?.let { items.add(it) }
        getCircleMenuItemByPackageName(getDefaultPhoneApp(context))
            ?.let { items.add(it) }
        items.add(
            CircleMenuItem(
                image = DefaultImage(data = DefaultImages.Settings),
                action = OpenSettingsAction
            )
        )
        getCircleMenuItemByPackageName(getDefaultEmailApp(context))
            ?.let { items.add(it) }
        getCircleMenuItemByPackageName(getDefaultSmsApp(context))
            ?.let { items.add(it) }
        getCircleMenuItemByPackageName(getDefaultSettingsApp(context))
            ?.let { items.add(it) }

        return CircleMenu(
            title = title,
            items = items
        )
    }

    private fun getCircleMenuItemByPackageName(packageName: String?): CircleMenuItem? {
        if (packageName == null) return null
        return CircleMenuItem(
            image = AppImage(packageName = packageName),
            action = OpenAppAction(packageName = packageName)
        )
    }

    private fun getDefaultCameraApp(context: Context): String? {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val resolveInfo =
            context.packageManager.resolveActivity(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo != null) {
            return resolveInfo.activityInfo?.packageName
        }
        return null
    }

    @SuppressLint("IntentReset")
    fun getDefaultGalleryApp(context: Context): String? {
        val mainIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        mainIntent.type = "image/*"
        val pkgAppsList =
            context.packageManager.queryIntentActivities(mainIntent, PackageManager.GET_RESOLVED_FILTER)
        if (pkgAppsList.isNotEmpty()) {
            for (resolveInfo in pkgAppsList) {
                return resolveInfo.activityInfo.packageName
            }
        }
        return null
    }

    private fun getDefaultBrowserApp(context: Context): String? {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
        val resolveInfo =
            context.packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo?.activityInfo?.packageName
    }

    private fun getDefaultPhoneApp(context: Context): String? {
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        return telecomManager.defaultDialerPackage
    }

    private fun getDefaultEmailApp(context: Context): String? {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        val resolveInfoList =
            context.packageManager.queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfoList.isNotEmpty()) {
            for (resolveInfo in resolveInfoList) {
                return resolveInfo.activityInfo.packageName
            }
        }
        return null
    }

    private fun getDefaultSmsApp(context: Context): String? {
        return Telephony.Sms.getDefaultSmsPackage(context)
    }

    private fun getDefaultSettingsApp(context: Context): String? {
        val intent = Intent(Settings.ACTION_SETTINGS)
        val resolveInfo =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo.isEmpty()) {
            return null
        }

        return resolveInfo.firstOrNull()?.activityInfo?.packageName
    }
}