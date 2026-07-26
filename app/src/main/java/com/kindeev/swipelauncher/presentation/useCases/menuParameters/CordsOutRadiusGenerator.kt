package com.kindeev.swipelauncher.presentation.useCases.menuParameters

import androidx.compose.ui.geometry.Offset
import com.kindeev.swipelauncher.presentation.entities.MenuSize
import kotlin.math.pow

fun corsOutRadiusGenerator(menuSize: MenuSize, swipeRadiusGenerator: (MenuSize) -> Float): (Offset) -> Boolean {
    val swipeRadiusSq = swipeRadiusGenerator(menuSize).pow(2)
    return { cords ->
        cords.x.pow(2) + cords.y.pow(2) > swipeRadiusSq
    }
}