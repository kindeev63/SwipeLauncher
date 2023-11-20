package com.kindeev.swipelauncher.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import java.io.Serializable

@Entity(tableName = "table_menu")
data class CircleMenu(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    @ColumnInfo(name = "upAction")
    var upAction: CircleMenuAction,
    @ColumnInfo(name = "upBitmap")
    var upImage: CircleMenuImage,
    @ColumnInfo(name = "downAction")
    var downAction: CircleMenuAction,
    @ColumnInfo(name = "downBitmap")
    var downImage: CircleMenuImage,
    @ColumnInfo(name = "rightAction")
    var rightAction: CircleMenuAction,
    @ColumnInfo(name = "rightBitmap")
    var rightImage: CircleMenuImage,
    @ColumnInfo(name = "leftAction")
    var leftAction: CircleMenuAction,
    @ColumnInfo(name = "leftBitmap")
    var leftImage: CircleMenuImage,
): Serializable
