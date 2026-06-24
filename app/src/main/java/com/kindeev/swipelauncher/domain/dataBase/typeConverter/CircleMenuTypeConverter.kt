package com.kindeev.swipelauncher.domain.dataBase.typeConverter

import com.google.gson.Gson
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage

class CircleMenuTypeConverter {

    private val gson = Gson()

    fun fromCircleMenuItems(data: List<CircleMenuItem>): String {
        return gson.toJson(data.map { it.fromCircleMenuItem() })
    }

    fun fromCircleMenuImage(data: CircleMenuImage): String {
        return gson.toJson(
            SCircleMenuImage(
                image = gson.toJson(data),
                type = data.javaClass.name
            )
        )
    }

    fun toCircleMenuImage(data: String): CircleMenuImage {
        return gson.fromJson(
            data,
            SCircleMenuImage::class.java
        ).toCircleMenuImage()
    }

    fun fromCircleMenuAction(data: CircleMenuAction): String {
        return gson.toJson(
            SCircleMenuAction(
                action = gson.toJson(data),
                type = data.javaClass.name
            )
        )
    }

    fun toCircleMenuAction(data: String): CircleMenuAction {
        return gson.fromJson(
            data,
            SCircleMenuAction::class.java
        ).toCircleMenuAction()
    }

    private fun CircleMenuItem.fromCircleMenuItem(): String {
        return gson.toJson(
            SCircleMenuItem(
                image = fromCircleMenuImage(image),
                action = fromCircleMenuAction(action)
            )
        )
    }

    private fun SCircleMenuImage.toCircleMenuImage(): CircleMenuImage {
        val type = Class.forName(type)
            .asSubclass(CircleMenuImage::class.java)
        return gson.fromJson(image, type)
    }

    private fun SCircleMenuAction.toCircleMenuAction(): CircleMenuAction {
        val type = Class.forName(type)
            .asSubclass(CircleMenuAction::class.java)
        return gson.fromJson(action, type)
    }

    private data class SCircleMenuItem(val image: String, val action: String)

    private data class SCircleMenuImage(val image: String, val type: String)

    data class SCircleMenuAction(val action: String, val type: String)
}