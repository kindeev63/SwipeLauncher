package com.kindeev.swipelauncher.domain.viewModels.screens.hiddenAppsScreen

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.dataBase.entities.ApplicationData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase
import kotlinx.coroutines.launch

class HiddenAppsScreenVM(context: Context): ViewModel() {
    private val applicationsUseCase = ApplicationsUseCase(context)
    private val getItemImageUseCase = GetItemImageUseCase(context)

    fun getItemImage(circleMenuImage: CircleMenuImage): ImageBitmap? {
        return getItemImageUseCase.getItemImage(circleMenuImage)
    }

    fun getHiddenApps(applicationsInfo: List<ApplicationInfo>): List<ApplicationData> {
        return applicationsUseCase.getAllApplicationData(applicationsUseCase.getHidden(applicationsInfo))
    }

    fun showApp(packageName: String) {
        viewModelScope.launch {
            applicationsUseCase.showApp(packageName)
        }
    }
}