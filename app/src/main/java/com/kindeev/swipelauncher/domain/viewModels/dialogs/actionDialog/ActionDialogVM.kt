package com.kindeev.swipelauncher.domain.viewModels.dialogs.actionDialog

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.dataBase.entities.ApplicationData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase

class ActionDialogVM(context: Context): ViewModel() {
    private val getItemImageUseCase = GetItemImageUseCase(context)
    private val applicationsUseCase = ApplicationsUseCase(context)

    fun getItemImage(circleMenuImage: CircleMenuImage): ImageBitmap? {
        return getItemImageUseCase.getItemImage(circleMenuImage)
    }

    fun getAllApplicationsData(applicationsInfo: List<ApplicationInfo>): List<ApplicationData> {
        return applicationsUseCase.getAllApplicationData(applicationsInfo)
    }
}