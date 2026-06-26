package com.kindeev.swipelauncher.data.coil

import android.content.Context
import coil.Coil
import coil.annotation.ExperimentalCoilApi
import coil.memory.MemoryCache
import coil.request.ImageRequest

class CoilLoaderManager(private val context: Context) {
    private val loader = Coil.imageLoader(context)

    fun prefetch(data: Any, key: String) =
        loader.enqueue(
            ImageRequest.Builder(context)
                .data(data)
                .memoryCacheKey(MemoryCache.Key(key))
                .diskCacheKey(key)
                .build()
        )

    fun <T: Any> prefetchMany(data: Collection<T>, key: (T) -> String) =
        data.map { prefetch(it, key(it)) }

    @OptIn(ExperimentalCoilApi::class)
    fun remove(key: String): Boolean {
        val memoryResult = loader.memoryCache?.remove(MemoryCache.Key(key)) ?: true
        val diskResult = loader.diskCache?.remove(key) ?: true
        return memoryResult && diskResult
    }
}