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

class GetMaskItemImageUseCase(
    private val context: Context,
    private val applicationsUseCase: ApplicationsUseCase,
    private val getItemImageUseCase: GetItemImageUseCase
) {
    fun getItemImageForApplicationInfoDialog(
        circleMenuImage: CircleMenuImage,
        packageName: String,
    ): ImageBitmap? {
        return when (circleMenuImage) {

            is DefaultImage -> {
                context.resources.getDrawable(
                    circleMenuImage.data.getResourceId() ?: return null,
                    context.theme
                )
                    .toBitmap().asImageBitmap()
            }

            is AppImage -> {
                if (circleMenuImage.packageName == packageName) {
                    val applicationInfo =
                        context.packageManager.getApplicationInfo(circleMenuImage.packageName, 0)
                    applicationInfo.loadIcon(context.packageManager).toBitmap().asImageBitmap()
                } else {
                    val applicationData =
                        applicationsUseCase.getApplicationData(circleMenuImage.packageName)
                    if (applicationData.image is AppImage && applicationData.image.packageName == applicationData.packageName
                    ) {
                        val applicationInfo =
                            context.packageManager.getApplicationInfo(
                                circleMenuImage.packageName,
                                0
                            )
                        applicationInfo.loadIcon(context.packageManager).toBitmap().asImageBitmap()
                    } else {
                        getItemImageUseCase.getItemImage(applicationData.image)
                    }
                }
            }

            is UserImage -> {
                LauncherData.userImages[circleMenuImage.id]
            }

            else -> null
        }
    }
}