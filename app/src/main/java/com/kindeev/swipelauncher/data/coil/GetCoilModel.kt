package com.kindeev.swipelauncher.data.coil

import android.content.Context
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.kindeev.swipelauncher.data.userImages.UserImagesRepository
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.UserImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImage
import com.kindeev.swipelauncher.domain.utils.getResourceId

fun CircleMenuImage.getCoilModel(context: Context): ImageRequest {
    val (data, key) = when (this) {
        is UserImage -> id to UserImagesRepository.cacheKey(id)
        is DefaultImage -> data.getResourceId() to "default_image_${data.name}"
        is AppImage -> appImageUri(packageName) to "app_image_$packageName"
    }
    return ImageRequest.Builder(context)
        .data(data)
        .memoryCacheKey(MemoryCache.Key(key))
        .diskCacheKey(key)
        .build()
}