package com.kindeev.swipelauncher.domain.circleMenuUseCases

import com.kindeev.swipelauncher.domain.AppDao
import com.kindeev.swipelauncher.domain.CircleMenu

class DeleteCircleMenuUseCase(private val appDao: AppDao) {

    suspend fun delete(circleMenu: CircleMenu) {
        appDao.deleteCircleMenu(circleMenu)
    }
}