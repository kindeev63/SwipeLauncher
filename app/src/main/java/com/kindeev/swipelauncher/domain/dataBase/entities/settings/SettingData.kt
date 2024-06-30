package com.kindeev.swipelauncher.domain.dataBase.entities.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_settings")
data class SettingData(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "name")
    val name: SettingNames,
    @ColumnInfo(name = "value")
    val value: SettingValue,
)
