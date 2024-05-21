package com.kindeev.swipelauncher.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage

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
)
