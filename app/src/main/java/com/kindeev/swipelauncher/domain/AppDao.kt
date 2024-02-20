package com.kindeev.swipelauncher.domain

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kindeev.swipelauncher.data.settings.SettingData

@Dao
interface AppDao {

    @Query("SELECT * FROM table_menu")
    fun getAllCircleMenu(): LiveData<List<CircleMenu>>

    @Insert(CircleMenu::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCircleMenu(circleMenu: CircleMenu)

    @Insert(CircleMenu::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCircleMenus(circleMenus: List<CircleMenu>)

    @Delete(CircleMenu::class)
    suspend fun deleteCircleMenu(circleMenu: CircleMenu)

    @Query("SELECT * FROM table_settings")
    fun getAllSettings(): LiveData<List<SettingData>>

    @Insert(SettingData::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(settingData: SettingData)

    @Insert(SettingData::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settingsData: List<SettingData>)
}