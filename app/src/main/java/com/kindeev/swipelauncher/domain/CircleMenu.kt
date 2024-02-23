package com.kindeev.swipelauncher.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.data.dataBaseElements.MenuImages
import com.kindeev.swipelauncher.data.dataBaseElements.MenuActions

@Entity(tableName = "table_menu")
data class CircleMenu(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "menuImages")
    var menuImages: MenuImages,
    @ColumnInfo(name = "menuActions")
    var menuActions: MenuActions,
)
