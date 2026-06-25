package com.kindeev.swipelauncher.domain.interfaces

import android.net.Uri
import java.io.File

interface UserImagesRepository {
    suspend fun getAllIds(): List<Int>

    fun getFile(id: Int): File

    suspend fun insert(uri: Uri): Int?

    suspend fun delete(id: Int): Boolean

    suspend fun deleteMany(ids: Collection<Int>): Boolean

    suspend fun removeUnused(usedIds: Set<Int>): Boolean

    suspend fun prefetchAll()
}