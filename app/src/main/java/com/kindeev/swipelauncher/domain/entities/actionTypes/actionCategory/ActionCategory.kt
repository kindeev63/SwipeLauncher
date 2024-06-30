package com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory

import java.io.Serializable

data class ActionCategory(
    val name: String,
    val imageResId: Int,
    val type: ActionCategories
): Serializable