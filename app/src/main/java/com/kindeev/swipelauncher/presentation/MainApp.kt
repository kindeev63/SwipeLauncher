package com.kindeev.swipelauncher.presentation

import android.app.Application
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.data.database.AppDataBase
import com.kindeev.swipelauncher.data.database.getRepository
import com.kindeev.swipelauncher.data.userImages.UserImagesRepository
import com.kindeev.swipelauncher.data.userImages.UserImagesStorage
import com.kindeev.swipelauncher.data.userImages.initCoil
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.utils.getMinScreenLength
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainApp: Application() {

    override fun onCreate() {
        super.onCreate()
//        GlobalExceptionHandler.initialize(this, ErrorActivity::class.java)

        // Constants
        setConstants()

        // Database
        val dataRepository = AppDataBase.getDataBase(this).getRepository()
        LauncherData.setDataRepository(dataRepository)
        LauncherData.settings = dataRepository.getAllSettings().stateIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            started = SharingStarted.Eagerly,
            initialValue = Constants.defaultSettings
        )
        LauncherData.allCircleMenus = dataRepository.getAllCircleMenus().stateIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )
        LauncherData.allApplicationData = dataRepository.getAllApplicationsData().stateIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

        // UserImages
        initCoil(this)
        val userImagesStorage = UserImagesStorage(this)
        val userImagesRepository = UserImagesRepository(userImagesStorage, this)
        LauncherData.setUserImagesRepository(userImagesRepository)
        CoroutineScope(Dispatchers.IO).launch {
            userImagesRepository.prefetchAll()
        }
    }

    private fun setConstants() {
        Constants.minScreenLength = getMinScreenLength()
        Constants.settingsTextSize = Constants.minScreenLength.sp / 20
    }
}