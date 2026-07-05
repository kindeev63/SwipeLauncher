package com.kindeev.swipelauncher.domain.interfaces

import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.settings.LauncherSettings
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    fun getAllCircleMenus(): Flow<List<CircleMenu>>

    fun getSettings(): Flow<LauncherSettings>

    suspend fun insertCircleMenu(circleMenu: CircleMenu)

    suspend fun insertCircleMenus(circleMenus: List<CircleMenu>)

    suspend fun deleteCircleMenus(circleMenus: List<CircleMenu>)

    suspend fun insertSettings(settings: LauncherSettings)

}