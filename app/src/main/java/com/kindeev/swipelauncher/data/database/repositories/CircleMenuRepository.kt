package com.kindeev.swipelauncher.data.database.repositories

import com.kindeev.swipelauncher.data.database.daos.CircleMenuDao
import com.kindeev.swipelauncher.data.database.mappers.fromTable
import com.kindeev.swipelauncher.data.database.mappers.toTable
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CircleMenuRepository(private val dao: CircleMenuDao) {

    suspend fun getCircleMenus(): List<CircleMenu> = dao.getCircleMenus().map { it.fromTable() }

    fun getAll(): Flow<List<CircleMenu>> =
        dao.getAll().map { data -> data.map { it.fromTable() } }

    suspend fun insert(circleMenu: CircleMenu) = dao.insert(circleMenu.toTable())

    suspend fun insertMany(circleMenus: List<CircleMenu>) =
        dao.insertMany(circleMenus.map { it.toTable() })

    suspend fun deleteMany(circleMenus: List<CircleMenu>) =
        dao.deleteMany(circleMenus.map { it.toTable() })

    suspend fun deleteManyByIds(ids: Collection<Int>) = dao.deleteManyByIds(ids)
}