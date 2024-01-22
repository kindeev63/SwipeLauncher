package com.kindeev.swipelauncher.data

import com.kindeev.swipelauncher.data.dataBaseElements.MenuActions
import com.kindeev.swipelauncher.data.dataBaseElements.MenuImages
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage

object RootCircleMenu {
    private val menuImages = MenuImages(
        upImage = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage.UpArrow
        ),
        downImage = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage.Settings
        ),
        rightImage = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage.RightArrow
        ),
        leftImage = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage.LeftArrow
        )
    )
    private val menuActions = MenuActions(
        upAction = CircleMenuAction(
            type = CircleMenuActionTypes.OpenCircleMenu,
            data = OpenCircleMenu(
                id = 0
            )
        ),
        downAction = CircleMenuAction(type = CircleMenuActionTypes.OpenSettings),
        rightAction = CircleMenuAction(type = CircleMenuActionTypes.NoneAction),
        leftAction = CircleMenuAction(type = CircleMenuActionTypes.NoneAction)
    )
    val rootCircleMenu = CircleMenu(
        title = "Root",
        menuImages = menuImages,
        menuActions = menuActions
    )
}