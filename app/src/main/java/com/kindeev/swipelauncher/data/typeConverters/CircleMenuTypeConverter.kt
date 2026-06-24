package com.kindeev.swipelauncher.data.typeConverters

import androidx.room.TypeConverter
import com.kindeev.swipelauncher.data.entities.circleMenu.CircleMenuEntityItem
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class CircleMenuTypeConverter {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromCircleMenuEntityItemList(items: List<CircleMenuEntityItem>): String =
        json.encodeToString(
            serializer = ListSerializer(CircleMenuEntityItem.serializer()),
            value = items
        )

    @TypeConverter
    fun toCircleMenuEntityItemList(data: String): List<CircleMenuEntityItem> =
        json.decodeFromString(
            deserializer = ListSerializer(CircleMenuEntityItem.serializer()),
            string = data
        )
}