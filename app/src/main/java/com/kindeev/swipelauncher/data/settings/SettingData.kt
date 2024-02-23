package com.kindeev.swipelauncher.data.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.data.DataObject.deserializableSettingData
import com.kindeev.swipelauncher.data.DataObject.serializableSettingData

@Entity(tableName = "table_settings")
data class SettingData(
    @PrimaryKey(autoGenerate = false)
    val setting: ApplicationSetting,
    @ColumnInfo(name = "data")
    val data: String
) {
    constructor(setting: ApplicationSetting, data: Any? = null) : this(setting, data.serializableSettingData())

    fun getObjectData(): Any? {
        return data.deserializableSettingData(setting)
    }
}
