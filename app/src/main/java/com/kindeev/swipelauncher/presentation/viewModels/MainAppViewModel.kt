package com.kindeev.swipelauncher.presentation.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.data.dataBaseElements.AppDataBase
import com.kindeev.swipelauncher.domain.AppDao
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuUseCases.DeleteCircleMenuUseCase
import com.kindeev.swipelauncher.domain.circleMenuUseCases.GetAllCircleMenuUseCase
import com.kindeev.swipelauncher.domain.circleMenuUseCases.InsertCircleMenuUseCase
import kotlinx.coroutines.launch

class MainAppViewModel(application: Application): AndroidViewModel(application) {
    private val appDao: AppDao
    private val insertCircleMenuUseCase: InsertCircleMenuUseCase
    private val deleteCircleMenuUseCase: DeleteCircleMenuUseCase
    private val getAllCircleMenuUseCase: GetAllCircleMenuUseCase
    val allCircleMenu: LiveData<List<CircleMenu>>

    init {
        appDao = AppDataBase.getDataBase(application).getDao()
        insertCircleMenuUseCase = InsertCircleMenuUseCase(appDao)
        deleteCircleMenuUseCase = DeleteCircleMenuUseCase(appDao)
        getAllCircleMenuUseCase = GetAllCircleMenuUseCase(appDao)
        allCircleMenu = getAllCircleMenuUseCase.get()
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
}