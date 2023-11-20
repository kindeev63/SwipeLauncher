package com.kindeev.swipelauncher.domain

import androidx.room.TypeConverter
import com.google.gson.Gson
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
    fun toCircleMenuAction(data: String): CircleMenuAction {
        val gson = Gson()
        val circleMenuActionToSave = gson.fromJson(data, CircleMenuActionToSave::class.java)
        val classOfData = getClassOfActionData(circleMenuActionToSave.type)
        val circleMenuData = gson.fromJson(circleMenuActionToSave.data, classOfData)
        return CircleMenuAction(type = circleMenuActionToSave.type, data = circleMenuData)
    }

    @TypeConverter
    fun fromCircleMenuAction(circleMenuAction: CircleMenuAction): String {
        val gson = Gson()
        val circleMenuData = gson.toJson(circleMenuAction.data)
        val circleMenuActionToSave = CircleMenuActionToSave().apply {
            type = circleMenuAction.type
            data = circleMenuData
        }
        return gson.toJson(circleMenuActionToSave)
    }

    @TypeConverter
    fun toCircleMenuImage(data: String): CircleMenuImage {
        val gson = Gson()
        val circleMenuImageToSave = gson.fromJson(data, CircleMenuImageToSave::class.java)
        val classOfData = getClassOfImageData(circleMenuImageToSave.type)
        val circleMenuData = gson.fromJson(circleMenuImageToSave.data, classOfData)
        return CircleMenuImage(type = circleMenuImageToSave.type, data = circleMenuData)
    }

    @TypeConverter
    fun fromCircleMenuImage(circleMenuImage: CircleMenuImage): String {
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

private class CircleMenuActionToSave {
    var type: CircleMenuActionTypes = CircleMenuActionTypes.NoneAction
    var data: String = ""
}

private class CircleMenuImageToSave {
    var type: CircleMenuImageTypes = CircleMenuImageTypes.NoneImage
    var data: String = ""
}