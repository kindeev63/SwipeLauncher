package com.kindeev.swipelauncher.data.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.data.ApplicationSetting
import java.io.Serializable

@Entity(tableName = "table_settings")
data class SettingData(
    @PrimaryKey(autoGenerate = false)
    val setting: ApplicationSetting,
    @ColumnInfo(name = "value")
    val value: SettingValue
): Serializable
