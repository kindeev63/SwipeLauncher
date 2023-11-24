package com.kindeev.swipelauncher.data

import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.CircleMenuDirection
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage

object RootCircleMenu {
    private val directionSettings = CircleMenuDirection(
        image = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage(
                id = R.drawable.ic_settings
            )
        ),
        action = CircleMenuAction(type = CircleMenuActionTypes.OpenSettings)
    )
    private val directionDown = CircleMenuDirection(
        image = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage(
                id = R.drawable.ic_down_arrow
            )
        ),
        action = CircleMenuAction(type = CircleMenuActionTypes.NoneAction)
    )
    private val directionRight = CircleMenuDirection(
        image = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage(
                id = R.drawable.ic_left_arrow
            )
        ),
        action = CircleMenuAction(type = CircleMenuActionTypes.NoneAction)
    )
    private val directionLeft = CircleMenuDirection(
        image = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage(
                id = R.drawable.ic_right_arrow
            )
        ),
        action = CircleMenuAction(type = CircleMenuActionTypes.NoneAction)
    )
    private val upImage = CircleMenuImage(
        type = CircleMenuImageTypes.DefaultImage,
        data = DefaultImage(
            id = R.drawable.ic_up_arrow
        )
    )
    private val directionUp = CircleMenuDirection(
        image = upImage,
        action = CircleMenuAction(
            type = CircleMenuActionTypes.OpenCircleMenu,
            data = OpenCircleMenu(
                id = 0
            )
        )
    )
    val rootCircleMenu = CircleMenu(
        directionUp = directionUp,
        directionDown = directionSettings,
        directionRight = directionRight,
        directionLeft = directionLeft
    )
}