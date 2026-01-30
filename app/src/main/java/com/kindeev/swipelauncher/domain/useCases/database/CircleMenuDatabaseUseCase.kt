package com.kindeev.swipelauncher.domain.useCases.database

import com.kindeev.swipelauncher.data.dataBases.circle_menu.CircleMenuDao
import com.kindeev.swipelauncher.data.dataBases.circle_menu.SCircleMenu
import com.kindeev.swipelauncher.domain.entities.circle_menu.CircleMenu
import kotlinx.coroutines.flow.map

class CircleMenuDatabaseUseCase(
    private val dao: CircleMenuDao
) {

    fun getAllFlow() = dao.getAllFlow().map { data ->
        data.map { it.toCircleMenu() }
    }

    suspend fun getAll() = dao.getAll().map { it.toCircleMenu() }

    suspend fun getById(id: Int): CircleMenu? {
        return dao.getById(id)?.toCircleMenu()
    }

    suspend fun insert(circleMenu: CircleMenu) {
        dao.insert(circleMenu.toSCircleMenu())
    }

    suspend fun insert(circleMenus: List<CircleMenu>) {
        dao.insert(circleMenus.map { it.toSCircleMenu() })
    }

    suspend fun deleteByIds(ids: List<Int>) {
        dao.deleteByIds(ids)
    }

    fun SCircleMenu.toCircleMenu(): CircleMenu {
        return CircleMenu(
            id = id,
            title = title,
            items = items
        )
    }

    private fun CircleMenu.toSCircleMenu(): SCircleMenu {
        return SCircleMenu(
            id = id,
            title = title,
            items = items
        )
    }
}