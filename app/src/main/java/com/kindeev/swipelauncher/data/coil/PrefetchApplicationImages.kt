package com.kindeev.swipelauncher.data.coil

fun CoilLoaderManager.prefetchApplicationImages(packageNames: List<String>) =
    packageNames.forEach {
        prefetch(appIconUri(it), "app_image_$it")
    }