package com.kindeev.swipelauncher.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kindeev.swipelauncher.data.database.entities.settings.LauncherSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings")
    fun get(): Flow<LauncherSettingsEntity?>

    @Insert(LauncherSettingsEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LauncherSettingsEntity)
}