package com.kindeev.swipelauncher.data.coil

import com.kindeev.swipelauncher.data.userImages.UserImagesRepository
import java.io.File

fun CoilLoaderManager.prefetchUserImages(images: List<Pair<Int, File>>) =
    images.forEach { (id, file) ->
        prefetch(file, UserImagesRepository.cacheKey(id))
    }
