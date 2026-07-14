package com.kindeev.swipelauncher.presentation.mappers

import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Process
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.UserImage
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import com.kindeev.swipelauncher.presentation.entities.CircleMenuForUI
import com.kindeev.swipelauncher.presentation.entities.CircleMenuItemForUI

class CircleMenuForUIMapper(
    private val userImagesRepository: UserImagesRepository,
    context: Context,
) {

    private val appContext = context.applicationContext

    private val launcherApps: LauncherApps =
        appContext.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps

    fun map(circleMenu: CircleMenu): CircleMenuForUI =
        CircleMenuForUI(
            id = circleMenu.id,
            title = circleMenu.title,
            items = circleMenu.items.mapNotNull { it.toUI() }
        )

    private fun CircleMenuItem.toUI(): CircleMenuItemForUI? {
        return CircleMenuItemForUI(
            image = image,
            imageBitmap = image.toUI() ?: return null,
            action = action
        )
    }

    fun circleMenuImageToUI(image: CircleMenuImage): ImageBitmap? = image.toUI()
    private fun CircleMenuImage.toUI(): ImageBitmap? {
        return when (this) {
            is AppImage -> launcherApps.getApplicationInfo(
                packageName,
                PackageManager.GET_META_DATA,
                Process.myUserHandle()
            )
                .loadIcon(appContext.packageManager).toBitmap().asImageBitmap()

            is DefaultImage -> ResourcesCompat.getDrawable(
                appContext.resources,
                Constants.defaultImages[data] ?: return null,
                appContext.theme
            )?.toBitmap()?.asImageBitmap()

            is UserImage -> {
                val file = userImagesRepository.getFile(id)
                if (file.exists()) {
                    BitmapFactory.decodeFile(
                        userImagesRepository.getFile(id).absolutePath ?: return null
                    ).asImageBitmap()
                } else null
            }
        }
    }
}