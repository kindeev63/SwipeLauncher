package com.kindeev.swipelauncher.domain

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kindeev.swipelauncher.domain.dataBase.AppDao
import com.kindeev.swipelauncher.domain.entities.ApplicationData
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.entities.settings.SettingData

object LauncherData {

    private lateinit var appDao: AppDao
    lateinit var allCircleMenus: LiveData<List<CircleMenu>>
    lateinit var allSettings: LiveData<List<SettingData>>

    private val _allApplicationData = MutableLiveData<List<ApplicationData>>(emptyList())
    val allApplicationData: LiveData<List<ApplicationData>> = _allApplicationData
    var userImages = emptyMap<Int, ImageBitmap>()

    var flashLightCondition = false

    fun setAppDao(appDao: AppDao) {
        this.appDao = appDao
    }

    fun setAllApplicationData(allApplicationData: List<ApplicationData>) {
        _allApplicationData.postValue(allApplicationData)
    }

    suspend fun insertCircleMenu(circleMenu: CircleMenu) {
        appDao.insertCircleMenu(circleMenu)
    }

    suspend fun insertCircleMenus(circleMenus: List<CircleMenu>) {
        appDao.insertCircleMenus(circleMenus)
    }

    suspend fun deleteCircleMenu(circleMenu: CircleMenu) {
        appDao.deleteCircleMenu(circleMenu)
    }

    suspend fun insertSetting(settingData: SettingData) {
        appDao.insertSetting(settingData)
    }

    suspend fun insertSettings(settingsData: List<SettingData>) {
        appDao.insertSettings(settingsData)
    }
}