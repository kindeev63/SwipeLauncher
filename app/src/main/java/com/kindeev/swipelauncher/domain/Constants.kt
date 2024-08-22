package com.kindeev.swipelauncher.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImages
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.BlackTextColorOnWallpaper
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.ClickOnClock
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.OpenLastApp
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.PickAppActionWithImage
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.HomeScreenWallpaperChange
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.LockScreenWallpaperChange
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.WallpaperChangeType
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategory
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.actionCategoryItem.ActionCategoryItem
import com.kindeev.swipelauncher.domain.entities.imageTypes.ImageType

object Constants {
    const val WALLPAPER_CHANGE_SCREEN_INTENT_KEY = "screen"
    const val WALLPAPER_CHANGE_HOME_SCREEN_VALUE = 1
    const val WALLPAPER_CHANGE_LOCK_SCREEN_VALUE = 2
    const val WALLPAPER_CHANGE_INTENT_ACTION = "com.kindeev.swipelauncher.ACTION_CHANGE_WALLPAPER"
    var actionCategories = listOf<ActionCategory>()
    var imageTypes = listOf<ImageType>()
    var flashlightActionCategoryItems = listOf<ActionCategoryItem>()
    var telephoneActionCategoryItems = listOf<ActionCategoryItem>()
    var minScreenLength = 0f
    var settingsTextSize = 0.sp
    val defaultSettings = listOf(
        SettingData(SettingNames.OpenLastApp, OpenLastApp(true)),
        SettingData(SettingNames.ClickOnClock, ClickOnClock(false)),
        SettingData(SettingNames.BlackTextColorOnWallpaper, BlackTextColorOnWallpaper(false)),
        SettingData(SettingNames.PickAppActionWithImage, PickAppActionWithImage(true)),
        SettingData(
            SettingNames.HomeScreenWallpaperChange, HomeScreenWallpaperChange(
                enabled = false,
                changeType = WallpaperChangeType.Unlock
            )
        ),
        SettingData(
            SettingNames.LockScreenWallpaperChange, LockScreenWallpaperChange(
                enabled = false,
                changeType = WallpaperChangeType.Unlock
            )
        )
    )
    val wallpaperChangeTypeText = mapOf(
        Pair(WallpaperChangeType.ScreenOn, R.string.wallpaper_change_type_screen_on),
        Pair(WallpaperChangeType.ScreenOff, R.string.wallpaper_change_type_screen_off),
        Pair(WallpaperChangeType.Unlock, R.string.wallpaper_change_type_unlock),
        Pair(WallpaperChangeType.Time, R.string.wallpaper_change_type_time),
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