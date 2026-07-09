package com.kindeev.swipelauncher.data.database.mappers

import com.kindeev.swipelauncher.data.database.entities.circleMenu.CircleMenuTable
import com.kindeev.swipelauncher.data.entities.mappers.fromEntity
import com.kindeev.swipelauncher.data.entities.mappers.toEntity
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu

fun CircleMenu.toTable(): CircleMenuTable =
    CircleMenuTable(
        id = id,
        title = title,
        items = items.map { it.toEntity() }
    )

fun CircleMenuTable.fromTable(): CircleMenu =
    CircleMenu(
        id = id,
        title = title,
        items = items.map { it.fromEntity() }
    )