package com.kindeev.swipelauncher.data

import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.ui.theme.MenuActions
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
            data = DefaultImage(
                id = R.drawable.ic_up_arrow
            )
        ),
        downImage = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage(
                id = R.drawable.ic_settings
            )
        ),
        rightImage = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage(
                id = R.drawable.ic_right_arrow
            )
        ),
        leftImage = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage(
                id = R.drawable.ic_left_arrow
            )
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