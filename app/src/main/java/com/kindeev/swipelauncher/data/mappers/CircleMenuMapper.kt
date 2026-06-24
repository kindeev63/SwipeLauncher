package com.kindeev.swipelauncher.data.mappers

import com.kindeev.swipelauncher.data.entities.circleMenu.CircleMenuEntity
import com.kindeev.swipelauncher.data.entities.circleMenu.CircleMenuEntityItem
import com.kindeev.swipelauncher.data.entities.circleMenu.actions.CallEntityAction
import com.kindeev.swipelauncher.data.entities.circleMenu.actions.ChangeFlashLightConditionEntityAction
import com.kindeev.swipelauncher.data.entities.circleMenu.actions.CircleMenuEntityAction
import com.kindeev.swipelauncher.data.entities.circleMenu.actions.DialEntityAction
import com.kindeev.swipelauncher.data.entities.circleMenu.actions.FlashLightOffEntityAction
import com.kindeev.swipelauncher.data.entities.circleMenu.actions.FlashLightOnEntityAction
import com.kindeev.swipelauncher.data.entities.circleMenu.actions.OpenAppEntityAction
import com.kindeev.swipelauncher.data.entities.circleMenu.actions.OpenCircleMenuEntityAction
import com.kindeev.swipelauncher.data.entities.circleMenu.actions.OpenSettingsEntityAction
import com.kindeev.swipelauncher.data.entities.circleMenu.actions.OpenUrlEntityAction
import com.kindeev.swipelauncher.data.entities.circleMenu.images.AppEntityImage
import com.kindeev.swipelauncher.data.entities.circleMenu.images.CircleMenuEntityImage
import com.kindeev.swipelauncher.data.entities.circleMenu.images.DefaultEntityImage
import com.kindeev.swipelauncher.data.entities.circleMenu.images.DefaultEntityImages
import com.kindeev.swipelauncher.data.entities.circleMenu.images.UserEntityImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.CallAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.ChangeFlashLightConditionAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.DialAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOffAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOnAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenSettingsAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenUrlAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImages

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

private fun CircleMenuEntityItem.fromEntity(): CircleMenuItem =
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

private fun CircleMenuItem.toEntity(): CircleMenuEntityItem =
    CircleMenuEntityItem(
        image = image.toEntity(),
        action = action.toEntity()
    )

internal fun CircleMenuImage.toEntity(): CircleMenuEntityImage =
    when (this) {
        is AppImage -> AppEntityImage(packageName)
        is DefaultImage -> DefaultEntityImage(DefaultEntityImages.valueOf(data.name))
        is UserImage -> UserEntityImage(id)
        else -> throw IllegalArgumentException("Illegal image type $this")
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
        else -> throw IllegalArgumentException("Illegal action type $this")
    }