package com.kindeev.swipelauncher.data.userImages

import android.content.Context
import android.net.Uri
import coil.Coil
import coil.annotation.ExperimentalCoilApi
import coil.memory.MemoryCache
import coil.request.Disposable
import coil.request.ImageRequest
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class UserImagesRepository(
    private val storage: UserImagesStorage,
    private val context: Context
): UserImagesRepository {
    private val loader = Coil.imageLoader(context)

    override suspend fun getAllIds(): List<Int> = withContext(Dispatchers.IO) {
        storage.getIds()
    }

    override fun getFile(id: Int): File = storage.getFile(id)

    override suspend fun insert(uri: Uri): Int? = withContext(Dispatchers.IO) {
        findFreeId()
            .takeIf { storage.insert(uri, it) }
            ?.also { prefetch(it) }
    }

    override suspend fun delete(id: Int): Boolean = withContext(Dispatchers.IO) {
        val deleteResult = storage.delete(id)
        val clearCacheResult = removeFromCache(id)
        return@withContext deleteResult && clearCacheResult
    }

    override suspend fun deleteMany(ids: Collection<Int>): Boolean = withContext(Dispatchers.IO) {
        ids.map { delete(it) }.all { it }
    }

    override suspend fun removeUnused(usedIds: Set<Int>): Boolean = withContext(Dispatchers.IO) {
        deleteMany(getAllIds().toSet() - usedIds)
    }

    override suspend fun prefetchAll(): Unit = withContext(Dispatchers.IO) {
        getAllIds().forEach { prefetch(it) }
    }

    private fun prefetch(id: Int): Disposable? =
        storage.getFile(id)
            .takeIf { it.exists() }
            ?.let { file ->
                loader.enqueue(
                    ImageRequest.Builder(context)
                        .data(file)
                        .memoryCacheKey(memoryCacheKey(id))
                        .diskCacheKey(diskCacheKey(id))
                        .build()
                )
            }

    private fun findFreeId(): Int {
        val ids = storage.getIds().toSet()
        return generateSequence(0) { it + 1 }
            .first { it !in ids }
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun removeFromCache(id: Int): Boolean {
        val memoryResult = loader.memoryCache?.remove(memoryCacheKey(id)) ?: true
        val diskResult = loader.diskCache?.remove(diskCacheKey(id)) ?: true
        return memoryResult && diskResult
    }

    companion object {

        fun diskCacheKey(id: Int): String =
            "user_image_$id"

        fun memoryCacheKey(id: Int): MemoryCache.Key =
            MemoryCache.Key(diskCacheKey(id))

    }
}