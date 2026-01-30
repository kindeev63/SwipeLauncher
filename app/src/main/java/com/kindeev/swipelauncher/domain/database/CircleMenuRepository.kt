package com.kindeev.swipelauncher.domain.database

import com.kindeev.swipelauncher.domain.entities.circle_menu.CircleMenu
import kotlinx.coroutines.flow.Flow

interface CircleMenuRepository {

    fun getAllFlow(): Flow<List<CircleMenu>>
    suspend fun getAll(): List<CircleMenu>
    suspend fun getById(id: Int): CircleMenu?
    suspend fun insert(circleMenu: CircleMenu)
    suspend fun insert(circleMenus: List<CircleMenu>)
    suspend fun deleteByIds(ids: List<Int>)
}