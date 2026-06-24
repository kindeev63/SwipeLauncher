package com.kindeev.swipelauncher.domain.entities

import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage

data class ApplicationData(
    val packageName: String,
    val title: String,
    val image: CircleMenuImage,
    val hidden: Boolean = false
)