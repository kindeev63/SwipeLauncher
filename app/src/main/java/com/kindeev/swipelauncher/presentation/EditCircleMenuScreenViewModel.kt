package com.kindeev.swipelauncher.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.data.CircleMenuDirection
import com.kindeev.swipelauncher.data.CircleMenuItem
import com.kindeev.swipelauncher.data.RootCircleMenu
import com.kindeev.swipelauncher.domain.CircleMenu

class EditCircleMenuScreenViewModel(
    private val context: Context,
    private val mainAppViewModel: MainAppViewModel
) : ViewModel() {
    private val _circleMenu = MutableLiveData(RootCircleMenu.rootCircleMenu)
    val circleMenu: LiveData<CircleMenu> = _circleMenu
    private val _selectedCircleMenuItem = MutableLiveData<CircleMenuItem?>(null)
    val selectedCircleMenuItem: LiveData<CircleMenuItem?> = _selectedCircleMenuItem
    private val _direction = MutableLiveData(CircleMenuDirection.Up)
    val direction: LiveData<CircleMenuDirection> = _direction

    fun setCircleMenu(circleMenu: CircleMenu) {
        _circleMenu.value = circleMenu
    }

    fun setDirection(circleMenuDirection: CircleMenuDirection) {
        _direction.value = circleMenuDirection
        _selectedCircleMenuItem.value = getNowCircleMenuItem()
    }

    fun updateCircleMenuItem(circleMenuItem: CircleMenuItem) {
        circleMenu.value?.let { circleMenu ->
            val newCircleMenu = updateCircleMenuItem(
                circleMenu = circleMenu,
                circleMenuItem = circleMenuItem
            )
            Log.e("test", "New Circle Menu $newCircleMenu")
            mainAppViewModel.insertCircleMenu(newCircleMenu)
        }
    }

    fun updateCircleMenusEvent(circleMenus: List<CircleMenu>) {
        circleMenus.find { it.id == circleMenu.value?.id }?.let {
            _circleMenu.value = it
            _selectedCircleMenuItem.value = getNowCircleMenuItem()
        }
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
}