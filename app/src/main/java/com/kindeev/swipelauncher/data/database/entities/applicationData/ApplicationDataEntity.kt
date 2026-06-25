package com.kindeev.swipelauncher.data.database.entities.applicationData

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.data.database.entities.circleMenu.images.CircleMenuEntityImage

@Entity(tableName = "application_data")
data class ApplicationDataEntity(
    @PrimaryKey
    val packageName: String,
    val title: String,
    val image: CircleMenuEntityImage,
    val hidden: Boolean
)
