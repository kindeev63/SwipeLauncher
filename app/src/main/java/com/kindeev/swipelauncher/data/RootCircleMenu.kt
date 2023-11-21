package com.kindeev.swipelauncher.data

import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage

object RootCircleMenu {
    private val downImage = CircleMenuImage(
        type = CircleMenuImageTypes.DefaultImage,
        data = DefaultImage(
            id = R.drawable.ic_down_arrow
        )
    )
    private val downAction = CircleMenuAction(type = CircleMenuActionTypes.NoneAction)
    private val rightImage = CircleMenuImage(
        type = CircleMenuImageTypes.DefaultImage,
        data = DefaultImage(
            id = R.drawable.ic_left_arrow
        )
    )
    private val rightAction = CircleMenuAction(type = CircleMenuActionTypes.NoneAction)
    private val leftImage = CircleMenuImage(
        type = CircleMenuImageTypes.DefaultImage,
        data = DefaultImage(
            id = R.drawable.ic_right_arrow
        )
    )
    private val leftAction = CircleMenuAction(type = CircleMenuActionTypes.NoneAction)
    private val upImage = CircleMenuImage(
        type = CircleMenuImageTypes.DefaultImage,
        data = DefaultImage(
            id = R.drawable.ic_up_arrow
        )
    )
    private val upAction = CircleMenuAction(
        type = CircleMenuActionTypes.OpenCircleMenu,
        data = OpenCircleMenu(
            circleMenu = CircleMenu(
                id = 1,
                upAction = downAction,
                upImage = upImage,
                downAction = downAction,
                downImage = downImage,
                rightAction = rightAction,
                rightImage = rightImage,
                leftAction = leftAction,
                leftImage = leftImage
            )
        )
    )
    val rootCircleMenu = CircleMenu(
        upAction = upAction,
        upImage = upImage,
        downAction = downAction,
        downImage = downImage,
        rightAction = rightAction,
        rightImage = rightImage,
        leftAction = leftAction,
        leftImage = leftImage
    )
}