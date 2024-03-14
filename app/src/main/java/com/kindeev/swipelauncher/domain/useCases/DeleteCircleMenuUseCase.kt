package com.kindeev.swipelauncher.domain.useCases

import com.kindeev.swipelauncher.domain.dataBase.AppDao
import com.kindeev.swipelauncher.domain.entities.CircleMenu

class DeleteCircleMenuUseCase(private val appDao: AppDao) {

    suspend fun delete(circleMenu: CircleMenu) {
        appDao.deleteCircleMenu(circleMenu)
    }
}