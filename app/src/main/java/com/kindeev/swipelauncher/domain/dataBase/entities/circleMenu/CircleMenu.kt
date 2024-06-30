package com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem

@Entity(tableName = "table_menu")
data class CircleMenu(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "items")
    val items: List<CircleMenuItem>
)