package com.kindeev.swipelauncher.data.dataBases.circle_menu

import com.kindeev.swipelauncher.domain.database.CircleMenuRepository
import com.kindeev.swipelauncher.domain.entities.circle_menu.CircleMenu
import kotlinx.coroutines.flow.map

class CircleMenuDatabaseManager(
    private val dao: CircleMenuDao
): CircleMenuRepository {

    override fun getAllFlow() = dao.getAllFlow().map { data ->
        data.map { it.toCircleMenu() }
    }

    override suspend fun getAll() = dao.getAll().map { it.toCircleMenu() }

    override suspend fun getById(id: Int): CircleMenu? {
        return dao.getById(id)?.toCircleMenu()
    }

    override suspend fun insert(circleMenu: CircleMenu) {
        dao.insert(circleMenu.toSCircleMenu())
    }

    override suspend fun insert(circleMenus: List<CircleMenu>) {
        dao.insert(circleMenus.map { it.toSCircleMenu() })
    }

    override suspend fun deleteByIds(ids: List<Int>) {
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