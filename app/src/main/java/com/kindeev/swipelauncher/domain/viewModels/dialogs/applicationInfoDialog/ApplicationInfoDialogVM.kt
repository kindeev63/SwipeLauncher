package com.kindeev.swipelauncher.domain.viewModels.dialogs.applicationInfoDialog

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.dataBase.entities.ApplicationData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase
import com.kindeev.swipelauncher.domain.useCases.GetMaskItemImageUseCase
import kotlinx.coroutines.launch

class ApplicationInfoDialogVM(context: Context, packageName: String) : ViewModel() {
    private val getItemImageUseCase = GetItemImageUseCase(context)
    private val applicationsUseCase = ApplicationsUseCase(context, getItemImageUseCase)
    private val getMaskItemImageUseCase =
        GetMaskItemImageUseCase(context, applicationsUseCase, getItemImageUseCase)
    val firstAppData = applicationsUseCase.getApplicationData(packageName)

    private val _appData = MutableLiveData(firstAppData)
    val appData: LiveData<ApplicationData> = _appData

    private val _imageDialogVisibility = MutableLiveData(false)
    val imageDialogVisibility: LiveData<Boolean> = _imageDialogVisibility

    fun showImageDialog() {
        _imageDialogVisibility.postValue(true)
    }

    fun hideImageDialog() {
        _imageDialogVisibility.postValue(false)
    }

    fun setAppDataImage(image: CircleMenuImage) {
        _appData.postValue(appData.value?.copy(image = image))
    }

    fun setAppDataTitle(title: String) {
        _appData.postValue(appData.value?.copy(title = title))
    }

    fun resetAppData() {
        appData.value?.packageName?.let {
            _appData.postValue(applicationsUseCase.getNotMaskApplicationData(it))
        }
    }

    fun getItemImage(): ImageBitmap? {
        appData.value?.let {
            return getMaskItemImageUseCase.getItemImageForApplicationInfoDialog(
                it.image,
                it.packageName
            )
        }
        return null
    }

    fun getAppDetails() {
        appData.value?.packageName?.let {
            applicationsUseCase.getAppDetails(it)
        }
    }

    fun deleteApp() {
        appData.value?.packageName?.let {
            applicationsUseCase.deleteApp(it)
        }
    }

    fun changeAppHiddenStatus() {
        appData.value?.let {
            _appData.postValue(it.copy(hidden = !it.hidden))
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            appData.value?.let {
                applicationsUseCase.changeApp(it)
                if (firstAppData.hidden != it.hidden) {
                    if (it.hidden) {
                        applicationsUseCase.hideApp(it.packageName)
                    } else {
                        applicationsUseCase.showApp(it.packageName)
                    }
                }
            }
        }
    }

    fun hasChanges(): Boolean {
        return appData.value != firstAppData
    }

}