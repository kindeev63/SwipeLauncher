package com.kindeev.swipelauncher.domain.viewModels.elements.cirlceMenuItems

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase

class CircleMenuItemsVM(context: Context): ViewModel() {
    private val getItemImageUseCase = GetItemImageUseCase(context)

    fun getItemImage(circleMenuImage: CircleMenuImage): ImageBitmap? {
        return getItemImageUseCase.getItemImage(circleMenuImage)
    }
}