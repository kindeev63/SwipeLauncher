package com.kindeev.swipelauncher.domain.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.emptyCircleMenu
import com.kindeev.swipelauncher.domain.entities.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import kotlinx.coroutines.launch

class EditCircleMenuScreenVM(circleMenuId: Int?) : ViewModel() {
    private val _circleMenu = MutableLiveData<CircleMenu?>(null)
    val circleMenu: LiveData<CircleMenu?> = _circleMenu
    private val _item = MutableLiveData(circleMenu.value?.items?.first())
    val item: LiveData<CircleMenuItem?> = _item


    init {
        if (circleMenuId == null) {
            val allIds = LauncherData.allCircleMenus.value?.map { it.id } ?: emptyList()
            var currentId = 0
            while (true) {
                if (currentId !in allIds) break
                currentId++
            }
            val circleMenu = emptyCircleMenu(id = currentId)
            viewModelScope.launch {
                LauncherData.insertCircleMenu(circleMenu = circleMenu)
            }
            _circleMenu.value = circleMenu
        } else {
            _circleMenu.value = LauncherData.allCircleMenus.value?.find { it.id == circleMenuId }
        }

    }

    fun setItem(item: CircleMenuItem) {
        _item.postValue(item)
    }

    fun updateCircleMenuItem(item: CircleMenuItem) = viewModelScope.launch {
        circleMenu.value?.let { circleMenu ->
            LauncherData.insertCircleMenu(
                circleMenu.copy(
                    items = circleMenu.items.toMutableList()
                        .apply { replaceAll { if (it.offset == item.offset) item else it } })
            )
        }
    }

    fun updateCircleMenusEvent(circleMenus: List<CircleMenu>) {
        circleMenus.find { it.id == circleMenu.value?.id }?.let {
            _circleMenu.value = it
            _item.postValue(it.items.find { newItem -> newItem.offset == item.value?.offset }
                ?: it.items.first())
        }
    }

    fun insertCircleMenu(circleMenu: CircleMenu) = viewModelScope.launch {
        LauncherData.insertCircleMenu(circleMenu)
    }

    fun deleteItem(item: CircleMenuItem) {
        circleMenu.value?.let {
            insertCircleMenu(it.copy(items = it.items.toMutableList().apply { remove(item) }))
        }
    }

    fun insertItem(item: CircleMenuItem) {
        circleMenu.value?.let {
            insertCircleMenu(it.copy(items = it.items.toMutableList().apply { add(item) }))
            _item.postValue(item)
        }
    }
}