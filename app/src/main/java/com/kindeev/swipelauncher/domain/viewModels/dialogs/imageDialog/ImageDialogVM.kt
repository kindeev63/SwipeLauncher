package com.kindeev.swipelauncher.domain.viewModels.dialogs.imageDialog

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.entities.imageTypes.AllImageTypes
import com.kindeev.swipelauncher.domain.useCases.UserImagesUseCase

class ImageDialogVM(context: Context): ViewModel() {
    private val userImagesUseCase = UserImagesUseCase(context)

    private val _imageType = MutableLiveData<AllImageTypes?>(null)
    val imageType: LiveData<AllImageTypes?> = _imageType

    fun addUserImage(uri: Uri): UserImage {
        return userImagesUseCase.addUserImage(uri = uri)
    }

    fun setImageType(imageType: AllImageTypes) {
        _imageType.postValue(imageType)
    }

    fun clearImageType() {
        _imageType.postValue(null)
    }
}