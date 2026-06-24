package com.kindeev.swipelauncher.data.mappers

import com.kindeev.swipelauncher.data.entities.applicationData.ApplicationDataEntity
import com.kindeev.swipelauncher.domain.entities.ApplicationData

fun ApplicationData.toEntity(): ApplicationDataEntity =
    ApplicationDataEntity(
        packageName = packageName,
        title = title,
        image = image.toEntity(),
        hidden = hidden
    )

fun ApplicationDataEntity.fromEntity(): ApplicationData =
    ApplicationData(
        packageName = packageName,
        title = title,
        image = image.fromEntity(),
        hidden = hidden
    )