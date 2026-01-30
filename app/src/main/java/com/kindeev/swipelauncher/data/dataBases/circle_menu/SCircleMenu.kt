package com.kindeev.swipelauncher.data.dataBases.circle_menu

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.domain.entities.circle_menu.circleMenuItem.CircleMenuItem

@Entity(tableName = "table_circle_menu")
data class SCircleMenu(
    @PrimaryKey
    val id: Int,
    val title: String,
    val items: List<CircleMenuItem>
)