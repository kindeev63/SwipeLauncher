package com.kindeev.swipelauncher.presentation.viewModels.allCircleMenus

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.di.container
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AllCircleMenusScreenVM(context: Context) : ViewModel() {

    private val container = context.container
    private val _selectedMenuIds = MutableStateFlow<List<Int>>(emptyList())
    val selectedMenuIds: StateFlow<List<Int>> = _selectedMenuIds

    fun selectAllMenus(allMenus: List<CircleMenu>) {
        _selectedMenuIds.value = allMenus.map { it.id }
    }

    fun deleteSelectedMenus(allMenus: List<CircleMenu>) = viewModelScope.launch {
        container.dataRepository.deleteCircleMenus(allMenus.filter { selectedMenuIds.value.contains(it.id) }
            .filter { it.id != 0 })
        _selectedMenuIds.value = emptyList()
    }

    fun exportSelectedMenus(allMenus: List<CircleMenu>, onFinish: (Boolean) -> Unit) {
        onFinish(
            container.exportCircleMenusUseCase.export(
                allMenus.filter { selectedMenuIds.value.contains(it.id) }
            )
        )
        _selectedMenuIds.value = emptyList()
    }

    fun importCircleMenus(uri: Uri, onFinish: (Boolean) -> Unit) = viewModelScope.launch {
        onFinish(
            container.importCircleMenusUseCase.import(uri)
        )
    }

    fun finishSelect() {
        _selectedMenuIds.value = emptyList()
    }

    fun changeSelectionStateOf(id: Int) {
        _selectedMenuIds.value =
            selectedMenuIds.value.toMutableList().apply {
                if (contains(id)) {
                    remove(id)
                } else {
                    add(id)
                }
            }
    }
}