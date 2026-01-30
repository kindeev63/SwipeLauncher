package com.kindeev.swipelauncher.data.dataBases.application_data

import com.kindeev.swipelauncher.domain.database.ApplicationDataRepository
import com.kindeev.swipelauncher.domain.entities.application_data.ApplicationData
import kotlinx.coroutines.flow.map

class ApplicationDataDatabaseManager(
    private val dao: ApplicationDataDao
): ApplicationDataRepository  {

    override fun getAllFlow() = dao.getAllFlow().map { data ->
        data.map { it.toApplicationData() }
    }

    override suspend fun getAll() = dao.getAll().map { it.toApplicationData() }

    override suspend fun insert(applicationsData: List<ApplicationData>) {
        dao.insert(applicationsData.map { it.toSApplicationData() })
    }

    override suspend fun insert(applicationData: ApplicationData) {
        dao.insert(applicationData.toSApplicationData())
    }

    override suspend fun delete(packageName: String) {
        dao.delete(packageName)
    }

    override suspend fun delete(packageNames: List<String>) {
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