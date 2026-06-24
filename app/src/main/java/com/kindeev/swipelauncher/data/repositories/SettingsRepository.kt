package com.kindeev.swipelauncher.data.repositories

import com.kindeev.swipelauncher.data.daos.SettingsDao
import com.kindeev.swipelauncher.data.mappers.fromEntity
import com.kindeev.swipelauncher.data.mappers.toEntity
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dao: SettingsDao) {
    fun getAll(): Flow<List<SettingData>> =
        dao.get().map { it.fromEntity() }
    suspend fun insert(settings: List<SettingData>) = dao.insert(settings.toEntity())
}