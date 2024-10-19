package com.kindeev.swipelauncher.domain.utils

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import androidx.activity.SystemBarStyle
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.ApplicationData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.CallAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.ChangeFlashLightConditionAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.DialAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOffAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOnAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenSettingsAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenUrlAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImages
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.BlackTextColorOnWallpaper
import com.kindeev.swipelauncher.domain.entities.actionTypes.AllActionTypes
import com.kindeev.swipelauncher.domain.entities.imageTypes.AllImageTypes
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategories
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategory
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.actionCategoryItem.ActionCategoryItem
import com.kindeev.swipelauncher.presentation.activities.SettingsActivity
import com.kindeev.swipelauncher.domain.entities.imageTypes.ImageType
import com.kindeev.swipelauncher.presentation.entities.searchBox.AppSBR
import com.kindeev.swipelauncher.presentation.entities.searchBox.SearchBoxResult
import com.kindeev.swipelauncher.presentation.receivers.AppsReceiver
import com.kindeev.swipelauncher.presentation.receivers.WallpaperChangeReceiver

fun Context.isMyLauncherDefault(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = this.getSystemService(Context.ROLE_SERVICE) as RoleManager
        roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
                roleManager.isRoleHeld(RoleManager.ROLE_HOME)
    } else {
        val packageManager = this.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        resolveInfo?.activityInfo?.packageName == this.packageName
    }
}

fun Context.showLauncherSelection() {
    val intent = Intent(Settings.ACTION_HOME_SETTINGS)
    if (intent.resolveActivity(this.packageManager) != null) {
        this.startActivity(intent)
    }
}

fun DefaultImages.getResourceId(): Int? {
    return Constants.defaultImages[this]
}

fun Context.registerAppsReceiver(appsReceiver: AppsReceiver) {
    val filter = IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addDataScheme("package")
    }
    this.registerReceiver(appsReceiver, filter)
}

fun Context.unregisterReceivers(
    appsReceiver: AppsReceiver,
    wallpaperChangeReceiver: WallpaperChangeReceiver
) {
    try {
        this.unregisterReceiver(appsReceiver)
        this.unregisterReceiver(wallpaperChangeReceiver)
    } catch (_: Exception) {
    }
}

fun Context.setActionAndImageTypes() {
    Constants.actionCategories = listOf(
        ActionCategory(
            name = this.resources.getString(R.string.open_app_action),
            imageResId = R.drawable.open_app_image,
            type = ActionCategories.OpenApp
        ),
        ActionCategory(
            name = this.resources.getString(R.string.open_circle_menu_action),
            imageResId = R.drawable.open_circle_menu_image,
            type = ActionCategories.OpenCircleMenu
        ),
        ActionCategory(
            name = this.resources.getString(R.string.telephone_action),
            imageResId = R.drawable.telephone_image,
            type = ActionCategories.Telephone
        ),
        ActionCategory(
            name = this.resources.getString(R.string.flashlight_action),
            imageResId = R.drawable.flashlight_action,
            type = ActionCategories.Flashlight
        ),
        ActionCategory(
            name = this.resources.getString(R.string.open_settings_action),
            imageResId = R.drawable.open_settings_image,
            type = ActionCategories.OpenSettings
        ),
        ActionCategory(
            name = this.resources.getString(R.string.open_url_action),
            imageResId = R.drawable.open_url_image,
            type = ActionCategories.OpenUrl
        ),
    )
    Constants.flashlightActionCategoryItems = listOf(
        ActionCategoryItem(
            name = this.resources.getString(R.string.on_flashlight_action),
            imageResId = R.drawable.on_flashlight_image,
            type = AllActionTypes.FlashLightOn
        ),
        ActionCategoryItem(
            name = this.resources.getString(R.string.off_flashlight_action),
            imageResId = R.drawable.off_flashlight_image,
            type = AllActionTypes.FlashLightOff
        ),
        ActionCategoryItem(
            name = this.resources.getString(R.string.change_condition_flashlight_action),
            imageResId = R.drawable.change_condition_flashlight_image,
            type = AllActionTypes.ChangeFlashLightCondition
        ),
    )
    Constants.telephoneActionCategoryItems = listOf(
        ActionCategoryItem(
            name = this.resources.getString(R.string.call_telephone_action),
            imageResId = R.drawable.call_telephone_image,
            type = AllActionTypes.Call
        ),
        ActionCategoryItem(
            name = this.resources.getString(R.string.dial_telephone_action),
            imageResId = R.drawable.dial_telephone_image,
            type = AllActionTypes.Dial
        ),
    )
    Constants.imageTypes = listOf(
        ImageType(
            name = this.resources.getString(R.string.app_image),
            imageResId = R.drawable.app_image,
            type = AllImageTypes.AppImage
        ),
        ImageType(
            name = this.resources.getString(R.string.default_image),
            imageResId = R.drawable.default_image,
            type = AllImageTypes.DefaultImage
        ),
        ImageType(
            name = this.resources.getString(R.string.user_image),
            imageResId = R.drawable.user_image,
            type = AllImageTypes.UserImage
        ),
    )
}

