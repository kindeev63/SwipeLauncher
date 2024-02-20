package com.kindeev.swipelauncher.domain

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.kindeev.swipelauncher.data.ApplicationSetting
import com.kindeev.swipelauncher.data.dataBaseElements.MenuImages
import com.kindeev.swipelauncher.data.dataBaseElements.MenuActions
import com.kindeev.swipelauncher.data.settings.SettingTypes
import com.kindeev.swipelauncher.data.settings.SettingValue
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.UserImage

class DataBaseTypesConverter {

    @TypeConverter
    fun toMenuImages(data: String): MenuImages {
        val gson = Gson()
        val menuImagesToSave = gson.fromJson(data, MenuImagesToSave::class.java)
        return MenuImages(
            upImage = toCircleMenuImage(menuImagesToSave.upImage),
            downImage = toCircleMenuImage(menuImagesToSave.downImage),
            rightImage = toCircleMenuImage(menuImagesToSave.rightImage),
            leftImage = toCircleMenuImage(menuImagesToSave.leftImage),
        )
    }

    @TypeConverter
    fun fromMenuImages(menuImages: MenuImages): String {
        val gson = Gson()
        val menuImagesToSave = MenuImagesToSave().apply {
            upImage = fromCircleMenuImage(menuImages.upImage)
            downImage = fromCircleMenuImage(menuImages.downImage)
            rightImage = fromCircleMenuImage(menuImages.rightImage)
            leftImage = fromCircleMenuImage(menuImages.leftImage)
        }
        return gson.toJson(menuImagesToSave)
    }

    @TypeConverter
    fun toMenuActions(data: String): MenuActions {
        val gson = Gson()
        val menuActionsToSave = gson.fromJson(data, MenuActionsToSave::class.java)
        return MenuActions(
            upAction = toCircleMenuAction(menuActionsToSave.upAction),
            downAction = toCircleMenuAction(menuActionsToSave.downAction),
            rightAction = toCircleMenuAction(menuActionsToSave.rightAction),
            leftAction = toCircleMenuAction(menuActionsToSave.leftAction),
        )
    }

    @TypeConverter
    fun fromMenuActions(menuActions: MenuActions): String {
        val gson = Gson()
        val menuActionsToSave = MenuActionsToSave().apply {
            upAction = fromCircleMenuAction(menuActions.upAction)
            downAction = fromCircleMenuAction(menuActions.downAction)
            rightAction = fromCircleMenuAction(menuActions.rightAction)
            leftAction = fromCircleMenuAction(menuActions.leftAction)
        }
        return gson.toJson(menuActionsToSave)
    }

    @TypeConverter
    fun toApplicationSetting(data: String): ApplicationSetting {
        return ApplicationSetting.valueOf(data)
    }

    @TypeConverter
    fun fromApplicationSetting(applicationSetting: ApplicationSetting): String {
        return applicationSetting.name
    }

    @TypeConverter
    fun toSettingValue(data: String): SettingValue {
        val gson = Gson()
        val settingValueToSave = gson.fromJson(data, SettingValueToSave::class.java)
        val classOfData = getClassOfSettingData(settingValueToSave.type)
        return SettingValue(
            type = settingValueToSave.type,
            data = gson.fromJson(settingValueToSave.data, classOfData)
        )
    }

    @TypeConverter
    fun fromSettingValue(settingValue: SettingValue): String {
        val gson = Gson()
        val newData = gson.toJson(settingValue.data)
        return gson.toJson(SettingValueToSave().apply {
            type = settingValue.type
            data = newData
        }
        )
    }


    private fun toCircleMenuAction(data: String): CircleMenuAction {
        val gson = Gson()
        val circleMenuActionToSave = gson.fromJson(data, CircleMenuActionToSave::class.java)
        val classOfData = getClassOfActionData(circleMenuActionToSave.type)
        return CircleMenuAction(
            type = circleMenuActionToSave.type,
            data = if (classOfData == null) null else gson.fromJson(
                circleMenuActionToSave.data,
                classOfData
            )
        )
    }

    private fun fromCircleMenuAction(circleMenuAction: CircleMenuAction): String {
        val gson = Gson()
        val circleMenuData = gson.toJson(circleMenuAction.data)
        val circleMenuActionToSave = CircleMenuActionToSave().apply {
            type = circleMenuAction.type
            data = circleMenuData
        }
        return gson.toJson(circleMenuActionToSave)
    }

    private fun toCircleMenuImage(data: String): CircleMenuImage {
        val gson = Gson()
        val circleMenuImageToSave = gson.fromJson(data, CircleMenuImageToSave::class.java)
        val classOfData = getClassOfImageData(circleMenuImageToSave.type)
        val circleMenuData = gson.fromJson(circleMenuImageToSave.data, classOfData)
        return CircleMenuImage(type = circleMenuImageToSave.type, data = circleMenuData)
    }

    private fun fromCircleMenuImage(circleMenuImage: CircleMenuImage): String {
        val gson = Gson()
        val circleMenuData = gson.toJson(circleMenuImage.data)
        val circleMenuImageToSave = CircleMenuImageToSave().apply {
            type = circleMenuImage.type
            data = circleMenuData
        }
        return gson.toJson(circleMenuImageToSave)
    }

    private fun getClassOfActionData(type: CircleMenuActionTypes): Class<*>? {
        return when (type) {
            CircleMenuActionTypes.OpenCircleMenu -> OpenCircleMenu::class.java
            CircleMenuActionTypes.OpenApp -> OpenApp::class.java
            else -> null
        }
    }

    private fun getClassOfImageData(type: CircleMenuImageTypes): Class<*> {
        return when (type) {
            CircleMenuImageTypes.AppImage -> AppImage::class.java
            CircleMenuImageTypes.DefaultImage -> DefaultImage::class.java
            CircleMenuImageTypes.UserImage -> UserImage::class.java
        }
    }

    private fun getClassOfSettingData(type: SettingTypes): Class<*> {
        return when (type) {
            SettingTypes.Switch -> Boolean::class.java
            SettingTypes.Clickable -> Any::class.java
        }
    }
}

private class MenuActionsToSave {
    var upAction = ""
    var downAction = ""
    var rightAction = ""
    var leftAction = ""
}

private class CircleMenuActionToSave {
    var type: CircleMenuActionTypes = CircleMenuActionTypes.OpenSettings
    var data: String = ""
}

private class MenuImagesToSave {
    var upImage = ""
    var downImage = ""
    var rightImage = ""
    var leftImage = ""
}

private class CircleMenuImageToSave {
    var type: CircleMenuImageTypes = CircleMenuImageTypes.DefaultImage
    var data: String = ""
}

private class SettingValueToSave {
    var type: SettingTypes = SettingTypes.Clickable
    var data: String = ""
}