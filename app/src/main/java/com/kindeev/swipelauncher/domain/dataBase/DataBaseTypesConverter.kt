package com.kindeev.swipelauncher.domain.dataBase

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.kindeev.swipelauncher.domain.entities.settings.ApplicationSetting
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.Call
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenApp
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenCircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.UserImage
class DataBaseTypesConverter {

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
            CircleMenuActionTypes.Call -> Call::class.java
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
}