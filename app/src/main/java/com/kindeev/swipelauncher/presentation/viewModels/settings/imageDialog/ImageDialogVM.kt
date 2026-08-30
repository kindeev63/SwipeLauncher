package com.kindeev.swipelauncher.presentation.viewModels.settings.imageDialog

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.data.applications.ApplicationsManager
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.EmptyImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.UserImage
import com.kindeev.swipelauncher.domain.entities.imageTypes.AllImageTypes
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import com.kindeev.swipelauncher.presentation.viewModels.settings.imageDialog.entities.ImageDialogState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageDialogVM(
    private val userImagesRepository: UserImagesRepository,
    applicationsManager: ApplicationsManager
) : ViewModel() {

    private val applications = applicationsManager.applications

    private val _state =
        MutableStateFlow<ImageDialogState>(ImageDialogState.PickType(TextFieldValue(""), Constants.imageTypes))
    val state: StateFlow<ImageDialogState> = _state.asStateFlow()

    private val _pickUserImage = MutableSharedFlow<Boolean>()
    val pickUserImage: SharedFlow<Boolean> = _pickUserImage.asSharedFlow()

    private val _pickImage = MutableSharedFlow<CircleMenuImage>()
    val pickImage: SharedFlow<CircleMenuImage> = _pickImage.asSharedFlow()

    fun openPickType() {
        _state.value = ImageDialogState.PickType(TextFieldValue(""), Constants.imageTypes)
    }

    fun search(searchText: TextFieldValue) {
        when (val currentState = state.value) {
            is ImageDialogState.PickAppImage -> {
                _state.value = currentState.copy(
                    searchText = searchText,
                    applications = applications.value.filter { it.title.lowercase().contains(searchText.text.lowercase()) }
                )
            }
            is ImageDialogState.PickDefaultImage -> {
                _state.value = currentState.copy(
                    searchText = searchText,
                    defaultImages = Constants.defaultImages.keys.toList().filter { it.name.lowercase().contains(currentState.searchText.text.lowercase()) }
                )
            }
            is ImageDialogState.PickType -> {
                _state.value = currentState.copy(
                    searchText = searchText,
                    imageTypes = Constants.imageTypes.filter { it.name.lowercase().contains(currentState.searchText.text.lowercase()) })
            }
        }
    }

    fun pickImage(image: CircleMenuImage) {
        viewModelScope.launch {
            _pickImage.emit(image)
        }
    }

    fun pickType(type: AllImageTypes) {
        when (type) {
            AllImageTypes.AppImage -> {
                _state.value = ImageDialogState.PickAppImage(searchText = TextFieldValue(""), applications = applications.value)
            }
            AllImageTypes.DefaultImage -> {
                _state.value = ImageDialogState.PickDefaultImage(
                    searchText = TextFieldValue(""),
                    defaultImages = Constants.defaultImages.keys.toList()
                )
            }
            AllImageTypes.UserImage -> {
                viewModelScope.launch {
                    _pickUserImage.emit(true)
                }
            }

            AllImageTypes.Empty -> {
                viewModelScope.launch {
                    _pickImage.emit(EmptyImage)
                }
            }
        }
    }

    fun onPickUserImage(uri: Uri?, onError: () -> Unit) {
        if (uri == null) {
            return
        }
        viewModelScope.launch {
            val id = userImagesRepository.insert(uri = uri)
            if (id == null) {
                onError()
            } else {
                viewModelScope.launch {
                    _pickImage.emit(UserImage(id))
                }
            }
        }
    }
}