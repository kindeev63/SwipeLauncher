package com.kindeev.swipelauncher.domain.dataBase

import androidx.compose.ui.geometry.Offset
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kindeev.swipelauncher.domain.entities.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.Call
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.Dial
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenApp
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenCircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.UserImage
class DataBaseTypesConverter {

    @TypeConverter
    fun toCircleMenuItems(data: String): List<CircleMenuItem> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson<List<String>>(data, type).map { it.toCircleMenuItem() }
    }

    @TypeConverter
    fun fromCircleMenuItem(items: List<CircleMenuItem>): String {
        return Gson().toJson(items.map { it.fromCircleMenuItem() })
    }



    private fun String.toCircleMenuItem(): CircleMenuItem {
        val gson = Gson()
        val circleMenuItemToSave = gson.fromJson(this, CircleMenuItemToSave::class.java)
        val offset = gson.fromJson(circleMenuItemToSave.offset, Offset::class.java)
        val image = circleMenuItemToSave.image.toCircleMenuImage()
        val action = circleMenuItemToSave.action.toCircleMenuAction()
        return CircleMenuItem(
            offset = offset,
            image = image,
            action = action
        )
    }

    private fun CircleMenuItem.fromCircleMenuItem(): String {
        val gson = Gson()
        val image = this.image.fromCircleMenuImage()
        val action = this.action.fromCircleMenuAction()
        val offset = gson.toJson(this.offset)
        return gson.toJson(
            CircleMenuItemToSave().apply {
                this.image = image
                this.action = action
                this.offset = offset
            }
        )
    }

    private fun String.toCircleMenuAction(): CircleMenuAction {
        val gson = Gson()
        val circleMenuActionToSave = gson.fromJson(this, CircleMenuActionToSave::class.java)
        val classOfData = getClassOfActionData(circleMenuActionToSave.type)
        return CircleMenuAction(
            type = circleMenuActionToSave.type,
            data = if (classOfData == null) null else gson.fromJson(
                circleMenuActionToSave.data,
                classOfData
            )
        )
    }

    private fun CircleMenuAction.fromCircleMenuAction(): String {
        val gson = Gson()
        val type = this.type
        val circleMenuData = gson.toJson(this.data)
        val circleMenuActionToSave = CircleMenuActionToSave().apply {
            this.type = type
            data = circleMenuData
        }
        return gson.toJson(circleMenuActionToSave)
    }

    private fun String.toCircleMenuImage(): CircleMenuImage {
        val gson = Gson()
        val circleMenuImageToSave = gson.fromJson(this, CircleMenuImageToSave::class.java)
        val classOfData = getClassOfImageData(circleMenuImageToSave.type)
        val circleMenuData = gson.fromJson(circleMenuImageToSave.data, classOfData)
        return CircleMenuImage(type = circleMenuImageToSave.type, data = circleMenuData)
    }

    private fun CircleMenuImage.fromCircleMenuImage(): String {
        val gson = Gson()
        val type = this.type
        val circleMenuData = gson.toJson(this.data)
        val circleMenuImageToSave = CircleMenuImageToSave().apply {
            this.type = type
            data = circleMenuData
        }
        return gson.toJson(circleMenuImageToSave)
    }

    private class CircleMenuItemToSave {
        var offset: String = ""
        var image: String = ""
        var action: String = ""
    }

    private class CircleMenuActionToSave {
        var type: CircleMenuActionTypes = CircleMenuActionTypes.OpenSettings
        var data: String = ""
    }

    private class CircleMenuImageToSave {
        var type: CircleMenuImageTypes = CircleMenuImageTypes.DefaultImage
        var data: String = ""
    }

    private fun getClassOfActionData(type: CircleMenuActionTypes): Class<*>? {
        return when (type) {
            CircleMenuActionTypes.OpenCircleMenu -> OpenCircleMenu::class.java
            CircleMenuActionTypes.OpenApp -> OpenApp::class.java
            CircleMenuActionTypes.Call -> Call::class.java
            CircleMenuActionTypes.Dial -> Dial::class.java
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