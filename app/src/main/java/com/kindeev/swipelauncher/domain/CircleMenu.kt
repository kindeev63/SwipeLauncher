package com.kindeev.swipelauncher.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "table_menu")
data class CircleMenu(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    @ColumnInfo(name = "directionUp")
    var directionUp: CircleMenuDirection,
    @ColumnInfo(name = "directionDown")
    var directionDown: CircleMenuDirection,
    @ColumnInfo(name = "directionRight")
    var directionRight: CircleMenuDirection,
    @ColumnInfo(name = "directionLeft")
    var directionLeft: CircleMenuDirection,
): Serializable
