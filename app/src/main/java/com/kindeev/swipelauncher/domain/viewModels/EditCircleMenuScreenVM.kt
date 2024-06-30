package com.kindeev.swipelauncher.domain.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.emptyCircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.PickAppActionWithImage
import com.kindeev.swipelauncher.domain.getValueOf
import kotlinx.coroutines.launch

class EditCircleMenuScreenVM(circleMenuId: Int?) : ViewModel() {
    private val _circleMenu = MutableLiveData<CircleMenu?>(null)
    val circleMenu: LiveData<CircleMenu?> = _circleMenu
    private val _item = MutableLiveData(circleMenu.value?.items?.firstOrNull())
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

    fun updateImage(item: CircleMenuItem) = viewModelScope.launch {
        var action = item.action
        if (item.image is AppImage && LauncherData.settings.value?.getValueOf(
                SettingNames.PickAppActionWithImage,
                PickAppActionWithImage::class.java
            )?.enabled == true
        ) {
            action = OpenAppAction(item.image.packageName)
        }
        circleMenu.value?.let { circleMenu ->
            LauncherData.insertCircleMenu(
                circleMenu.copy(
                    items = circleMenu.items.toMutableList()
                        .apply { replaceAll { if (it.offset == item.offset) item.copy(action = action) else it } })
            )
        }
    }

    fun updateCircleMenusEvent(circleMenus: List<CircleMenu>) {
        circleMenus.find { it.id == circleMenu.value?.id }?.let {
            _circleMenu.value = it
            _item.postValue(it.items.find { newItem -> newItem.offset == item.value?.offset }
                ?: it.items.firstOrNull())
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