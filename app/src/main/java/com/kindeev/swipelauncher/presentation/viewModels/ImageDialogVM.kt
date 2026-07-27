package com.kindeev.swipelauncher.presentation.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.UserImage
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageDialogVM(
    private val userImagesRepository: UserImagesRepository
): ViewModel() {

    suspend fun addUserImage(uri: Uri): UserImage? = withContext(Dispatchers.IO) {
        userImagesRepository.insert(uri = uri)?.let { UserImage(it) }
    }
}