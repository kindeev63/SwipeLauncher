package com.kindeev.swipelauncher.domain.viewModels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.exportCircleMenus
import com.kindeev.swipelauncher.domain.importCircleMenus
import kotlinx.coroutines.launch

class AllCircleMenusVM : ViewModel() {
    private val _selectedMenuIds = MutableLiveData<List<Int>>(emptyList())
    val selectedMenuIds: LiveData<List<Int>> = _selectedMenuIds

    fun selectAllMenus(allMenus: List<CircleMenu>) {
        _selectedMenuIds.postValue(allMenus.map { it.id })
    }

    fun deleteSelectedMenus(allMenus: List<CircleMenu>) = viewModelScope.launch {
        LauncherData.deleteCircleMenus(allMenus.filter { selectedMenuIds.value?.contains(it.id) == true }
            .filter { it.id != 0 })
        _selectedMenuIds.postValue(emptyList())
    }

    fun exportSelectedMenus(allMenus: List<CircleMenu>, context: Context, onFinish: (Boolean) -> Unit) {
        val result = context.exportCircleMenus(allMenus.filter { selectedMenuIds.value?.contains(it.id) == true })
        _selectedMenuIds.postValue(emptyList())
        onFinish(result)
    }

    fun importCircleMenus(uri: Uri?, context: Context, onFinish: (Boolean) -> Unit) = viewModelScope.launch {
        val result = context.importCircleMenus(uri ?: return@launch)
        onFinish(result)
    }

    fun finishSelect() {
        _selectedMenuIds.postValue(emptyList())
    }

    fun changeSelectionStateOf(circleMenu: CircleMenu) {
        _selectedMenuIds.postValue(
            selectedMenuIds.value?.toMutableList()?.apply {
                if (contains(circleMenu.id)) {
                    remove(circleMenu.id)
                } else {
                    add(circleMenu.id)
                }
            }
        )
    }
}