package com.kindeev.swipelauncher.data.dataBases.application_data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.kindeev.swipelauncher.domain.entities.application_data.ApplicationDataImage
import com.kindeev.swipelauncher.domain.entities.application_data.ApplicationDataTitle

class ApplicationDataTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromApplicationDataTitle(data: ApplicationDataTitle): String {
        return gson.toJson(
            SApplicationDataTitle(
                title = gson.toJson(data),
                type = data.javaClass.name
            )
        )
    }

    @TypeConverter
    fun toApplicationDataTitle(data: String): ApplicationDataTitle {
        return gson.fromJson(
            data,
            SApplicationDataTitle::class.java
        ).toApplicationDataTitle()
    }

    @TypeConverter
    fun fromApplicationDataImage(data: ApplicationDataImage): String {
        return gson.toJson(
            SApplicationDataImage(
                image = gson.toJson(data),
                type = data.javaClass.name
            )
        )
    }

    @TypeConverter
    fun toApplicationDataImage(data: String): ApplicationDataImage {
        return gson.fromJson(
            data,
            SApplicationDataImage::class.java
        ).toApplicationDataImage()
    }

    private fun SApplicationDataTitle.toApplicationDataTitle(): ApplicationDataTitle {
        val type = Class.forName(type)
            .asSubclass(ApplicationDataTitle::class.java)
        return gson.fromJson(title, type)
    }

    private fun SApplicationDataImage.toApplicationDataImage(): ApplicationDataImage {
        val type = Class.forName(type)
            .asSubclass(ApplicationDataImage::class.java)
        return gson.fromJson(image, type)
    }

    private data class SApplicationDataTitle(val title: String, val type: String)

    private data class SApplicationDataImage(val image: String, val type: String)

}