package com.kindeev.swipelauncher.domain.viewModels.dialogs.wallpaperChangerInfoDialog

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase

class WallpaperChangerInfoDialogVM(context: Context): ViewModel() {
    private val applicationsUseCase = ApplicationsUseCase(context)
    private val packageName = context.packageName

    fun getAppDetails() {
        applicationsUseCase.getAppDetails(packageName)
    }
}