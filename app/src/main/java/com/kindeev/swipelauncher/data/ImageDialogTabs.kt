package com.kindeev.swipelauncher.data

import com.kindeev.swipelauncher.R

sealed class ImageDialogTabs(val nameResourceId: Int) {
    object AppImageTab: ImageDialogTabs(nameResourceId = R.string.app_image_tab)
    object DefaultImageTab: ImageDialogTabs(nameResourceId = R.string.default_image_tab)
    object UserImageTab: ImageDialogTabs(nameResourceId = R.string.own)
}
