package com.kindeev.swipelauncher.data.dataBases.circle_menu_parameters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kindeev.swipelauncher.domain.entities.circle_menu.parameters.CircleMenuItemOffset

class CircleMenuParametersTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun toOffsets(data: String): List<CircleMenuItemOffset> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson<List<String>>(data, type).map {
            it.toOffset()
        }
    }

    @TypeConverter
    fun fromOffsets(data: List<CircleMenuItemOffset>): String {
        return gson.toJson(data.map { "${it.x} : ${it.y}" })
    }

    private fun String.toOffset(): CircleMenuItemOffset {
        val data = this.split(" : ").map { it.toFloat() }
        return CircleMenuItemOffset(
            x = data[0],
            y = data[1]
        )
    }
}