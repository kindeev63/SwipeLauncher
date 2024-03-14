package com.kindeev.swipelauncher.domain.useCases

import com.kindeev.swipelauncher.domain.dataBase.AppDao
import com.kindeev.swipelauncher.domain.entities.CircleMenu

class InsertCircleMenuUseCase(private val appDao: AppDao) {

    suspend fun insertCircleMenu(circleMenu: CircleMenu) {
        appDao.insertCircleMenu(circleMenu)
    }

    suspend fun insertCircleMenus(circleMenus: List<CircleMenu>) {
        appDao.insertCircleMenus(circleMenus)
    }
}