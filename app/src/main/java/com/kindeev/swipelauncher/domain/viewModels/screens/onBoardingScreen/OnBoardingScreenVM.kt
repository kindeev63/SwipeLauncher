package com.kindeev.swipelauncher.domain.viewModels.screens.onBoardingScreen

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase

class OnBoardingScreenVM(context: Context): ViewModel() {
    private val getItemImageUseCase = GetItemImageUseCase(context)
    private val applicationsUseCase = ApplicationsUseCase(context, getItemImageUseCase)

    fun getThisAppIcon(): ImageBitmap {
        return applicationsUseCase.getThisAppIcon()
    }
}