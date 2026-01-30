package com.kindeev.swipelauncher.domain.useCases.database

import com.kindeev.swipelauncher.data.dataBases.application_data.SApplicationData
import com.kindeev.swipelauncher.domain.entities.application_data.ApplicationData
import com.kindeev.swipelauncher.data.dataBases.application_data.ApplicationDataDao
import kotlinx.coroutines.flow.map

class ApplicationDataDatabaseUseCase(
    private val dao: ApplicationDataDao
)  {

    fun getAllFlow() = dao.getAllFlow().map { data ->
        data.map { it.toApplicationData() }
    }

    suspend fun getAll() = dao.getAll().map { it.toApplicationData() }

    suspend fun insert(applicationsData: List<ApplicationData>) {
        dao.insert(applicationsData.map { it.toSApplicationData() })
    }

    suspend fun insert(applicationData: ApplicationData) {
        dao.insert(applicationData.toSApplicationData())
    }

    suspend fun delete(packageName: String) {
        dao.delete(packageName)
    }

    suspend fun delete(packageNames: List<String>) {
        dao.delete(packageNames)
    }
    private fun SApplicationData.toApplicationData(): ApplicationData {
        return ApplicationData(
            packageName = packageName,
            title = title,
            image = image,
            hidden = hidden
        )
    }

    private fun ApplicationData.toSApplicationData(): SApplicationData {
        return SApplicationData(
            packageName = packageName,
            title = title,
            image = image,
            hidden = hidden
        )
    }
}