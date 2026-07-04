package com.kindeev.swipelauncher.data.userImages

import android.net.Uri
import coil.request.Disposable
import com.kindeev.swipelauncher.data.coil.CoilLoaderManager
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class UserImagesRepository(
    private val storage: UserImagesStorage,
    private val coilLoaderManager: CoilLoaderManager
): UserImagesRepository {

    override suspend fun getAllIds(): List<Int> = withContext(Dispatchers.IO) {
        storage.getIds()
    }

    suspend fun getAllFiles(): List<Pair<Int, File>> = withContext(Dispatchers.IO) {
        storage.getIds().map { id -> id to getFile(id) }
    }

    override fun getFile(id: Int): File = storage.getFile(id)

    override suspend fun insert(uri: Uri): Int? = withContext(Dispatchers.IO) {
        findFreeId()
            .takeIf { storage.insert(uri, it) }
            ?.also { prefetch(it) }
    }

    override suspend fun delete(id: Int): Boolean = withContext(Dispatchers.IO) {
        val deleteResult = storage.delete(id)
        val clearCacheResult = coilLoaderManager.remove(cacheKey(id))
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
                coilLoaderManager.prefetch(file, cacheKey(id))
            }

    private fun findFreeId(): Int {
        val ids = storage.getIds().toSet()
        return generateSequence(0) { it + 1 }
            .first { it !in ids }
    }

    companion object {

        fun cacheKey(id: Int): String =
            "user_image_$id"

    }
}