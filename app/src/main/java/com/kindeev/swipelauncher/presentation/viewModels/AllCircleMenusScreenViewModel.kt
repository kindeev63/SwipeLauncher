package com.kindeev.swipelauncher.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu

class AllCircleMenusScreenViewModel(
    val mainAppViewModel: MainAppViewModel
) : ViewModel() {

    fun deleteCircleMenu(circleMenu: CircleMenu) {
        if (circleMenu.id == 0) return
        mainAppViewModel.deleteCircleMenu(circleMenu)
        mainAppViewModel.allCircleMenu.value?.let { circleMenus ->
            val changedCircleMenus = mutableListOf<CircleMenu>()
            circleMenus.forEach { currentCircleMenu ->
                val newCircleMenu = deleteOpenCircleMenuActions(
                    circleMenu = currentCircleMenu,
                    circleMenuId = circleMenu.id
                )
                if (newCircleMenu != currentCircleMenu) changedCircleMenus.add(newCircleMenu)
            }
            mainAppViewModel.insertCircleMenus(changedCircleMenus)
        }
    }

    private fun deleteOpenCircleMenuActions(circleMenu: CircleMenu, circleMenuId: Int): CircleMenu {
        val menuActions = circleMenu.menuActions
        val noneAction = CircleMenuAction(type = CircleMenuActionTypes.OpenCircleMenu, data = OpenCircleMenu(id = 0))
        if (menuActions.upAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = menuActions.upAction.data as OpenCircleMenu
            if (openCircleMenu.id == circleMenuId) menuActions.upAction = noneAction
        }
        if (menuActions.downAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = menuActions.downAction.data as OpenCircleMenu
            if (openCircleMenu.id == circleMenuId) menuActions.downAction = noneAction
        }
        if (menuActions.rightAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = menuActions.rightAction.data as OpenCircleMenu
            if (openCircleMenu.id == circleMenuId) menuActions.rightAction = noneAction
        }
        if (menuActions.leftAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = menuActions.leftAction.data as OpenCircleMenu
            if (openCircleMenu.id == circleMenuId) menuActions.leftAction = noneAction
        }
        return circleMenu.copy(menuActions = menuActions)
    }
}