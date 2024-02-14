package com.kindeev.swipelauncher.data

import androidx.compose.ui.graphics.ImageBitmap
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes

data class ImageTab(val name: String, val type: CircleMenuImageTypes)
data class ActionTab(val name: String, val type: CircleMenuActionTypes)
object DataObject {
    var allApplicationData = emptyList<ApplicationData>()
    var userImages = emptyMap<Int, ImageBitmap>()
    val imageDialogTabs = listOf(
        ImageTab(name = "Apps", type = CircleMenuImageTypes.AppImage),
        ImageTab(name = "Default", type = CircleMenuImageTypes.DefaultImage),
        ImageTab(name = "Own", type = CircleMenuImageTypes.UserImage),
    )
    val actionDialogTabs = listOf(
        ActionTab(name = "Apps", type = CircleMenuActionTypes.OpenApp),
        ActionTab(name = "CircleMenu", type = CircleMenuActionTypes.OpenCircleMenu),
        ActionTab(name = "Settings", type = CircleMenuActionTypes.OpenSettings),
    )
}