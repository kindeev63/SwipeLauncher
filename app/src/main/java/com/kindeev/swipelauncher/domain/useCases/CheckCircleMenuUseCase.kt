package com.kindeev.swipelauncher.domain.useCases

import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage

class CheckCircleMenuUseCase(
    private val userImagesUseCase: UserImagesUseCase,
    private val applicationsUseCase: ApplicationsUseCase
) {

    fun check(
        circleMenu: CircleMenu,

    ): Boolean {
        return check(
            circleMenu = circleMenu,
            allCircleMenuIds = LauncherData.allCircleMenus.value?.map { it.id } ?: emptyList(),
            allPackageNames = (LauncherData.allApplicationInfo.value
                ?: applicationsUseCase.getAllApplicationInfo()).map { it.packageName },
            userImageIds = userImagesUseCase.getUserImageIds()
        ) == null
    }

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

    private fun getOnlyChanged(
        circleMenus: List<CircleMenu>,
        allPackageNames: List<String>,
        allCircleMenuIds: List<Int>,
        userImageIds: List<Int>
    ): List<CircleMenu> {
        val changedCircleMenus = mutableListOf<CircleMenu>()
        circleMenus.forEach { circleMenu ->
            check(
                circleMenu = circleMenu,
                allPackageNames = allPackageNames,
                allCircleMenuIds = allCircleMenuIds,
                userImageIds = userImageIds
            )?.let { changedCircleMenus.add(it) }
        }
        return changedCircleMenus
    }

    fun getOnlyChanged(
        circleMenus: List<CircleMenu>
    ) = getOnlyChanged(
        circleMenus = circleMenus,
        allCircleMenuIds = LauncherData.allCircleMenus.value?.map { it.id } ?: emptyList(),
        allPackageNames = applicationsUseCase.getAllApplicationInfo().map { it.packageName },
        userImageIds = userImagesUseCase.getUserImageIds()
    )
}