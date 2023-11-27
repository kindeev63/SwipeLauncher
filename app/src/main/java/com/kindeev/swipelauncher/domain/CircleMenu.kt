package com.kindeev.swipelauncher.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.data.MenuImages
import com.kindeev.swipelauncher.data.ui.theme.MenuActions
import java.io.Serializable

@Entity(tableName = "table_menu")
data class CircleMenu(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    @ColumnInfo(name = "menuImages")
    var menuImages: MenuImages,
    @ColumnInfo(name = "menuActions")
    var menuActions: MenuActions,
): Serializable
