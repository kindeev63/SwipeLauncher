package com.kindeev.swipelauncher.presentation

import android.app.Application
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.AppDataBase
import com.kindeev.swipelauncher.presentation.activities.ErrorActivity

class MainApp: Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalExceptionHandler.initialize(this, ErrorActivity::class.java)
        val appDao = AppDataBase.getDataBase(this).getDao()
        LauncherData.setAppDao(appDao)
        LauncherData.allCircleMenus = appDao.getAllCircleMenu()
        LauncherData.allSettings = appDao.getAllSettings()
    }
}