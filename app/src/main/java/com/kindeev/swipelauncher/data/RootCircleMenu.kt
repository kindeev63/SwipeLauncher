package com.kindeev.swipelauncher.data

import com.google.gson.Gson
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.CircleMenuAction
import com.kindeev.swipelauncher.domain.CircleMenuImage

object RootCircleMenu {
    private val downImage =
        Gson().toJson(
            CircleMenuImage().apply {
                type = CircleMenuImage.DEFAULT_IMAGE
                data = R.drawable.ic_down_arrow.toString()
            }
        )
    private val downAction =
        Gson().toJson(
            CircleMenuAction().apply { type = CircleMenuAction.OPEN_SETTINGS }
        )
    private val rightImage =
        Gson().toJson(
            CircleMenuImage().apply {
                type = CircleMenuImage.DEFAULT_IMAGE
                data = R.drawable.ic_left_arrow.toString()
            }
        )
    private val rightAction =
        Gson().toJson(
            CircleMenuAction().apply { type = CircleMenuAction.NONE_ACTION }
        )
    private val leftImage =
        Gson().toJson(
            CircleMenuImage().apply {
                type = CircleMenuImage.DEFAULT_IMAGE
                data = R.drawable.ic_right_arrow.toString()
            }
        )
    private val leftAction =
        Gson().toJson(
            CircleMenuAction().apply { type = CircleMenuAction.NONE_ACTION }
        )
    private val upImage =
        Gson().toJson(
            CircleMenuImage().apply {
                type = CircleMenuImage.DEFAULT_IMAGE
                data = R.drawable.ic_up_arrow.toString()
            }
        )
    private val upAction =
        Gson().toJson(
            CircleMenuAction().apply {
                type = CircleMenuAction.OPEN_CIRCLE_MENU
                data = CircleMenu(
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
            }
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