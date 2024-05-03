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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.getApplicationData
import com.kindeev.swipelauncher.domain.getAs
import com.kindeev.swipelauncher.domain.pickUserImageLauncher
import com.kindeev.swipelauncher.presentation.ui.dialogs.AppImageData
import com.kindeev.swipelauncher.presentation.ui.dialogs.DefaultImageData

@Composable
fun ImageDataByType(
    image: CircleMenuImage,
    onChangeImage: (CircleMenuImage) -> Unit
) {
    when (image.type) {
        CircleMenuImageTypes.AppImage -> {
            AppImageDataItem(image = image, onChangeImage = onChangeImage)
        }
        CircleMenuImageTypes.DefaultImage -> {
            DefaultImageDataItem(image = image, onChangeImage = onChangeImage)
        }
        CircleMenuImageTypes.UserImage -> {
            UserImageDataItem(image = image, onChangeImage = onChangeImage)
        }
    }
}

@Composable
private fun AppImageDataItem(
    image: CircleMenuImage,
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
    val appImage = image.data.getAs(AppImage::class.java)
    val applicationData = LocalContext.current.getApplicationData(appImage.packageName)
    Column(
        modifier = Modifier
            .padding(5.dp)
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
            text = applicationData.name,
            color = Color.Black,
            fontSize = Constants.minScreenLength.sp / 20
        )
    }
}

@Composable
private fun DefaultImageDataItem(
    image: CircleMenuImage,
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
    val defaultImage = image.data.getAs(DefaultImage::class.java)
    Image(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(7.dp))
            .clickable { showDefaultImageDialog = true }
            .size(Constants.minScreenLength.dp / 3),
        painter = painterResource(id = Constants.defaultImages[defaultImage] ?: R.drawable.ic_error),
        contentDescription = "Default image"
    )
}

@Composable
private fun UserImageDataItem(
    image: CircleMenuImage,
    onChangeImage: (CircleMenuImage) -> Unit
) {
    val launcher = pickUserImageLauncher(onChangeImage)
    val userImage = image.data.getAs(UserImage::class.java)
    Image(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(7.dp))
            .clickable { launcher.launch("image/*") }
            .size(Constants.minScreenLength.dp / 3),
        bitmap = LauncherData.userImages[userImage.id] ?: throw IllegalArgumentException("Illegal UserImage"),
        contentDescription = "Default image"
    )
}