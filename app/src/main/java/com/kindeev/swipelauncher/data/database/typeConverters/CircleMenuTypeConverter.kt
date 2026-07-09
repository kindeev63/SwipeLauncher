package com.kindeev.swipelauncher.data.database.typeConverters

import androidx.room.TypeConverter
import com.kindeev.swipelauncher.data.entities.CircleMenuItemEntity
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class CircleMenuTypeConverter {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromCircleMenuEntityItemList(items: List<CircleMenuItemEntity>): String =
        json.encodeToString(
            serializer = ListSerializer(CircleMenuItemEntity.serializer()),
            value = items
        )

    @TypeConverter
    fun toCircleMenuEntityItemList(data: String): List<CircleMenuItemEntity> =
        json.decodeFromString(
            deserializer = ListSerializer(CircleMenuItemEntity.serializer()),
            string = data
        )
}