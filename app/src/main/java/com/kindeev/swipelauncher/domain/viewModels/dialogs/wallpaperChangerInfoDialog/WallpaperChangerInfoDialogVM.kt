package com.kindeev.swipelauncher.domain.viewModels.dialogs.wallpaperChangerInfoDialog

import android.content.Context
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase

class WallpaperChangerInfoDialogVM(context: Context): ViewModel() {
    private val getItemImageUseCase = GetItemImageUseCase(context)
    private val applicationsUseCase = ApplicationsUseCase(context, getItemImageUseCase)
    private val packageName = context.packageName

    fun getAppDetails() {
        applicationsUseCase.getAppDetails(packageName)
    }
}