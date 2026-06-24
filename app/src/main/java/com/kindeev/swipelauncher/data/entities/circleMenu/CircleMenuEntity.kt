package com.kindeev.swipelauncher.data.entities.circleMenu

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "circle_menu")
data class CircleMenuEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val items: List<CircleMenuEntityItem>
)