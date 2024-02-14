package com.kindeev.swipelauncher.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.data.ChangedCircleMenu
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
                val changedCircleMenu = deleteOpenCircleMenuActions(
                    circleMenu = currentCircleMenu,
                    circleMenuId = circleMenu.id
                )
                if (changedCircleMenu.changed) changedCircleMenus.add(changedCircleMenu.circleMenu)
            }
            mainAppViewModel.insertCircleMenus(changedCircleMenus)
        }
    }

    private fun deleteOpenCircleMenuActions(
        circleMenu: CircleMenu,
        circleMenuId: Int
    ): ChangedCircleMenu {
        val menuActions = circleMenu.menuActions
        val defaultAction = CircleMenuAction(
            type = CircleMenuActionTypes.OpenCircleMenu,
            data = OpenCircleMenu(id = 0)
        )
        var changed = false
        if (menuActions.upAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = menuActions.upAction.data as OpenCircleMenu
            if (openCircleMenu.id == circleMenuId) {
                menuActions.upAction = defaultAction
                changed = true
            }
        }
        if (menuActions.downAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = menuActions.downAction.data as OpenCircleMenu
            if (openCircleMenu.id == circleMenuId) {
                menuActions.downAction = defaultAction
                changed = true
            }
        }
        if (menuActions.rightAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = menuActions.rightAction.data as OpenCircleMenu
            if (openCircleMenu.id == circleMenuId) {
                menuActions.rightAction = defaultAction
                changed = true
            }
        }
        if (menuActions.leftAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = menuActions.leftAction.data as OpenCircleMenu
            if (openCircleMenu.id == circleMenuId) {
                menuActions.leftAction = defaultAction
                changed = true
            }
        }
        return ChangedCircleMenu(
            circleMenu = circleMenu.copy(menuActions = menuActions),
            changed = changed
        )
    }
}