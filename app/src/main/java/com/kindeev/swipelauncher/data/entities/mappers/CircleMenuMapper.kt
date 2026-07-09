package com.kindeev.swipelauncher.data.entities.mappers

import com.kindeev.swipelauncher.data.entities.CircleMenuEntity
import com.kindeev.swipelauncher.data.entities.CircleMenuItemEntity
import com.kindeev.swipelauncher.data.entities.actions.CallEntityAction
import com.kindeev.swipelauncher.data.entities.actions.ChangeFlashLightConditionEntityAction
import com.kindeev.swipelauncher.data.entities.actions.CircleMenuEntityAction
import com.kindeev.swipelauncher.data.entities.actions.DialEntityAction
import com.kindeev.swipelauncher.data.entities.actions.FlashLightOffEntityAction
import com.kindeev.swipelauncher.data.entities.actions.FlashLightOnEntityAction
import com.kindeev.swipelauncher.data.entities.actions.OpenAppEntityAction
import com.kindeev.swipelauncher.data.entities.actions.OpenCircleMenuEntityAction
import com.kindeev.swipelauncher.data.entities.actions.OpenSettingsEntityAction
import com.kindeev.swipelauncher.data.entities.actions.OpenUrlEntityAction
import com.kindeev.swipelauncher.data.entities.images.AppEntityImage
import com.kindeev.swipelauncher.data.entities.images.CircleMenuEntityImage
import com.kindeev.swipelauncher.data.entities.images.DefaultEntityImage
import com.kindeev.swipelauncher.data.entities.images.DefaultEntityImages
import com.kindeev.swipelauncher.data.entities.images.UserEntityImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CallAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.ChangeFlashLightConditionAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.DialAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.FlashLightOffAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.FlashLightOnAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenAppAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenSettingsAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenUrlAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.UserImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImages

fun CircleMenu.toEntity(): CircleMenuEntity =
    CircleMenuEntity(
        id = id,
        title = title,
        items = items.map { it.toEntity() }
    )

fun CircleMenuEntity.fromEntity(): CircleMenu =
    CircleMenu(
        id = id,
        title = title,
        items = items.map { it.fromEntity() }
    )

internal fun CircleMenuItemEntity.fromEntity(): CircleMenuItem =
    CircleMenuItem(
        image = image.fromEntity(),
        action = action.fromEntity()
    )

internal fun CircleMenuEntityAction.fromEntity(): CircleMenuAction =
    when (this) {
        is CallEntityAction -> CallAction(phoneNumber)
        ChangeFlashLightConditionEntityAction -> ChangeFlashLightConditionAction
        is DialEntityAction -> DialAction(phoneNumber)
        FlashLightOffEntityAction -> FlashLightOffAction
        FlashLightOnEntityAction -> FlashLightOnAction
        is OpenAppEntityAction -> OpenAppAction(packageName)
        is OpenCircleMenuEntityAction -> OpenCircleMenuAction(id)
        OpenSettingsEntityAction -> OpenSettingsAction
        is OpenUrlEntityAction -> OpenUrlAction(url)
    }

internal fun CircleMenuEntityImage.fromEntity(): CircleMenuImage =
    when (this) {
        is AppEntityImage -> AppImage(packageName)
        is DefaultEntityImage -> DefaultImage(DefaultImages.valueOf(data.name))
        is UserEntityImage -> UserImage(id)
    }

internal fun CircleMenuItem.toEntity(): CircleMenuItemEntity =
    CircleMenuItemEntity(
        image = image.toEntity(),
        action = action.toEntity()
    )

internal fun CircleMenuImage.toEntity(): CircleMenuEntityImage =
    when (this) {
        is AppImage -> AppEntityImage(packageName)
        is DefaultImage -> DefaultEntityImage(DefaultEntityImages.valueOf(data.name))
        is UserImage -> UserEntityImage(id)
    }

internal fun CircleMenuAction.toEntity(): CircleMenuEntityAction =
    when (this) {
        is CallAction -> CallEntityAction(phoneNumber)
        ChangeFlashLightConditionAction -> ChangeFlashLightConditionEntityAction
        is DialAction -> DialEntityAction(phoneNumber)
        FlashLightOffAction -> FlashLightOffEntityAction
        FlashLightOnAction -> FlashLightOnEntityAction
        is OpenAppAction -> OpenAppEntityAction(packageName)
        is OpenCircleMenuAction -> OpenCircleMenuEntityAction(id)
        OpenSettingsAction -> OpenSettingsEntityAction
        is OpenUrlAction -> OpenUrlEntityAction(url)
    }