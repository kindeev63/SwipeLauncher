package com.kindeev.swipelauncher.domain

import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.entities.dialogTabs.OtherAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.entities.dialogTabs.DialogTab
import com.kindeev.swipelauncher.domain.entities.settings.ApplicationSetting
import com.kindeev.swipelauncher.domain.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.entities.settings.settingTypes.ClickableClock
import com.kindeev.swipelauncher.domain.entities.settings.settingTypes.OpenLastApp
import com.kindeev.swipelauncher.presentation.ui.dialogs.ActionType
import com.kindeev.swipelauncher.presentation.ui.dialogs.FlashlightActionType
import com.kindeev.swipelauncher.presentation.ui.dialogs.TelephoneActionType

object Constants {

    var actionTypes = listOf<ActionType>()
    var flashlightActionTypes = listOf<FlashlightActionType>()
    var telephoneActionTypes = listOf<TelephoneActionType>()

    val imageDialogTabs = listOf(
        DialogTab(R.string.app_image_tab),
        DialogTab(R.string.default_image_tab),
        DialogTab(R.string.user_image_tab)
    )
    val otherActionsList = listOf(
        OtherAction(
            type = CircleMenuActionTypes.OpenSettings,
            nameResourceId = R.string.open_settings,
            image = DefaultImage.Settings
        ),
        OtherAction(
            type = CircleMenuActionTypes.FlashLightOn,
            nameResourceId = R.string.flashlight_on,
            image = DefaultImage.FlashLightOn
        ),
        OtherAction(
            type = CircleMenuActionTypes.FlashLightOff,
            nameResourceId = R.string.flashlight_off,
            image = DefaultImage.FlashLightOff
        ),
        OtherAction(
            type = CircleMenuActionTypes.ChangeFlashLightCondition,
            nameResourceId = R.string.change_flashlight_condition,
            image = DefaultImage.FlashLightOn
        ),
    )

    val settingsNames = mapOf(
        Pair(ApplicationSetting.OpenAllCircleMenus, R.string.all_circle_menus),
        Pair(ApplicationSetting.OpenLastApp, R.string.open_last_app),
        Pair(ApplicationSetting.ClickableClock, R.string.clickable_clock),
        Pair(ApplicationSetting.ChangeDefaultLauncher, R.string.change_default_launcher),
    )
    val defaultSettings = listOf(
        SettingData(ApplicationSetting.OpenLastApp, OpenLastApp(true)),
        SettingData(ApplicationSetting.OpenAllCircleMenus),
        SettingData(ApplicationSetting.ClickableClock, ClickableClock(false)),
        SettingData(ApplicationSetting.ChangeDefaultLauncher)
    )

    val defaultImages = mapOf(
        Pair(DefaultImage.Settings, R.drawable.ic_settings),
        Pair(DefaultImage.UpArrow, R.drawable.ic_up_arrow),
        Pair(DefaultImage.DownArrow, R.drawable.ic_down_arrow),
        Pair(DefaultImage.RightArrow, R.drawable.ic_right_arrow),
        Pair(DefaultImage.LeftArrow, R.drawable.ic_left_arrow),
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