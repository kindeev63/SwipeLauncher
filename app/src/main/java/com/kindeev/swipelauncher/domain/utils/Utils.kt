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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.circleMenuAction.actionTypes.ChangeFlashLightConditionAction
import com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOffAction
import com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOnAction
import com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImages
import com.kindeev.swipelauncher.data.dataBases.entities.settings.SettingData
import com.kindeev.swipelauncher.data.dataBases.entities.settings.SettingNames
import com.kindeev.swipelauncher.data.dataBases.entities.settings.settingValues.BlackTextColorOnWallpaper
import com.kindeev.swipelauncher.domain.entities.actionTypes.AllActionTypes
import com.kindeev.swipelauncher.domain.entities.imageTypes.AllImageTypes
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategories
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategory
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.actionCategoryItem.ActionCategoryItem
import com.kindeev.swipelauncher.domain.entities.imageTypes.ImageType
import com.kindeev.swipelauncher.presentation.receivers.AppsReceiver

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

fun Context.unregisterAppsReceiver(
    appsReceiver: AppsReceiver,
) {
    try {
        this.unregisterReceiver(appsReceiver)
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

fun Context.openApp(packageName: String) {
    val intent =
        this.packageManager.getLaunchIntentForPackage(packageName)
    intent?.let { this.startActivity(it) }
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