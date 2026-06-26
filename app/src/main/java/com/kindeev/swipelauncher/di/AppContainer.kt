package com.kindeev.swipelauncher.di

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.data.coil.initCoil
import com.kindeev.swipelauncher.data.database.AppDataBase
import com.kindeev.swipelauncher.data.database.getRepository
import com.kindeev.swipelauncher.data.userImages.UserImagesRepository
import com.kindeev.swipelauncher.data.userImages.UserImagesStorage
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.utils.getMinScreenLength
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val dataRepository = AppDataBase.getDataBase(appContext).getRepository()
    private val userImagesStorage = UserImagesStorage(appContext)
    val userImagesRepository = UserImagesRepository(userImagesStorage, appContext)

    val circleMenus = dataRepository.getAllCircleMenus().stateIn(
        scope = appScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val applicationsData = dataRepository.getAllApplicationsData().stateIn(
        scope = appScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val settings = dataRepository.getAllSettings().stateIn(
        scope = appScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _applicationsInfo = MutableStateFlow<List<ApplicationInfo>>(emptyList())
    val applicationsInfo: StateFlow<List<ApplicationInfo>> = _applicationsInfo

    private val _textColorOnWallpaper = MutableStateFlow(Color.White)
    val textColorOnWallpaper: StateFlow<Color> = _textColorOnWallpaper

    var flashLightCondition = false

    init {
        Constants.minScreenLength = appContext.getMinScreenLength()
        Constants.settingsTextSize = Constants.minScreenLength.sp / 20

        initCoil(appContext)
        appScope.launch {
            userImagesRepository.prefetchAll()
        }
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

    fun setApplications(applications: List<ApplicationInfo>) {
        _applicationsInfo.value = applications
    }

    fun setTextColorOnWallpaper(color: Color) {
        _textColorOnWallpaper.value = color
    }
}