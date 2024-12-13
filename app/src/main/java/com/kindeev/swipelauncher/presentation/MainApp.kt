package com.kindeev.swipelauncher.presentation

import android.app.Application
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.AppDataBase
import com.kindeev.swipelauncher.domain.utils.getMinScreenLength
import com.kindeev.swipelauncher.presentation.activities.ErrorActivity

class MainApp: Application() {

    override fun onCreate() {
        super.onCreate()
//        GlobalExceptionHandler.initialize(this, ErrorActivity::class.java)
        val appDao = AppDataBase.getDataBase(this).getDao()
        LauncherData.setAppDao(appDao)
        LauncherData.settings = appDao.getAllSettings()
        LauncherData.allCircleMenus = appDao.getAllCircleMenu()
        LauncherData.allApplicationData = appDao.getAllApplicationData()
        setConstants()
    }

    private fun setConstants() {
        Constants.minScreenLength = getMinScreenLength()
        Constants.settingsTextSize = Constants.minScreenLength.sp / 20
    }
}