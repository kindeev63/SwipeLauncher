package com.kindeev.swipelauncher.data.repositories

import com.kindeev.swipelauncher.domain.dataBase.entities.ApplicationData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(
    private val circleMenuRepository: CircleMenuRepository,
    private val settingsRepository: SettingsRepository,
    private val applicationDataRepository: ApplicationDataRepository
): DataRepository {

    override fun getAllCircleMenus(): Flow<List<CircleMenu>> = circleMenuRepository.getAll()

    override fun getAllSettings(): Flow<List<SettingData>> = settingsRepository.getAll()

    override fun getAllApplicationsData(): Flow<List<ApplicationData>> = applicationDataRepository.getAll()

    override suspend fun insertCircleMenu(circleMenu: CircleMenu) =
        circleMenuRepository.insert(circleMenu)

    override suspend fun insertCircleMenus(circleMenus: List<CircleMenu>) =
        circleMenuRepository.insertMany(circleMenus)

    override suspend fun deleteCircleMenus(circleMenus: List<CircleMenu>) =
        circleMenuRepository.deleteMany(circleMenus)

    override suspend fun insertSettings(settings: List<SettingData>) =
        settingsRepository.insert(settings)

    override suspend fun insertApplicationData(applicationData: ApplicationData) =
        applicationDataRepository.insert(applicationData)

    override suspend fun insertApplicationsData(applicationsData: List<ApplicationData>) =
        applicationDataRepository.insertMany(applicationsData)

    override suspend fun deleteApplicationData(applicationData: ApplicationData) =
        applicationDataRepository.delete(applicationData)

    override suspend fun deleteApplicationDataByPackageName(packageName: String) =
        applicationDataRepository.deleteByPackageName(packageName)

    override suspend fun deleteApplicationsData(applicationsData: List<ApplicationData>) =
        applicationDataRepository.deleteMany(applicationsData)

}