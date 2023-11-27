package com.kindeev.swipelauncher.domain

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.kindeev.swipelauncher.data.MenuImages
import com.kindeev.swipelauncher.data.ui.theme.MenuActions
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.NoneAction
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.NoneImage

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

    private fun toCircleMenuAction(data: String): CircleMenuAction {
        val gson = Gson()
        val circleMenuActionToSave = gson.fromJson(data, CircleMenuActionToSave::class.java)
        val classOfData = getClassOfActionData(circleMenuActionToSave.type)
        val circleMenuData = gson.fromJson(circleMenuActionToSave.data, classOfData)
        return CircleMenuAction(type = circleMenuActionToSave.type, data = circleMenuData)
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

    private fun getClassOfActionData(type: CircleMenuActionTypes): Class<*> {
        return when(type) {
            CircleMenuActionTypes.NoneAction -> NoneAction::class.java
            CircleMenuActionTypes.OpenCircleMenu -> OpenCircleMenu::class.java
            CircleMenuActionTypes.OpenSettings -> NoneAction::class.java
        }
    }

    private fun getClassOfImageData(type: CircleMenuImageTypes): Class<*> {
        return when(type) {
            CircleMenuImageTypes.NoneImage -> NoneImage::class.java
            CircleMenuImageTypes.AppImage -> NoneImage::class.java
            CircleMenuImageTypes.DefaultImage -> DefaultImage::class.java
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
    var type: CircleMenuActionTypes = CircleMenuActionTypes.NoneAction
    var data: String = ""
}

private class MenuImagesToSave {
    var upImage = ""
    var downImage = ""
    var rightImage = ""
    var leftImage = ""
}
private class CircleMenuImageToSave {
    var type: CircleMenuImageTypes = CircleMenuImageTypes.NoneImage
    var data: String = ""
}