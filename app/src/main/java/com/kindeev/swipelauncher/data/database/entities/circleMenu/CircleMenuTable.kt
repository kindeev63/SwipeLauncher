package com.kindeev.swipelauncher.data.database.entities.circleMenu

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.data.entities.CircleMenuItemEntity

@Entity(tableName = "circle_menu")
data class CircleMenuTable(
    @PrimaryKey
    val id: Int,
    val title: String,
    val items: List<CircleMenuItemEntity>
)