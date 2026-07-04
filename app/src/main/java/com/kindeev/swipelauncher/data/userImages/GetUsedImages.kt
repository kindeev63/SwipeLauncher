package com.kindeev.swipelauncher.data.userImages

import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.UserImage

fun List<CircleMenu>.getUsedImagesIds(): List<Int> =
    flatMap { it.items }
        .map { it.image }
        .filterIsInstance<UserImage>()
        .map { it.id }
