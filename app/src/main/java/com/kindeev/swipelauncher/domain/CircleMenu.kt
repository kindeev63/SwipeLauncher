package com.kindeev.swipelauncher.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "table_menu")
data class CircleMenu(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo(name = "upAction")
    val upAction: CircleMenuItemAction,
    @ColumnInfo(name = "downAction")
    val downAction: CircleMenuItemAction,
    @ColumnInfo(name = "rightAction")
    val rightAction: CircleMenuItemAction,
    @ColumnInfo(name = "leftAction")
    val leftAction: CircleMenuItemAction
): Serializable
