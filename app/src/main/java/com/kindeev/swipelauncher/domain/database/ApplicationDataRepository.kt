package com.kindeev.swipelauncher.domain.database

import com.kindeev.swipelauncher.domain.entities.application_data.ApplicationData
import kotlinx.coroutines.flow.Flow

interface ApplicationDataRepository {

    fun getAllFlow(): Flow<List<ApplicationData>>
    suspend fun getAll(): List<ApplicationData>
    suspend fun insert(applicationsData: List<ApplicationData>)
    suspend fun insert(applicationData: ApplicationData)
    suspend fun delete(packageName: String)
    suspend fun delete(packageNames: List<String>)
}