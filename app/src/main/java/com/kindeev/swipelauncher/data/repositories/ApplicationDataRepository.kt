package com.kindeev.swipelauncher.data.repositories

import com.kindeev.swipelauncher.data.daos.ApplicationDataDao
import com.kindeev.swipelauncher.data.mappers.fromEntity
import com.kindeev.swipelauncher.data.mappers.toEntity
import com.kindeev.swipelauncher.domain.dataBase.entities.ApplicationData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ApplicationDataRepository(private val dao: ApplicationDataDao) {

    fun getAll(): Flow<List<ApplicationData>> =
        dao.getAll().map { data -> data.map { it.fromEntity() } }

    suspend fun insert(applicationData: ApplicationData) = dao.insert(applicationData.toEntity())

    suspend fun insertMany(applicationsData: List<ApplicationData>) =
        dao.insertMany(applicationsData.map { it.toEntity() })

    suspend fun delete(applicationData: ApplicationData) = dao.delete(applicationData.toEntity())

    suspend fun deleteMany(applicationsData: List<ApplicationData>) =
        dao.deleteMany(applicationsData.map { it.toEntity() })

    suspend fun deleteByPackageName(packageName: String) = dao.deleteByPackageName(packageName)
}