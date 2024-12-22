package com.kindeev.swipelauncher.domain.viewModels.screens.onBoardingScreen

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase

class OnBoardingScreenVM(context: Context): ViewModel() {
    private val applicationsUseCase = ApplicationsUseCase(context)

    fun getThisAppIcon(): ImageBitmap {
        return applicationsUseCase.getThisAppIcon()
    }
}