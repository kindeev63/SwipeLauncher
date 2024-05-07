package com.kindeev.swipelauncher.domain.entities.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.domain.deserializableSettingValue
import com.kindeev.swipelauncher.domain.serializableSettingValue

@Entity(tableName = "table_settings")
data class SettingData(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "setting")
    val setting: Setting,
    @ColumnInfo(name = "value")
     val _data: String
) {
    constructor(setting: Setting, value: Any? = null) : this(setting, value.serializableSettingValue())

    val value: Any?
        get() = _data.deserializableSettingValue(setting)
}
