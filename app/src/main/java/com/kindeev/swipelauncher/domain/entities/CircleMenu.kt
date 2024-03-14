package com.kindeev.swipelauncher.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.domain.dataBase.MenuImages
import com.kindeev.swipelauncher.domain.dataBase.MenuActions

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
