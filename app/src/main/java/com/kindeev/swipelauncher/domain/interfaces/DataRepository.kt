package com.kindeev.swipelauncher.domain.interfaces

import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.settings.SettingData
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    fun getAllCircleMenus(): Flow<List<CircleMenu>>

    fun getAllSettings(): Flow<List<SettingData>>

    suspend fun insertCircleMenu(circleMenu: CircleMenu)

    suspend fun insertCircleMenus(circleMenus: List<CircleMenu>)

    suspend fun deleteCircleMenus(circleMenus: List<CircleMenu>)

    suspend fun insertSettings(settings: List<SettingData>)

}