package com.kindeev.swipelauncher.domain

import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.entities.actionTypes.FlashlightActionType
import com.kindeev.swipelauncher.domain.entities.actionTypes.TelephoneActionType
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImages
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategory
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenSettingsAction
import com.kindeev.swipelauncher.domain.entities.imageTypes.ImageType
import com.kindeev.swipelauncher.domain.entities.settings.ClickOnClock
import com.kindeev.swipelauncher.domain.entities.settings.LauncherSettings

object Constants {
    const val USER_IMAGES_DIR = "UserImages"
    var imageTypes = listOf<ImageType>()
    var actionCategories = listOf<ActionCategory>()
    var telephoneActionTypes = listOf<TelephoneActionType>()
    var flashlightActionTypes = listOf<FlashlightActionType>()
    var minScreenLength = 0f
    val defaultSettings = LauncherSettings(
        blackTextColorOnWallpaper = false,
        clickOnClock = ClickOnClock(
            false,
            OpenSettingsAction
        ),
        openLastApp = true,
        pickAppActionWithImage = true,
        showKeyboardOnStartSearch = true
    )

    val defaultImages = mapOf(
        Pair(DefaultImages.Settings, R.drawable.ic_settings),
        Pair(DefaultImages.UpArrow, R.drawable.image_arrow_up),
        Pair(DefaultImages.DownArrow, R.drawable.image_arrow_down),
        Pair(DefaultImages.RightArrow, R.drawable.image_arrow_right),
        Pair(DefaultImages.LeftArrow, R.drawable.image_arrow_left),
        Pair(DefaultImages.Alarm, R.drawable.ic_alarm),
        Pair(DefaultImages.Time, R.drawable.ic_time),
        Pair(DefaultImages.Wallet, R.drawable.ic_wallet),
        Pair(DefaultImages.AccountBox, R.drawable.ic_account_box),
        Pair(DefaultImages.AccountCircle, R.drawable.ic_account_circle),
        Pair(DefaultImages.ObjectsTree, R.drawable.ic_objects_tree),
        Pair(DefaultImages.PopupNotification, R.drawable.ic_popup_notification),
        Pair(DefaultImages.MailA, R.drawable.ic_mail_a),
        Pair(DefaultImages.Apps, R.drawable.ic_apps),
        Pair(DefaultImages.Article, R.drawable.ic_article),
        Pair(DefaultImages.UncheckedTasks, R.drawable.ic_unchecked_tasks),
        Pair(DefaultImages.Brush, R.drawable.ic_brush),
        Pair(DefaultImages.Build, R.drawable.ic_build),
        Pair(DefaultImages.CheckedTasks, R.drawable.ic_checked_tasks),
        Pair(DefaultImages.Mail, R.drawable.ic_mail),
        Pair(DefaultImages.Extension, R.drawable.ic_extension),
        Pair(DefaultImages.Favourite, R.drawable.ic_favorite),
        Pair(DefaultImages.FlashLightOn, R.drawable.ic_flashlight_on),
        Pair(DefaultImages.FlashLightOff, R.drawable.ic_flashlight_off),
        Pair(DefaultImages.Error, R.drawable.ic_error),
    )
}