package com.kindeev.swipelauncher.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kindeev.swipelauncher.data.database.entities.settings.LauncherSettingsTable
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings")
    fun get(): Flow<LauncherSettingsTable?>

    @Insert(LauncherSettingsTable::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LauncherSettingsTable)
}