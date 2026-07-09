package com.kindeev.swipelauncher.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class CircleMenuEntity(
    val id: Int,
    val title: String,
    val items: List<CircleMenuItemEntity>
)