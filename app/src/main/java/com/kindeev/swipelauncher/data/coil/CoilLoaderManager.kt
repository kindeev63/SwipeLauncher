package com.kindeev.swipelauncher.data.coil

import android.content.Context
import coil.Coil
import coil.annotation.ExperimentalCoilApi
import coil.memory.MemoryCache
import coil.request.ImageRequest

class CoilLoaderManager(context: Context) {

    private val appContext = context.applicationContext
    private val loader = Coil.imageLoader(appContext)

    init {
        initCoil(context)
    }
    fun prefetch(data: Any, key: String) =
        loader.enqueue(
            ImageRequest.Builder(appContext)
                .data(data)
                .memoryCacheKey(MemoryCache.Key(key))
                .diskCacheKey(key)
                .build()
        )

    @OptIn(ExperimentalCoilApi::class)
    fun remove(key: String): Boolean {
        val memoryResult = loader.memoryCache?.remove(MemoryCache.Key(key)) ?: true
        val diskResult = loader.diskCache?.remove(key) ?: true
        return memoryResult && diskResult
    }
}