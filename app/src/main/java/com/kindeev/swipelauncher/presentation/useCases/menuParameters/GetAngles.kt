package com.kindeev.swipelauncher.presentation.useCases.menuParameters

fun getAngles(itemsCount: ItemsCount): List<Float> =
    if (itemsCount == 0) {
        emptyList()
    } else {
        (0 until itemsCount).map { 360f / itemsCount * (it + 0.5f) }
    }
