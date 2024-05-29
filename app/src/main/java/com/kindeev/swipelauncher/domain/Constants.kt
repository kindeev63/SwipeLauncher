package com.kindeev.swipelauncher.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.entities.settings.Setting
import com.kindeev.swipelauncher.domain.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.entities.settings.settingTypes.ClickOnClock
import com.kindeev.swipelauncher.presentation.entities.ActionType
import com.kindeev.swipelauncher.presentation.entities.FlashlightActionType
import com.kindeev.swipelauncher.presentation.entities.ImageType
import com.kindeev.swipelauncher.presentation.entities.TelephoneActionType

object Constants {

    var actionTypes = listOf<ActionType>()
    var imageTypes = listOf<ImageType>()
    var flashlightActionTypes = listOf<FlashlightActionType>()
    var telephoneActionTypes = listOf<TelephoneActionType>()
    var minScreenLength = 0f
    var settingsTextSize = 0.sp
    val defaultSettings = listOf(
        SettingData(Setting.OpenLastApp, true),
        SettingData(Setting.ClickOnClock, ClickOnClock(false)),
        SettingData(Setting.BlackTextColorOnWallpaper, false),
        SettingData(Setting.PickAppActionWithImage, true)
    )

    val onBoarding2MenuImageResIds = listOf(
        R.drawable.on_boarding_2_1_image,
        R.drawable.on_boarding_2_2_image,
        R.drawable.on_boarding_2_3_image,
        R.drawable.on_boarding_2_4_image,
        R.drawable.on_boarding_2_5_image,
        R.drawable.on_boarding_2_6_image,
        R.drawable.on_boarding_2_7_image,
        R.drawable.on_boarding_2_8_image,
    )

    val menuCords = listOf(
        Offset(0f, -4f), // 1
        Offset(3f, -3f), // 2
        Offset(4f, 0f), // 3
        Offset(3f, 3f), // 4
        Offset(0f, 4f), // 5
        Offset(-3f, 3f), // 6
        Offset(-4f, 0f), // 7
        Offset(-3f, -3f), // 8
    )

    val defaultImages = mapOf(
        Pair(DefaultImage.Settings, R.drawable.ic_settings),
        Pair(DefaultImage.UpArrow, R.drawable.image_arrow_up),
        Pair(DefaultImage.DownArrow, R.drawable.image_arrow_down),
        Pair(DefaultImage.RightArrow, R.drawable.image_arrow_right),
        Pair(DefaultImage.LeftArrow, R.drawable.image_arrow_left),
        Pair(DefaultImage.Alarm, R.drawable.ic_alarm),
        Pair(DefaultImage.Time, R.drawable.ic_time),
        Pair(DefaultImage.Wallet, R.drawable.ic_wallet),
        Pair(DefaultImage.AccountBox, R.drawable.ic_account_box),
        Pair(DefaultImage.AccountCircle, R.drawable.ic_account_circle),
        Pair(DefaultImage.ObjectsTree, R.drawable.ic_objects_tree),
        Pair(DefaultImage.PopupNotification, R.drawable.ic_popup_notification),
        Pair(DefaultImage.MailA, R.drawable.ic_mail_a),
        Pair(DefaultImage.Apps, R.drawable.ic_apps),
        Pair(DefaultImage.Article, R.drawable.ic_article),
        Pair(DefaultImage.UncheckedTasks, R.drawable.ic_unchecked_tasks),
        Pair(DefaultImage.Brush, R.drawable.ic_brush),
        Pair(DefaultImage.Build, R.drawable.ic_build),
        Pair(DefaultImage.CheckedTasks, R.drawable.ic_checked_tasks),
        Pair(DefaultImage.Mail, R.drawable.ic_mail),
        Pair(DefaultImage.Extension, R.drawable.ic_extension),
        Pair(DefaultImage.Favourite, R.drawable.ic_favorite),
        Pair(DefaultImage.FlashLightOn, R.drawable.ic_flashlight_on),
        Pair(DefaultImage.FlashLightOff, R.drawable.ic_flashlight_off),
        Pair(DefaultImage.Error, R.drawable.ic_error),
    )
}