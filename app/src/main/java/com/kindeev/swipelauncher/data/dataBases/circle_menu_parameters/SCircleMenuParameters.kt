package com.kindeev.swipelauncher.data.dataBases.circle_menu_parameters

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kindeev.swipelauncher.domain.entities.circle_menu.parameters.CircleMenuItemOffset

@Entity(tableName = "table_circle_menu_parameters")
data class SCircleMenuParameters(
    @PrimaryKey
    val itemsCount: Int,
    val itemSize: Float,
    val offsets: List<CircleMenuItemOffset>
)