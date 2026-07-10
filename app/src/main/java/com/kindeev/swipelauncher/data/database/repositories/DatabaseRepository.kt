package com.kindeev.swipelauncher.data.database.repositories

import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.settings.LauncherSettings
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(
    private val circleMenuRepository: CircleMenuRepository,
    private val settingsRepository: SettingsRepository,
): DataRepository {
    override suspend fun getCircleMenus(): List<CircleMenu> = circleMenuRepository.getCircleMenus()

    override fun getAllCircleMenus(): Flow<List<CircleMenu>> = circleMenuRepository.getAll()

    override fun getSettings(): Flow<LauncherSettings> = settingsRepository.getAll()

    override suspend fun insertCircleMenu(circleMenu: CircleMenu) =
        circleMenuRepository.insert(circleMenu)

    override suspend fun insertCircleMenus(circleMenus: List<CircleMenu>) =
        circleMenuRepository.insertMany(circleMenus)

    override suspend fun deleteCircleMenus(circleMenus: List<CircleMenu>) =
        circleMenuRepository.deleteMany(circleMenus)

    override suspend fun insertSettings(settings: LauncherSettings) =
        settingsRepository.insert(settings)

}