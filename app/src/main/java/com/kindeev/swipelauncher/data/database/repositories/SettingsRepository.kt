package com.kindeev.swipelauncher.data.database.repositories

import com.kindeev.swipelauncher.data.database.daos.SettingsDao
import com.kindeev.swipelauncher.data.database.mappers.fromEntity
import com.kindeev.swipelauncher.data.database.mappers.toEntity
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.settings.LauncherSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dao: SettingsDao) {
    fun getAll(): Flow<LauncherSettings> =
        dao.get().map {
            it?.fromEntity() ?: Constants.defaultSettings.also { default ->
                insert(default)
            }
        }

    suspend fun insert(settings: LauncherSettings) = dao.insert(settings.toEntity())
}