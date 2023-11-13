package com.kindeev.swipelauncher.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "table_menu")
data class CircleMenu(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    @ColumnInfo(name = "upAction")
    var upAction: String,
    @ColumnInfo(name = "upBitmap")
    var upImage: String,
    @ColumnInfo(name = "downAction")
    var downAction: String,
    @ColumnInfo(name = "downBitmap")
    var downImage: String,
    @ColumnInfo(name = "rightAction")
    var rightAction: String,
    @ColumnInfo(name = "rightBitmap")
    var rightImage: String,
    @ColumnInfo(name = "leftAction")
    var leftAction: String,
    @ColumnInfo(name = "leftBitmap")
    var leftImage: String,
): Serializable
