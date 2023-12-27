package com.kindeev.swipelauncher.domain.circleMenuUseCases

import com.kindeev.swipelauncher.domain.AppDao
import com.kindeev.swipelauncher.domain.CircleMenu

class InsertCircleMenuUseCase(private val appDao: AppDao) {

    suspend fun insertCircleMenu(circleMenu: CircleMenu) {
        appDao.insertCircleMenu(circleMenu)
    }

    suspend fun insertCircleMenus(circleMenus: List<CircleMenu>) {
        appDao.insertCircleMenus(circleMenus)
    }
}