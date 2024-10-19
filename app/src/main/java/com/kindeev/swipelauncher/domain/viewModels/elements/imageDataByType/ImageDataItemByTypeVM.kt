package com.kindeev.swipelauncher.domain.viewModels.elements.imageDataByType

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase
import com.kindeev.swipelauncher.domain.useCases.UserImagesUseCase

class ImageDataItemByTypeVM(context: Context): ViewModel() {
    private val getItemImageUseCase = GetItemImageUseCase(context)
    private val applicationsUseCase = ApplicationsUseCase(context, getItemImageUseCase)
    private val userImagesUseCase = UserImagesUseCase(context)

    fun getApplicationInfo(packageName: String): ApplicationInfo {
        return applicationsUseCase.getApplicationInfo(packageName)
    }

    fun addUserImage(uri: Uri): UserImage {
        return userImagesUseCase.addUserImage(uri)
    }
}