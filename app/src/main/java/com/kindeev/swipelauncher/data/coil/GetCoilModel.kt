package com.kindeev.swipelauncher.data.coil

import android.content.Context
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.kindeev.swipelauncher.data.userImages.UserImagesRepository
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImage
import com.kindeev.swipelauncher.domain.utils.getResourceId

fun CircleMenuImage.getCoilModel(context: Context): ImageRequest {
    val (data, key) = when (this) {
        is UserImage -> id to UserImagesRepository.diskCacheKey(id)
        is DefaultImage -> data.getResourceId() to "default_image_${data.name}"
        is AppImage -> appIconUri(packageName) to "app_image_$packageName"
        else -> throw IllegalArgumentException("Illegal image type $this")
    }
    return ImageRequest.Builder(context)
        .data(data)
        .memoryCacheKey(MemoryCache.Key(key))
        .diskCacheKey(key)
        .build()
}