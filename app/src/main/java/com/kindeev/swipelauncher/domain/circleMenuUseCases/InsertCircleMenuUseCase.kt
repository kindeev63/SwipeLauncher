package com.kindeev.swipelauncher.domain.circleMenuUseCases

import com.kindeev.swipelauncher.domain.AppDao
import com.kindeev.swipelauncher.domain.CircleMenu

class InsertCircleMenuUseCase(private val appDao: AppDao) {

    suspend fun insert(circleMenu: CircleMenu) {
        appDao.insertCircleMenu(circleMenu)
    }
}