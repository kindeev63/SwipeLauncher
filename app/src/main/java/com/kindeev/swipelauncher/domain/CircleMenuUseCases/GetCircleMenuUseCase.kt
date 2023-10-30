package com.kindeev.swipelauncher.domain.CircleMenuUseCases

import com.kindeev.swipelauncher.domain.AppDao
import com.kindeev.swipelauncher.domain.CircleMenu

class GetCircleMenuUseCase(private val appDao: AppDao) {
    suspend fun getById(id: Int): CircleMenu? {
        return appDao.getCircleMenu(id)
    }
}