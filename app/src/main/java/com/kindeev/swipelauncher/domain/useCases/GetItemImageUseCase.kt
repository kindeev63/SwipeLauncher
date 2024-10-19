package com.kindeev.swipelauncher.domain.useCases

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImage
import com.kindeev.swipelauncher.domain.utils.getResourceId

class GetItemImageUseCase(
    private val context: Context
) {
    fun getItemImage(circleMenuImage: CircleMenuImage): ImageBitmap? {
        return when (circleMenuImage) {

            is DefaultImage -> {
                context.resources.getDrawable(circleMenuImage.data.getResourceId() ?: return null, context.theme)
                    .toBitmap().asImageBitmap()
            }

            is AppImage -> {
                LauncherData.allApplicationInfo.value?.find { it.packageName == circleMenuImage.packageName }?.icon
                    ?: context.packageManager.getApplicationInfo(circleMenuImage.packageName, 0)
                        .loadIcon(context.packageManager).toBitmap().asImageBitmap()
            }

            is UserImage -> {
                LauncherData.userImages[circleMenuImage.id]
            }

            else -> null
        }
    }
}