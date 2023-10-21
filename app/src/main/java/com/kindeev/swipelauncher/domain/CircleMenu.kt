package com.kindeev.swipelauncher.domain

data class CircleMenu(
    val upAction: CircleMenuItemAction,
    val downAction: CircleMenuItemAction,
    val rightAction: CircleMenuItemAction,
    val leftAction: CircleMenuItemAction
)
