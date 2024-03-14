package com.kindeev.swipelauncher.domain.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.data.dataBaseElements.AppDataBase
import com.kindeev.swipelauncher.domain.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.dataBase.AppDao
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.useCases.DeleteCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.GetAllCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.InsertCircleMenuUseCase
import kotlinx.coroutines.launch

class MainAppVM(application: Application): AndroidViewModel(application) {
    private val appDao: AppDao
    private val insertCircleMenuUseCase: InsertCircleMenuUseCase
    private val deleteCircleMenuUseCase: DeleteCircleMenuUseCase
    private val getAllCircleMenuUseCase: GetAllCircleMenuUseCase
    val allCircleMenu: LiveData<List<CircleMenu>>
    val allSettings: LiveData<List<SettingData>>
    var flashLightCondition: Boolean

    init {
        appDao = AppDataBase.getDataBase(application).getDao()
        insertCircleMenuUseCase = InsertCircleMenuUseCase(appDao)
        deleteCircleMenuUseCase = DeleteCircleMenuUseCase(appDao)
        getAllCircleMenuUseCase = GetAllCircleMenuUseCase(appDao)
        allCircleMenu = getAllCircleMenuUseCase.get()
        allSettings = appDao.getAllSettings()
        flashLightCondition = false
    }

    fun insertCircleMenu(circleMenu: CircleMenu) = viewModelScope.launch {
        insertCircleMenuUseCase.insertCircleMenu(circleMenu)
    }

    fun insertCircleMenus(circleMenus: List<CircleMenu>) = viewModelScope.launch {
        insertCircleMenuUseCase.insertCircleMenus(circleMenus)
    }

    fun deleteCircleMenu(circleMenu: CircleMenu) = viewModelScope.launch {
        deleteCircleMenuUseCase.delete(circleMenu)
    }

    fun insertSetting(settingData: SettingData) = viewModelScope.launch {
        appDao.insertSetting(settingData)
    }

    fun insertSettings(settingsData: List<SettingData>) = viewModelScope.launch {
        appDao.insertSettings(settingsData)
    }
}