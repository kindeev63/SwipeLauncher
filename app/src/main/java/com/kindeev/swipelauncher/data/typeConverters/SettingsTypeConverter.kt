package com.kindeev.swipelauncher.data.typeConverters

import androidx.room.TypeConverter
import com.kindeev.swipelauncher.data.entities.settings.ClickOnClockEntity
import kotlinx.serialization.json.Json

class SettingsTypeConverter {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @TypeConverter
    fun fromClickOnClockEntity(value: ClickOnClockEntity): String =
        json.encodeToString(ClickOnClockEntity.serializer(), value)

    @TypeConverter
    fun toClickOnClockEntity(data: String): ClickOnClockEntity =
        json.decodeFromString(ClickOnClockEntity.serializer(), data)
}