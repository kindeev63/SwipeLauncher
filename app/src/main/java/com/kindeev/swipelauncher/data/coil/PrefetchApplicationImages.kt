package com.kindeev.swipelauncher.data.coil

fun CoilLoaderManager.prefetchApplicationImages(packageNames: List<String>) =
    packageNames.forEach {
        prefetch(appImageUri(it), "app_image_$it")
    }