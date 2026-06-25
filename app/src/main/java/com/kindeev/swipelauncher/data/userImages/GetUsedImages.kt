package com.kindeev.swipelauncher.data.userImages

import com.kindeev.swipelauncher.domain.entities.ApplicationData
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage

@JvmName("getUsedImagesIdsFromCircleMenus")
fun List<CircleMenu>.getUsedImagesIds(): List<Int> =
    flatMap { it.items }
        .map { it.image }
        .filterIsInstance<UserImage>()
        .map { it.id }

@JvmName("getUsedImagesIdsFromApplicationsData")
fun List<ApplicationData>.getUsedImagesIds(): List<Int> =
    map { it.image }
        .filterIsInstance<UserImage>()
        .map { it.id }

fun getUsedImageIds(circleMenus: List<CircleMenu>, applicationsData: List<ApplicationData>): Set<Int> =
    circleMenus.getUsedImagesIds().toSet() union applicationsData.getUsedImagesIds().toSet()