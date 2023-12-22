package com.kindeev.swipelauncher.data

import android.util.Log
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
import com.kindeev.swipelauncher.R
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
    fun getItemImage(circleMenuImage: CircleMenuImage): Painter {
        return when (circleMenuImage.type) {

            CircleMenuImageTypes.DefaultImage -> {
                val defaultImage = circleMenuImage.data as DefaultImage
                painterResource(id = defaultImage.id)
            }

            CircleMenuImageTypes.AppImage -> {
                val appImage = circleMenuImage.data as AppImage
                val context = LocalContext.current
                val applicationInfo = context.packageManager.getApplicationInfo(appImage.packageName, 0)
                val imageBitmap = applicationInfo.loadIcon(context.packageManager).toBitmap().asImageBitmap()
                remember(imageBitmap) { BitmapPainter(imageBitmap, filterQuality = DefaultFilterQuality) }
            }
        }
    }
}