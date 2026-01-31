package com.kindeev.swipelauncher.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.circle_menu.CircleMenu

object LauncherData {
    lateinit var allCircleMenus: LiveData<List<CircleMenu>>
    lateinit var allApplicationData: LiveData<List<ApplicationData>>
    lateinit var settings: LiveData<List<SettingData>>

    private val _allApplicationInfo = MutableLiveData<List<ApplicationInfo>>(emptyList())
    val allApplicationInfo: LiveData<List<ApplicationInfo>> = _allApplicationInfo
    var userImages = emptyMap<Int, ImageBitmap>()

    var flashLightCondition = false

    private val _textColorOnWallpaper = MutableLiveData(Color.White)
    val textColorOnWallpaper: LiveData<Color> = _textColorOnWallpaper



    fun setAllApplications(applications: List<ApplicationInfo>) {
        _allApplicationInfo.postValue(applications)
    }
}