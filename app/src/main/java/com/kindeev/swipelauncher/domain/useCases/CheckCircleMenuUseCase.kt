package com.kindeev.swipelauncher.domain.useCases

import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenAppAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.UserImage

class CheckCircleMenuUseCase {

    fun check(
        circleMenu: CircleMenu,
        allPackageNames: List<String>,
        allCircleMenuIds: List<Int>,
        userImageIds: List<Int>
    ): CircleMenu? {
        val newItems = mutableListOf<CircleMenuItem>()

        for (item in circleMenu.items) {
            when (item.image) {
                is AppImage -> {
                    if (item.image.packageName !in allPackageNames) {
                        continue
                    }
                }

                is UserImage -> {
                    if (item.image.id !in userImageIds) {
                        continue
                    }
                }

                else -> {}
            }
            when (item.action) {
                is OpenAppAction -> {
                    if (item.action.packageName !in allPackageNames) {
                        continue
                    }
                }

                is OpenCircleMenuAction -> {
                    if (item.action.id !in allCircleMenuIds) {
                        continue
                    }
                }

                else -> {}
            }
            newItems.add(item)
        }

        return if (circleMenu.items == newItems) {
            null
        } else circleMenu.copy(items = newItems)
    }

    fun getOnlyChanged(
        circleMenus: List<CircleMenu>,
        allPackageNames: List<String>,
        userImageIds: List<Int>
    ): List<CircleMenu> {
        if (circleMenus.isEmpty() || allPackageNames.isEmpty()) return emptyList()
        val changedCircleMenus = mutableListOf<CircleMenu>()
        circleMenus.forEach { circleMenu ->
            check(
                circleMenu = circleMenu,
                allPackageNames = allPackageNames,
                allCircleMenuIds = circleMenus.map { it.id },
                userImageIds = userImageIds
            )?.let { changedCircleMenus.add(it) }
        }
        return changedCircleMenus
    }

}