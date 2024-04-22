package com.kindeev.swipelauncher.domain.viewModels

import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.emptyCircleMenu
import com.kindeev.swipelauncher.domain.entities.CircleMenuDirection
import com.kindeev.swipelauncher.domain.entities.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import kotlinx.coroutines.launch

class EditCircleMenuScreenVM(circleMenuId: Int?) : ViewModel() {
    private val _circleMenu = MutableLiveData<CircleMenu?>(null)
    val circleMenu: LiveData<CircleMenu?> = _circleMenu
    private val _selectedCircleMenuItem = MutableLiveData<CircleMenuItem?>(null)
    val selectedCircleMenuItem: LiveData<CircleMenuItem?> = _selectedCircleMenuItem
    private val _direction = MutableLiveData(CircleMenuDirection.Up)
    val direction: LiveData<CircleMenuDirection> = _direction

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

    fun goToCircleMenu(circleMenuId: Int) {
        LauncherData.allCircleMenus.value?.find { it.id == circleMenuId }?.let {
            _circleMenu.value = it
        }
    }

    fun getMenuSize(configuration: Configuration): Float {
        return Integer.min(configuration.screenHeightDp, configuration.screenWidthDp) / 3 * 2f
    }

    fun setDirection(circleMenuDirection: CircleMenuDirection) {
        _direction.value = circleMenuDirection
        _selectedCircleMenuItem.value = getNowCircleMenuItem()
    }

    fun updateCircleMenuItem(circleMenuItem: CircleMenuItem) = viewModelScope.launch {
        circleMenu.value?.let { circleMenu ->
            val newCircleMenu = updateCircleMenuItem(
                circleMenu = circleMenu,
                circleMenuItem = circleMenuItem
            )
            LauncherData.insertCircleMenu(newCircleMenu)
        }
    }

    fun updateCircleMenusEvent(circleMenus: List<CircleMenu>) {
        circleMenus.find { it.id == circleMenu.value?.id }?.let {
            _circleMenu.value = it
        }
        _selectedCircleMenuItem.value = getNowCircleMenuItem()
    }

    private fun updateCircleMenuItem(
        circleMenu: CircleMenu,
        circleMenuItem: CircleMenuItem
    ): CircleMenu {
        val menuImages = circleMenu.menuImages
        val menuActions = circleMenu.menuActions
        return when (direction.value) {
            CircleMenuDirection.Up -> {
                circleMenu.copy(
                    menuImages = menuImages.copy(
                        upImage = circleMenuItem.image
                    ),
                    menuActions = menuActions.copy(
                        upAction = circleMenuItem.action
                    )
                )
            }

            CircleMenuDirection.Down -> {
                circleMenu.copy(
                    menuImages = menuImages.copy(
                        downImage = circleMenuItem.image
                    ),
                    menuActions = menuActions.copy(
                        downAction = circleMenuItem.action
                    )
                )
            }

            CircleMenuDirection.Right -> {
                circleMenu.copy(
                    menuImages = menuImages.copy(
                        rightImage = circleMenuItem.image
                    ),
                    menuActions = menuActions.copy(
                        rightAction = circleMenuItem.action
                    )
                )
            }

            CircleMenuDirection.Left -> {
                circleMenu.copy(
                    menuImages = menuImages.copy(
                        leftImage = circleMenuItem.image
                    ),
                    menuActions = menuActions.copy(
                        leftAction = circleMenuItem.action
                    )
                )
            }

            null -> circleMenu
        }
    }

    private fun getNowCircleMenuItem(): CircleMenuItem? {
        circleMenu.value?.let { circleMenu ->
            return when (direction.value) {
                CircleMenuDirection.Up -> {
                    CircleMenuItem(
                        action = circleMenu.menuActions.upAction,
                        image = circleMenu.menuImages.upImage
                    )
                }

                CircleMenuDirection.Down -> {
                    CircleMenuItem(
                        action = circleMenu.menuActions.downAction,
                        image = circleMenu.menuImages.downImage
                    )
                }

                CircleMenuDirection.Right -> {
                    CircleMenuItem(
                        action = circleMenu.menuActions.rightAction,
                        image = circleMenu.menuImages.rightImage
                    )
                }

                CircleMenuDirection.Left -> {
                    CircleMenuItem(
                        action = circleMenu.menuActions.leftAction,
                        image = circleMenu.menuImages.leftImage
                    )
                }

                else -> null
            }
        }
        return null
    }

    fun insertCircleMenu(circleMenu: CircleMenu) = viewModelScope.launch {
        LauncherData.insertCircleMenu(circleMenu)
    }
}