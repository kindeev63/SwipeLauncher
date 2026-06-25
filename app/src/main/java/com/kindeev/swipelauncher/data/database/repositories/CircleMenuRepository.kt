package com.kindeev.swipelauncher.data.database.repositories

import com.kindeev.swipelauncher.data.database.daos.CircleMenuDao
import com.kindeev.swipelauncher.data.database.mappers.fromEntity
import com.kindeev.swipelauncher.data.database.mappers.toEntity
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CircleMenuRepository(private val dao: CircleMenuDao) {

    fun getAll(): Flow<List<CircleMenu>> =
        dao.getAll().map { data -> data.map { it.fromEntity() } }

    suspend fun insert(circleMenu: CircleMenu) = dao.insert(circleMenu.toEntity())

    suspend fun insertMany(circleMenus: List<CircleMenu>) =
        dao.insertMany(circleMenus.map { it.toEntity() })

    suspend fun deleteMany(circleMenus: List<CircleMenu>) =
        dao.deleteMany(circleMenus.map { it.toEntity() })
}