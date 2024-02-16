package com.kindeev.swipelauncher.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.dataBaseElements.MenuActions
import com.kindeev.swipelauncher.data.dataBaseElements.MenuImages
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.UserImage

object DataObject {
    var allApplicationData = emptyList<ApplicationData>()
    var userImages = emptyMap<Int, ImageBitmap>()
    val imageDialogTabs = listOf(
        ImageDialogTabs.AppImageTab,
        ImageDialogTabs.DefaultImageTab,
        ImageDialogTabs.UserImageTab
    )
    val actionDialogTabs = listOf(
        ActionDialogTabs.OpenAppTab,
        ActionDialogTabs.OpenCircleMenuTab,
        ActionDialogTabs.OtherTab
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
    val otherActionsList = listOf(
        OtherAction(type = CircleMenuActionTypes.OpenSettings, nameResourceId = R.string.open_settings, image = DefaultImage.Settings),
        OtherAction(type = CircleMenuActionTypes.FlashLightOn, nameResourceId = R.string.flashlight_on, image = DefaultImage.FlashLightOn),
        OtherAction(type = CircleMenuActionTypes.FlashLightOff, nameResourceId = R.string.flashlight_off, image = DefaultImage.FlashLightOff),
        OtherAction(type = CircleMenuActionTypes.ChangeFlashLightCondition, nameResourceId = R.string.change_flashlight_condition, image = DefaultImage.FlashLightOn),
    )

    @Composable
    fun getRootCircleMenu(): CircleMenu {
        val menuImages = MenuImages(
            upImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.UpArrow
            ),
            downImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.Settings
            ),
            rightImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.RightArrow
            ),
            leftImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.LeftArrow
            )
        )
        val menuActions = MenuActions(
            upAction = CircleMenuAction(
                type = CircleMenuActionTypes.OpenCircleMenu,
                data = OpenCircleMenu(id = 0)
            ),
            downAction = CircleMenuAction(type = CircleMenuActionTypes.OpenSettings),
            rightAction = CircleMenuAction(
                type = CircleMenuActionTypes.OpenCircleMenu,
                data = OpenCircleMenu(id = 0)
            ),
            leftAction = CircleMenuAction(
                type = CircleMenuActionTypes.OpenCircleMenu,
                data = OpenCircleMenu(id = 0)
            )
        )
        return CircleMenu(
            title = stringResource(id = R.string.root),
            menuImages = menuImages,
            menuActions = menuActions
        )
    }
    @Composable
    fun getItemImage(
        circleMenuImage: CircleMenuImage
    ): Painter? {
        return when (circleMenuImage.type) {

            CircleMenuImageTypes.DefaultImage -> {
                val defaultImage = circleMenuImage.data as DefaultImage
                painterResource(id = defaultImages[defaultImage] ?: return null)
            }

            CircleMenuImageTypes.AppImage -> {
                val appImage = circleMenuImage.data as AppImage
                allApplicationData.find { it.packageName == appImage.packageName }
                    ?.let { applicationData ->
                        val imageBitmap = applicationData.icon
                        return remember(imageBitmap) {
                            BitmapPainter(
                                imageBitmap,
                                filterQuality = DrawScope.DefaultFilterQuality
                            )
                        }
                    }
                val context = LocalContext.current
                val applicationInfo =
                    context.packageManager.getApplicationInfo(appImage.packageName, 0)
                val imageBitmap =
                    applicationInfo.loadIcon(context.packageManager).toBitmap().asImageBitmap()
                return remember(imageBitmap) {
                    BitmapPainter(
                        imageBitmap,
                        filterQuality = DrawScope.DefaultFilterQuality
                    )
                }
            }

            CircleMenuImageTypes.UserImage -> {
                val userImage = circleMenuImage.data as UserImage
                val imageBitmap = userImages[userImage.id]
                imageBitmap?.let {
                    return remember(imageBitmap) {
                        BitmapPainter(
                            imageBitmap,
                            filterQuality = DrawScope.DefaultFilterQuality
                        )
                    }
                }
            }
        }
    }

    fun getItemsOffset(menuSize: Float, itemSize: Float) =
        listOf(
            // up
            Offset(
                x = menuSize / 2 - itemSize / 2,
                y = menuSize / 6 - itemSize / 2
            ),
            // down
            Offset(
                x = menuSize / 2 - itemSize / 2,
                y = menuSize / 6 * 5 - itemSize / 2
            ),
            // right
            Offset(
                x = menuSize / 6 * 5 - itemSize / 2,
                y = menuSize / 2 - itemSize / 2
            ),
            // left
            Offset(
                x = menuSize / 6 - itemSize / 2,
                y = menuSize / 2 - itemSize / 2
            )
        )

    fun createEmptyCircleMenu(id: Int, title: String = "") = CircleMenu(
        id = id,
        title = title,
        menuImages = MenuImages(
            upImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.UpArrow
            ),
            downImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.DownArrow
            ),
            rightImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.RightArrow
            ),
            leftImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.LeftArrow
            )
        ),
        menuActions = MenuActions(
            upAction = CircleMenuAction(type = CircleMenuActionTypes.OpenCircleMenu, data = OpenCircleMenu(id = 0)),
            downAction = CircleMenuAction(type = CircleMenuActionTypes.OpenCircleMenu, data = OpenCircleMenu(id = 0)),
            rightAction = CircleMenuAction(type = CircleMenuActionTypes.OpenCircleMenu, data = OpenCircleMenu(id = 0)),
            leftAction = CircleMenuAction(type = CircleMenuActionTypes.OpenCircleMenu, data = OpenCircleMenu(id = 0))
        )
    )
}