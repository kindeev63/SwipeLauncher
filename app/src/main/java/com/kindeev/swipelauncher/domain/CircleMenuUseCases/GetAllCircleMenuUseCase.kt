package com.kindeev.swipelauncher.domain.CircleMenuUseCases

import com.kindeev.swipelauncher.domain.AppDao

class GetAllCircleMenuUseCase(private val appDao: AppDao) {

    fun get() = appDao.getAllCircleMenu()
}