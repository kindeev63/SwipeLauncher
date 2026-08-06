package com.kindeev.swipelauncher.presentation.viewModels.settings.imageDialog.entities

import androidx.compose.ui.text.input.TextFieldValue
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImages
import com.kindeev.swipelauncher.domain.entities.imageTypes.ImageType

sealed class ImageDialogState {
    data class PickType(val searchText: TextFieldValue, val imageTypes: List<ImageType>): ImageDialogState()

    data class PickAppImage(val searchText: TextFieldValue, val applications: List<ApplicationInfo>): ImageDialogState()

    data class PickDefaultImage(val searchText: TextFieldValue, val defaultImages: List<DefaultImages>): ImageDialogState()
}