fun CircleMenuAction.getCategory(): ActionCategory? {
    return when (this) {
        is OpenCircleMenuAction -> Constants.actionCategories.find { it.type == ActionCategories.OpenCircleMenu }
        is OpenSettingsAction -> Constants.actionCategories.find { it.type == ActionCategories.OpenSettings }
        is OpenAppAction -> Constants.actionCategories.find { it.type == ActionCategories.OpenApp }
        is FlashLightOnAction -> Constants.actionCategories.find { it.type == ActionCategories.Flashlight }
        is FlashLightOffAction -> Constants.actionCategories.find { it.type == ActionCategories.Flashlight }
        is ChangeFlashLightConditionAction -> Constants.actionCategories.find { it.type == ActionCategories.Flashlight }
        is CallAction -> Constants.actionCategories.find { it.type == ActionCategories.Telephone }
        is DialAction -> Constants.actionCategories.find { it.type == ActionCategories.Telephone }
        is OpenUrlAction -> Constants.actionCategories.find { it.type == ActionCategories.OpenUrl }
        else -> null
    }
}

fun CircleMenuImage.getImageType(): ImageType? {
    return when (this) {
        is AppImage -> Constants.imageTypes.find { it.type == AllImageTypes.AppImage }
        is DefaultImage -> Constants.imageTypes.find { it.type == AllImageTypes.DefaultImage }
        is UserImage -> Constants.imageTypes.find { it.type == AllImageTypes.UserImage }
        else -> null
    }
}

fun String.formatPhoneNumber(): String {
    return if (this.length == 11) {
        "${this[0]} (${this.substring(1, 4)}) ${
            this.substring(4, 7)
        }-${this.substring(7, 9)}-${this.substring(9)}"
    } else this
}

fun Context.getContactName(phoneNumber: String): String? {
    val uri = Uri.withAppendedPath(
        ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
        Uri.encode(phoneNumber)
    )
    val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
    val cursor = contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            return cursor.getString(0)
        }
    }
    return null
}

fun Context.getMinScreenLength(): Float {
    return minOf(
        resources.configuration.screenWidthDp,
        resources.configuration.screenHeightDp
    ).toFloat()
}

@Composable
fun getMinScreenLengthDp(): Dp {
    val configuration = LocalConfiguration.current
    return minOf(configuration.screenWidthDp, configuration.screenHeightDp).dp
}

@Composable
fun getMinScreenLengthSp(): TextUnit {
    val configuration = LocalConfiguration.current
    return minOf(configuration.screenWidthDp, configuration.screenHeightDp).sp
}

fun <T> List<SettingData>.getValueOf(name: SettingNames, classOfT: Class<T>): T? {
    @Suppress("UNCHECKED_CAST")
    return this.find { it.name == name }?.value as T?
}

fun getLauncherStatusBarStyle(): SystemBarStyle {
    return if (LauncherData.settings.value?.getValueOf(
            SettingNames.BlackTextColorOnWallpaper,
            BlackTextColorOnWallpaper::class.java
        )?.enabled == true
    ) SystemBarStyle.light(
        Color.TRANSPARENT,
        Color.TRANSPARENT
    ) else SystemBarStyle.dark(Color.TRANSPARENT)
}

fun Offset.getItemOffset(menuSize: Float): Offset {
    val itemSize = menuSize / 5
    val x = (menuSize / 2 - itemSize / 2) + this.x * menuSize / 10
    val y = (menuSize / 2 - itemSize / 2) + this.y * menuSize / 10
    return Offset(x, y)
}

fun Offset.getSelectedBoxOffset(menuSize: Float): Offset {
    val itemSize = menuSize / 4
    val x = (menuSize / 2 - itemSize / 2) + this.x * menuSize / 10
    val y = (menuSize / 2 - itemSize / 2) + this.y * menuSize / 10
    return Offset(x, y)
}

fun Context.openApp(packageName: String) {
    val intent =
        this.packageManager.getLaunchIntentForPackage(packageName)
    intent?.let { this.startActivity(it) }
}

fun Context.executeSearchResult(result: SearchBoxResult) {
    when (result) {
        is AppSBR -> {
            if (result.applicationInfo.packageName == packageName) {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            } else {
                openApp(result.applicationInfo.packageName)
            }
        }
    }
}

fun Context.getNotMaskApplicationData(packageName: String): ApplicationData {
    val applicationInfo =
        packageManager.getApplicationInfo(packageName, 0)
    return ApplicationData(
        title = applicationInfo.loadLabel(packageManager).toString(),
        image = AppImage(packageName),
        packageName = applicationInfo.packageName
    )
}

fun AllActionTypes.getFlashlightAction(): CircleMenuAction {
    return when (this) {
        AllActionTypes.FlashLightOn -> FlashLightOnAction
        AllActionTypes.FlashLightOff -> FlashLightOffAction
        AllActionTypes.ChangeFlashLightCondition -> ChangeFlashLightConditionAction
        else -> throw IllegalArgumentException("Illegal flashlight action")
    }
}

suspend fun List<SettingData>.checkSettings() {
    val settingNames = this.map { it.name }
    val newSettings = mutableListOf<SettingData>()
    Constants.defaultSettings.forEach { defaultSetting ->
        if (defaultSetting.name !in settingNames) {
            newSettings.add(defaultSetting)
        }
    }
    if (newSettings.isNotEmpty()) {
        LauncherData.insertSettings(newSettings)
    }
}

fun LazyListScope.spacer() {
    item { Spacer(modifier = Modifier.height(5.dp)) }
}

fun Context.getTimeText(minutes: Int): String {
    if (minutes >= 60) {
        return "${minutes / 60} ${resources.getString(R.string.hours)} ${minutes % 60} ${resources.getString(R.string.minutes)}"
    }
    return "$minutes ${resources.getString(R.string.minutes)}"
}