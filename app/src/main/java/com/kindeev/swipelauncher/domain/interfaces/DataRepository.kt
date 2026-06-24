package com.kindeev.swipelauncher.domain.interfaces

import com.kindeev.swipelauncher.domain.entities.ApplicationData
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.settings.SettingData
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    fun getAllCircleMenus(): Flow<List<CircleMenu>>

    fun getAllSettings(): Flow<List<SettingData>>

    fun getAllApplicationsData(): Flow<List<ApplicationData>>

    suspend fun insertCircleMenu(circleMenu: CircleMenu)

    suspend fun insertCircleMenus(circleMenus: List<CircleMenu>)

    suspend fun deleteCircleMenus(circleMenus: List<CircleMenu>)

    suspend fun insertSettings(settings: List<SettingData>)

    suspend fun insertApplicationData(applicationData: ApplicationData)

    suspend fun insertApplicationsData(applicationsData: List<ApplicationData>)

    suspend fun deleteApplicationData(applicationData: ApplicationData)

    suspend fun deleteApplicationDataByPackageName(packageName: String)

    suspend fun deleteApplicationsData(applicationsData: List<ApplicationData>)

}