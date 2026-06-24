package com.kindeev.swipelauncher.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kindeev.swipelauncher.data.entities.settings.LauncherSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings")
    fun getAll(): Flow<List<LauncherSettingsEntity>>

    @Insert(LauncherSettingsEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LauncherSettingsEntity)
}