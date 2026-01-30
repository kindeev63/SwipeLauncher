package com.kindeev.swipelauncher.data.dataBases.application_data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.domain.entities.application_data.ApplicationDataImage
import com.kindeev.swipelauncher.domain.entities.application_data.ApplicationDataTitle

@Entity(tableName = "table_application_data")
data class SApplicationData(
    @PrimaryKey
    val packageName: String,
    val title: ApplicationDataTitle,
    val image: ApplicationDataImage,
    val hidden: Boolean
)