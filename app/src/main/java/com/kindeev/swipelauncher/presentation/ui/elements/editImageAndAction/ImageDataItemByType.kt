package com.kindeev.swipelauncher.presentation.ui.elements.editImageAndAction

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImage
import com.kindeev.swipelauncher.domain.getApplicationInfo
import com.kindeev.swipelauncher.domain.getResourceId
import com.kindeev.swipelauncher.domain.pickUserImageLauncher
import com.kindeev.swipelauncher.presentation.ui.dialogs.AppImageData
import com.kindeev.swipelauncher.presentation.ui.dialogs.DefaultImageData

@Composable
fun ImageDataByType(
    image: CircleMenuImage,
    onChangeImage: (CircleMenuImage) -> Unit
) {
    when (image) {
        is AppImage -> {
            AppImageDataItem(appImage = image, onChangeImage = onChangeImage)
        }
        is DefaultImage -> {
            DefaultImageDataItem(defaultImage = image, onChangeImage = onChangeImage)
        }
        is UserImage -> {
            UserImageDataItem(userImage = image, onChangeImage = onChangeImage)
        }
    }
}

@Composable
private fun AppImageDataItem(
    appImage: AppImage,
    onChangeImage: (CircleMenuImage) -> Unit
) {
    var showAppImageDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showAppImageDialog) {
        AppImageData(
            onPick = onChangeImage,
            onDismissRequest = { showAppImageDialog = false }
        )
    }
    val applicationData = LocalContext.current.getApplicationInfo(appImage.packageName)
    Column(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(7.dp))
            .clickable { showAppImageDialog = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(Constants.minScreenLength.dp / 3),
            bitmap = applicationData.icon,
            contentDescription = "App image"
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = applicationData.title,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = Constants.minScreenLength.sp / 20
        )
    }
}

@Composable
private fun DefaultImageDataItem(
    defaultImage: DefaultImage,
    onChangeImage: (CircleMenuImage) -> Unit
) {
    var showDefaultImageDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showDefaultImageDialog) {
        DefaultImageData(
            onPick = onChangeImage,
            onDismissRequest = { showDefaultImageDialog = false }
        )
    }
    Image(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(7.dp))
            .clickable { showDefaultImageDialog = true }
            .size(Constants.minScreenLength.dp / 3),
        painter = painterResource(id = defaultImage.data.getResourceId() ?: R.drawable.ic_error),
        contentDescription = "Default image"
    )
}

@Composable
private fun UserImageDataItem(
    userImage: UserImage,
    onChangeImage: (CircleMenuImage) -> Unit
) {
    val launcher = pickUserImageLauncher(onChangeImage)
    Image(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(7.dp))
            .clickable { launcher.launch("image/*") }
            .size(Constants.minScreenLength.dp / 3),
        bitmap = LauncherData.userImages[userImage.id] ?: throw IllegalArgumentException("Illegal UserImage"),
        contentDescription = "Default image"
    )
}