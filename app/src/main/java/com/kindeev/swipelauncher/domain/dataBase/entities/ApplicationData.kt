package com.kindeev.swipelauncher.domain.dataBase.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import java.io.Serializable

@Entity(tableName = "table_application_data")
data class ApplicationData(
    @PrimaryKey(autoGenerate = false)
    val packageName: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "image")
    val image: CircleMenuImage,
    @ColumnInfo(name = "hidden")
    val hidden: Boolean = false
): Serializable
