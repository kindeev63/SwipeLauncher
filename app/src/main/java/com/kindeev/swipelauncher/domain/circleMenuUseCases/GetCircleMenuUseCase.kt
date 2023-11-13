package com.kindeev.swipelauncher.domain.circleMenuUseCases

import com.kindeev.swipelauncher.domain.AppDao
import com.kindeev.swipelauncher.domain.CircleMenu

class GetCircleMenuUseCase(private val appDao: AppDao) {
    fun getById(id: Int): CircleMenu? {
        return appDao.getCircleMenu(id)
    }
}