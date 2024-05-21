package com.kindeev.swipelauncher.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kindeev.swipelauncher.domain.dataBase.AppDao
import com.kindeev.swipelauncher.domain.entities.ApplicationData
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.entities.settings.Setting
import com.kindeev.swipelauncher.domain.entities.settings.SettingData

object LauncherData {

    private lateinit var appDao: AppDao
    lateinit var allCircleMenus: LiveData<List<CircleMenu>>
    lateinit var allApplicationData: LiveData<List<ApplicationData>>
    lateinit var settings: LiveData<List<SettingData>>

    private val _allApplicationInfo = MutableLiveData<List<ApplicationInfo>>(emptyList())
    val allApplicationInfo: LiveData<List<ApplicationInfo>> = _allApplicationInfo
    var userImages = emptyMap<Int, ImageBitmap>()

    var flashLightCondition = false

    val textColorOnWallpaper: Color
        get() = if (settings.value?.getValueOf(
                Setting.BlackTextColorOnWallpaper,
                Boolean::class.java
            ) == true
        ) Color.Black else Color.White

    fun setAppDao(appDao: AppDao) {
        this.appDao = appDao
    }

    fun setAllApplications(applications: List<ApplicationInfo>) {
        _allApplicationInfo.postValue(applications)
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

    suspend fun insertApplicationData(applicationData: ApplicationData) {
        appDao.insertApplicationData(applicationData)
    }

    suspend fun insertApplicationsData(applicationsData: List<ApplicationData>) {
        appDao.insertApplicationsData(applicationsData)
    }

    suspend fun deleteApplicationData(applicationData: ApplicationData) {
        appDao.deleteApplicationData(applicationData)
    }

    suspend fun deleteApplicationDataByPackageName(packageName: String) {
        appDao.deleteApplicationDataByPackageName(packageName)
    }

    suspend fun deleteApplicationsData(applicationsData: List<ApplicationData>) {
        appDao.deleteApplicationsData(applicationsData)
    }
}