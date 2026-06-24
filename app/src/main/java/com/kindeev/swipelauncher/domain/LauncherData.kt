package com.kindeev.swipelauncher.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.kindeev.swipelauncher.domain.entities.ApplicationData
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object LauncherData {

    private lateinit var dataRepository: DataRepository
    lateinit var allCircleMenus: StateFlow<List<CircleMenu>>
    lateinit var allApplicationData: StateFlow<List<ApplicationData>>
    lateinit var settings: StateFlow<List<SettingData>>

    private val _allApplicationInfo = MutableStateFlow<List<ApplicationInfo>>(emptyList())
    val allApplicationInfo: StateFlow<List<ApplicationInfo>> = _allApplicationInfo
    var userImages = emptyMap<Int, ImageBitmap>()

    var flashLightCondition = false

    private val _textColorOnWallpaper = MutableStateFlow(Color.White)
    val textColorOnWallpaper: StateFlow<Color> = _textColorOnWallpaper

    fun setDataRepository(repository: DataRepository) {
        dataRepository = repository
    }

    fun setTextColorOnWallpaper(color: Color) {
        _textColorOnWallpaper.value = color
    }

    fun setAllApplications(applications: List<ApplicationInfo>) {
        _allApplicationInfo.value = applications
    }

    suspend fun insertCircleMenu(circleMenu: CircleMenu) {
        dataRepository.insertCircleMenu(circleMenu)
    }

    suspend fun insertCircleMenus(circleMenus: List<CircleMenu>) {
        dataRepository.insertCircleMenus(circleMenus)
    }

    suspend fun deleteCircleMenus(circleMenus: List<CircleMenu>) {
        dataRepository.deleteCircleMenus(circleMenus)
    }

    suspend fun insertSetting(settingData: SettingData) {
        dataRepository.insertSettings(
            settings.value.map {
                if (it.name == settingData.name)
                    settingData
                else it
            }
        )
    }

    suspend fun insertApplicationData(applicationData: ApplicationData) {
        dataRepository.insertApplicationData(applicationData)
    }

    suspend fun insertApplicationsData(applicationsData: List<ApplicationData>) {
        dataRepository.insertApplicationsData(applicationsData)
    }

    suspend fun deleteApplicationData(applicationData: ApplicationData) {
        dataRepository.deleteApplicationData(applicationData)
    }

    suspend fun deleteApplicationDataByPackageName(packageName: String) {
        dataRepository.deleteApplicationDataByPackageName(packageName)
    }

    suspend fun deleteApplicationsData(applicationsData: List<ApplicationData>) {
        dataRepository.deleteApplicationsData(applicationsData)
    }
}