package com.kindeev.swipelauncher.data.repositories

import com.kindeev.swipelauncher.domain.dataBase.entities.ApplicationData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingData
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(
    private val circleMenuRepository: CircleMenuRepository,
    private val settingsRepository: SettingsRepository,
    private val applicationDataRepository: ApplicationDataRepository
) {

    fun getAllCircleMenus(): Flow<List<CircleMenu>> = circleMenuRepository.getAll()

    fun getAllSettings(): Flow<List<SettingData>> = settingsRepository.getAll()

    fun getAllApplicationsData(): Flow<List<ApplicationData>> = applicationDataRepository.getAll()

    suspend fun insertCircleMenu(circleMenu: CircleMenu) =
        circleMenuRepository.insert(circleMenu)

    suspend fun insertCircleMenus(circleMenus: List<CircleMenu>) =
        circleMenuRepository.insertMany(circleMenus)

    suspend fun deleteCircleMenus(circleMenus: List<CircleMenu>) =
        circleMenuRepository.deleteMany(circleMenus)

    suspend fun insertSettings(settings: List<SettingData>) =
        settingsRepository.insert(settings)

    suspend fun insertApplicationData(applicationData: ApplicationData) =
        applicationDataRepository.insert(applicationData)

    suspend fun insertApplicationsData(applicationsData: List<ApplicationData>) =
        applicationDataRepository.insertMany(applicationsData)

    suspend fun deleteApplicationData(applicationData: ApplicationData) =
        applicationDataRepository.delete(applicationData)

    suspend fun deleteApplicationDataByPackageName(packageName: String) =
        applicationDataRepository.deleteByPackageName(packageName)

    suspend fun deleteApplicationsData(applicationsData: List<ApplicationData>) =
        applicationDataRepository.deleteMany(applicationsData)

}