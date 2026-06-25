package com.kindeev.swipelauncher.data.database.typeConverters

import androidx.room.TypeConverter
import com.kindeev.swipelauncher.data.database.entities.circleMenu.images.CircleMenuEntityImage
import kotlinx.serialization.json.Json

class ApplicationDataTypeConverter {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromCircleMenuEntityImage(image: CircleMenuEntityImage): String =
        json.encodeToString(CircleMenuEntityImage.serializer(), image)

    @TypeConverter
    fun toCircleMenuEntityImage(data: String): CircleMenuEntityImage =
        json.decodeFromString(CircleMenuEntityImage.serializer(), data)
}