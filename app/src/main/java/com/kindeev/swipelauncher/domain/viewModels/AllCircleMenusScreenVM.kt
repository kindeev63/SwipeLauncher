package com.kindeev.swipelauncher.domain.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.ChangedCircleMenu
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenCircleMenu
import com.kindeev.swipelauncher.domain.getAs
import kotlinx.coroutines.launch

class AllCircleMenusScreenVM : ViewModel() {

    fun deleteCircleMenu(circleMenu: CircleMenu) {
        viewModelScope.launch {
            if (circleMenu.id == 0) return@launch
            LauncherData.deleteCircleMenu(circleMenu)
            LauncherData.allCircleMenus.value?.let { circleMenus ->
                val changedCircleMenus = mutableListOf<CircleMenu>()
                circleMenus.forEach { currentCircleMenu ->
                    val changedCircleMenu = deleteOpenCircleMenuActions(
                        circleMenu = currentCircleMenu,
                        circleMenuId = circleMenu.id
                    )
                    if (changedCircleMenu.changed) changedCircleMenus.add(changedCircleMenu.circleMenu)
                }
                LauncherData.insertCircleMenus(changedCircleMenus)
            }
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
            val openCircleMenu = menuActions.upAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id == circleMenuId) {
                menuActions.upAction = defaultAction
                changed = true
            }
        }
        if (menuActions.downAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = menuActions.downAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id == circleMenuId) {
                menuActions.downAction = defaultAction
                changed = true
            }
        }
        if (menuActions.rightAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = menuActions.rightAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id == circleMenuId) {
                menuActions.rightAction = defaultAction
                changed = true
            }
        }
        if (menuActions.leftAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = menuActions.leftAction.data.getAs(OpenCircleMenu::class.java)
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