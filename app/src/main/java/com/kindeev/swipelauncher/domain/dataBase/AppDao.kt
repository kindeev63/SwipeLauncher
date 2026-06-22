package com.kindeev.swipelauncher.domain.dataBase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kindeev.swipelauncher.domain.dataBase.entities.ApplicationData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingData
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Query("SELECT * FROM table_menu")
    fun getAllCircleMenu(): Flow<List<CircleMenu>>

    @Insert(CircleMenu::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCircleMenu(circleMenu: CircleMenu)

    @Insert(CircleMenu::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCircleMenus(circleMenus: List<CircleMenu>)

    @Delete(CircleMenu::class)
    suspend fun deleteCircleMenus(circleMenus: List<CircleMenu>)

    @Query("SELECT * FROM table_settings")
    fun getAllSettings(): Flow<List<SettingData>>

    @Insert(SettingData::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(settingData: SettingData)

    @Insert(SettingData::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settingsData: List<SettingData>)

    @Query("SELECT * FROM table_application_data")
    fun getAllApplicationData(): Flow<List<ApplicationData>>

    @Insert(ApplicationData::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplicationsData(applicationsData: List<ApplicationData>)

    @Insert(ApplicationData::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplicationData(applicationData: ApplicationData)

    @Delete(ApplicationData::class)
    suspend fun deleteApplicationData(applicationData: ApplicationData)

    @Delete(ApplicationData::class)
    suspend fun deleteApplicationsData(applicationsData: List<ApplicationData>)

    @Query("DELETE FROM table_application_data WHERE packageName = :packageName")
    suspend fun deleteApplicationDataByPackageName(packageName: String)
}