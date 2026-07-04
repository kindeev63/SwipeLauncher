package com.kindeev.swipelauncher.data.database.repositories

import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(
    private val circleMenuRepository: CircleMenuRepository,
    private val settingsRepository: SettingsRepository,
): DataRepository {

    override fun getAllCircleMenus(): Flow<List<CircleMenu>> = circleMenuRepository.getAll()

    override fun getAllSettings(): Flow<List<SettingData>> = settingsRepository.getAll()

    override suspend fun insertCircleMenu(circleMenu: CircleMenu) =
        circleMenuRepository.insert(circleMenu)

    override suspend fun insertCircleMenus(circleMenus: List<CircleMenu>) =
        circleMenuRepository.insertMany(circleMenus)

    override suspend fun deleteCircleMenus(circleMenus: List<CircleMenu>) =
        circleMenuRepository.deleteMany(circleMenus)

    override suspend fun insertSettings(settings: List<SettingData>) =
        settingsRepository.insert(settings)

}