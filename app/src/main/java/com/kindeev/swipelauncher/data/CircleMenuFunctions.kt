package com.kindeev.swipelauncher.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.data.dataBaseElements.MenuActions
import com.kindeev.swipelauncher.data.dataBaseElements.MenuImages
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage

object CircleMenuFunctions {
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

    @Composable
    fun getItemImage(
        circleMenuImage: CircleMenuImage
    ): Painter? {
        return when (circleMenuImage.type) {

            CircleMenuImageTypes.DefaultImage -> {
                val defaultImage = circleMenuImage.data as DefaultImage
                painterResource(id = DefaultImagesValues.defaultImages[defaultImage] ?: return null)
            }

            CircleMenuImageTypes.AppImage -> {
                val appImage = circleMenuImage.data as AppImage
                DataObject.allApplicationData.find { it.packageName == appImage.packageName }?.let { applicationData ->
                    val imageBitmap = applicationData.icon
                    return remember(imageBitmap) { BitmapPainter(imageBitmap, filterQuality = DefaultFilterQuality) }
                }
                val context = LocalContext.current
                val applicationInfo = context.packageManager.getApplicationInfo(appImage.packageName, 0)
                val imageBitmap = applicationInfo.loadIcon(context.packageManager).toBitmap().asImageBitmap()
                return remember(imageBitmap) { BitmapPainter(imageBitmap, filterQuality = DefaultFilterQuality) }
            }

            else -> null
        }
    }

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
            upAction = CircleMenuAction(type = CircleMenuActionTypes.NoneAction),
            downAction = CircleMenuAction(type = CircleMenuActionTypes.NoneAction),
            rightAction = CircleMenuAction(type = CircleMenuActionTypes.NoneAction),
            leftAction = CircleMenuAction(type = CircleMenuActionTypes.NoneAction)
        )
    )
}