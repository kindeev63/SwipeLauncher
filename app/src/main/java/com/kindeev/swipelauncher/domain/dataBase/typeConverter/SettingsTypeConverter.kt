package com.kindeev.swipelauncher.domain.dataBase.typeConverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingValue
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.ClickOnClock

class SettingsTypeConverter {
    private val gson = Gson()

    private val circleMenuTypeConverter = CircleMenuTypeConverter()
   @TypeConverter
    fun fromSettingValue(settingValue: SettingValue): String {
        return if (settingValue is ClickOnClock) {
            gson.toJson(
                SSettingValue(
                    value = gson.toJson(
                        SClickOnClock(
                            enabled = settingValue.enabled,
                            action = circleMenuTypeConverter.fromCircleMenuAction(settingValue.action)
                        )
                    ),
                    type = SClickOnClock::class.java.name
                )
            )
        } else {
            gson.toJson(
                SSettingValue(value = gson.toJson(settingValue), type = settingValue.javaClass.name)
            )
        }
    }

    @TypeConverter
    fun toSettingValue(data: String): SettingValue {
        val settingValueToSave = gson.fromJson(data, SSettingValue::class.java)
        return if (settingValueToSave.type == SClickOnClock::class.java.name) {
            val clickOnClockToSave = gson.fromJson(settingValueToSave.value, SClickOnClock::class.java)
            ClickOnClock(
                enabled = clickOnClockToSave.enabled,
                action = circleMenuTypeConverter.toCircleMenuAction(clickOnClockToSave.action)
            )
        } else {
            val type = Class.forName(settingValueToSave.type)
                .asSubclass(SettingValue::class.java)
            gson.fromJson(settingValueToSave.value, type)
        }
    }

    private class SSettingValue(val value: String, val type: String)

    private class SClickOnClock(val enabled: Boolean, val action: String)
}