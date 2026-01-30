package com.kindeev.swipelauncher.domain.entities.application_data

data class ApplicationData(
    val packageName: String,
    val title: ApplicationDataTitle,
    val image: ApplicationDataImage,
    val hidden: Boolean
)