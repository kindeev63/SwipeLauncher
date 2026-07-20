package com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.entities

data class CircleMenuToDrawVM(
    val id: Int,
    val itemSize: Float,
    val items: List<CircleMenuItemToDrawVM>,
    val angles: List<Float>
)