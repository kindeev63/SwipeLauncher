package com.kindeev.swipelauncher.presentation.useCases.menuParameters

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.atan

fun itemIndexOnCordsGenerator(angles: List<Float>): (Offset) -> Int {
    if (angles.isEmpty()) {
        return { 0 }
    }
    return { cords ->
        angles.getIndex(
            currentAngle = cords.getAngle()
        )
    }
}

private fun List<Float>.getIndex(currentAngle: Float): Int {
    forEachIndexed { index, angle ->
        if (currentAngle < angle) return index
    }
    return 0
}

private fun Offset.getAngle(): Float {
    if (y == 0f) {
        return if (x > 0f) 90f else 270f
    }
    val angle = (atan(x / y) / PI * 180f).toFloat()
    return if (y > 0) {
        180 - angle
    } else {
        if (angle > 0) {
            360 - angle
        } else {
            -angle
        }
    }
}
