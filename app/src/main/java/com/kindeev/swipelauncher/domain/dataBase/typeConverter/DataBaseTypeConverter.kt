package com.kindeev.swipelauncher.domain.dataBase.typeConverter

import androidx.compose.ui.geometry.Offset
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem
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
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingValue
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.BlackTextColorOnWallpaper
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.ClickOnClock
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.OpenLastApp
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.PickAppActionWithImage
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.HomeScreenWallpaperChange
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.LockScreenWallpaperChange
import com.kindeev.swipelauncher.domain.entities.actionTypes.AllActionTypes
import com.kindeev.swipelauncher.domain.entities.imageTypes.AllImageTypes

class DataBaseTypeConverter {
    private val gson = Gson()
    @TypeConverter
    fun toCircleMenuItems(data: String): List<CircleMenuItem> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson<List<String>>(data, type).map { it.toCircleMenuItem() }
    }

    @TypeConverter
    fun fromCircleMenuItems(items: List<CircleMenuItem>): String {
        return gson.toJson(items.map { it.fromCircleMenuItem() })
    }

    private fun CircleMenuItem.fromCircleMenuItem(): String {
        val image = fromCircleMenuImage(image)
        val action = fromCircleMenuAction(action)
        val offset = gson.toJson(this.offset)
        return gson.toJson(
            CircleMenuItemToSave(
                offset = offset,
                image = image,
                action = action
            )
        )
    }

    @TypeConverter
    fun fromCircleMenuImage(circleMenuImage: CircleMenuImage): String {
        return gson.toJson(
            CircleMenuImageToSave(
                type = circleMenuImage.getType(),
                data = gson.toJson(circleMenuImage)
            ),
            CircleMenuImageToSave::class.java
        )
    }

    @TypeConverter
    fun toCircleMenuImage(data: String): CircleMenuImage {
        val circleMenuImageToSave = gson.fromJson(data, CircleMenuImageToSave::class.java)
        return gson.fromJson(circleMenuImageToSave.data, circleMenuImageToSave.type.getClass()) as CircleMenuImage
    }

    @TypeConverter
    fun fromCircleMenuAction(circleMenuAction: CircleMenuAction) : String {
        return gson.toJson(
            CircleMenuActionToSave(
                type = circleMenuAction.getType(),
                data = gson.toJson(circleMenuAction)
            ),
            CircleMenuActionToSave::class.java
        )
    }

    @TypeConverter
    fun toCircleMenuAction(data: String): CircleMenuAction {
        val circleMenuActionToSave = gson.fromJson(data, CircleMenuActionToSave::class.java)
        return gson.fromJson(circleMenuActionToSave.data, circleMenuActionToSave.type.getClass()) as CircleMenuAction
    }

    private fun String.toCircleMenuItem(): CircleMenuItem {
        val circleMenuItemToSave = gson.fromJson(this, CircleMenuItemToSave::class.java)
        val offset = gson.fromJson(circleMenuItemToSave.offset, Offset::class.java)
        val image = toCircleMenuImage(circleMenuItemToSave.image)
        val action = toCircleMenuAction(circleMenuItemToSave.action)
        return CircleMenuItem(
            offset = offset,
            image = image,
            action = action
        )
    }

    private fun CircleMenuAction.getType(): AllActionTypes {
        return when (this) {
            is CallAction -> AllActionTypes.Call
            is ChangeFlashLightConditionAction -> AllActionTypes.ChangeFlashLightCondition
            is DialAction -> AllActionTypes.Dial
            is FlashLightOffAction -> AllActionTypes.FlashLightOff
            is FlashLightOnAction -> AllActionTypes.FlashLightOn
            is OpenAppAction -> AllActionTypes.OpenApp
            is OpenCircleMenuAction -> AllActionTypes.OpenCircleMenu
            is OpenSettingsAction -> AllActionTypes.OpenSettings
            is OpenUrlAction -> AllActionTypes.OpenUrl

            else -> throw IllegalArgumentException("Illegal CircleMenuAction")
        }
    }

    private fun CircleMenuImage.getType(): AllImageTypes {
        return when (this) {
            is DefaultImage -> AllImageTypes.DefaultImage
            is AppImage -> AllImageTypes.AppImage
            is UserImage -> AllImageTypes.UserImage

            else -> throw IllegalArgumentException("Illegal CircleMenuImage")
        }
    }

    private fun AllActionTypes.getClass(): Class<*> {
        return when (this) {
            AllActionTypes.OpenCircleMenu -> OpenCircleMenuAction::class.java
            AllActionTypes.OpenSettings -> OpenSettingsAction::class.java
            AllActionTypes.OpenApp -> OpenAppAction::class.java
            AllActionTypes.FlashLightOn -> FlashLightOnAction::class.java
            AllActionTypes.FlashLightOff -> FlashLightOffAction::class.java
            AllActionTypes.ChangeFlashLightCondition -> ChangeFlashLightConditionAction::class.java
            AllActionTypes.Call -> CallAction::class.java
            AllActionTypes.Dial -> DialAction::class.java
            AllActionTypes.OpenUrl -> OpenUrlAction::class.java
        }
    }

    private fun AllImageTypes.getClass(): Class<*> {
        return when (this) {
            AllImageTypes.AppImage -> AppImage::class.java
            AllImageTypes.DefaultImage -> DefaultImage::class.java
            AllImageTypes.UserImage -> UserImage::class.java
        }
    }

    private class CircleMenuItemToSave(val offset: String, val image: String, val action: String)

    private class CircleMenuActionToSave(val type: AllActionTypes, val data: String)

    private class CircleMenuImageToSave(val type: AllImageTypes, val data: String)

    @TypeConverter
    fun fromSettingValue(settingValue: SettingValue): String {
        return if (settingValue is ClickOnClock) {
            gson.toJson(
                SettingValueToSave(name = SettingNames.ClickOnClock, value = gson.toJson(ClickOnClockToSave(enabled = settingValue.enabled, action = fromCircleMenuAction(settingValue.action))))
            )
        } else {
            gson.toJson(
                SettingValueToSave(name = settingValue.getSettingName(), value = gson.toJson(settingValue))
            )
        }
    }

    @TypeConverter
    fun toSettingValue(data: String): SettingValue {
        val settingValueToSave = gson.fromJson(data, SettingValueToSave::class.java)
        return if (settingValueToSave.name == SettingNames.ClickOnClock) {
            val clickOnClockToSave = gson.fromJson(settingValueToSave.value, ClickOnClockToSave::class.java)
            ClickOnClock(
                enabled = clickOnClockToSave.enabled,
                action = toCircleMenuAction(clickOnClockToSave.action)
            )
        } else {
            gson.fromJson(settingValueToSave.value, settingValueToSave.name.getClassOfSettingData()) as SettingValue
        }
    }

    private fun SettingNames.getClassOfSettingData(): Class<*> {
        return when (this) {
            SettingNames.OpenLastApp -> OpenLastApp::class.java
            SettingNames.ClickOnClock -> ClickOnClock::class.java
            SettingNames.BlackTextColorOnWallpaper -> BlackTextColorOnWallpaper::class.java
            SettingNames.PickAppActionWithImage -> PickAppActionWithImage::class.java
            SettingNames.HomeScreenWallpaperChange -> HomeScreenWallpaperChange::class.java
            SettingNames.LockScreenWallpaperChange -> LockScreenWallpaperChange::class.java
        }
    }

    private fun SettingValue.getSettingName(): SettingNames {
        return when (this) {
            is BlackTextColorOnWallpaper -> SettingNames.BlackTextColorOnWallpaper
            is ClickOnClock -> SettingNames.ClickOnClock
            is OpenLastApp -> SettingNames.OpenLastApp
            is PickAppActionWithImage -> SettingNames.PickAppActionWithImage
            is HomeScreenWallpaperChange -> SettingNames.HomeScreenWallpaperChange
            is LockScreenWallpaperChange -> SettingNames.LockScreenWallpaperChange
            else -> throw IllegalArgumentException("Illegal setting")
        }
    }

    private class SettingValueToSave(val name: SettingNames, val value: String)

    private class ClickOnClockToSave(val enabled: Boolean, val action: String)
}