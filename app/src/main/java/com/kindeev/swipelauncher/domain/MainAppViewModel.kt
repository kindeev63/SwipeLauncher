package com.kindeev.swipelauncher.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.CircleMenuUseCases.DeleteCircleMenuUseCase
import com.kindeev.swipelauncher.domain.CircleMenuUseCases.GetCircleMenuUseCase
import com.kindeev.swipelauncher.domain.CircleMenuUseCases.InsertCircleMenuUseCase
import kotlinx.coroutines.launch

class MainAppViewModel(appDao: AppDao): ViewModel() {
    private val getCircleMenuUseCase = GetCircleMenuUseCase(appDao = appDao)
    private val insertCircleMenuUseCase = InsertCircleMenuUseCase(appDao = appDao)
    private val deleteCircleMenuUseCase = DeleteCircleMenuUseCase(appDao = appDao)
    val allCircleMenu: LiveData<List<CircleMenu>>

    init {
        allCircleMenu = appDao.getAllCircleMenu()
    }
    fun insertCircleMenu(circleMenu: CircleMenu) = viewModelScope.launch {
        insertCircleMenuUseCase.insert(circleMenu)
    }

    fun deleteCircleMenu(circleMenu: CircleMenu) = viewModelScope.launch {
        deleteCircleMenuUseCase.delete(circleMenu)
    }

    suspend fun getCircleMenuById(id: Int, function: (CircleMenu?) -> Unit) {
        viewModelScope.launch {
            function(getCircleMenuUseCase.getById(id))
        }
    }
}