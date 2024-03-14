package com.kindeev.swipelauncher.domain.useCases

import com.kindeev.swipelauncher.domain.dataBase.AppDao

class GetAllCircleMenuUseCase(private val appDao: AppDao) {

    fun get() = appDao.getAllCircleMenu()
}