package com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.actionCategoryItem

import com.kindeev.swipelauncher.domain.entities.actionTypes.AllActionTypes
import java.io.Serializable

data class ActionCategoryItem(
    val name: String,
    val imageResId: Int,
    val type: AllActionTypes
): Serializable
