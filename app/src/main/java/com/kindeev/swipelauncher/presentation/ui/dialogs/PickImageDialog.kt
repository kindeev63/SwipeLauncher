package com.kindeev.swipelauncher.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.entities.dialogTabs.DialogTab
import com.kindeev.swipelauncher.domain.getAs

@Composable
fun PickImageDialog(
    onDismissRequest: () -> Unit,
    picked: CircleMenuImage,
    onPick: (CircleMenuImage) -> Unit
) {
    var selectedImage by remember {
        mutableStateOf(picked)
    }
    var selectedTab by remember {
        mutableStateOf(
            when (picked.type) {
                CircleMenuImageTypes.DefaultImage -> DialogTab(R.string.default_image_tab)
                CircleMenuImageTypes.AppImage -> DialogTab(R.string.app_image_tab)
                CircleMenuImageTypes.UserImage -> DialogTab(R.string.user_image_tab)
            }
        )
    }
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                DialogTabs(
                    tabs = Constants.imageDialogTabs,
                    selectedTab = selectedTab,
                    onSelectTab = { selectedTab = it }
                )
                when (selectedTab.nameResourceId) {
                    R.string.app_image_tab -> {
                        PickAppTabContent(
                            pickedPackageName = if (selectedImage.type == CircleMenuImageTypes.AppImage) {
                                selectedImage.data.getAs(AppImage::class.java).packageName
                            } else null,
                            onPick = {
                                selectedImage = CircleMenuImage(
                                    type = CircleMenuImageTypes.AppImage,
                                    data = AppImage(packageName = it)
                                )
                            }
                        )
                    }

                    R.string.default_image_tab -> {
                        PickDefaultImageTabContent(
                            picked =
                            if (selectedImage.type == CircleMenuImageTypes.DefaultImage) {
                                selectedImage.data.getAs(DefaultImage::class.java)
                            } else null,
                            onPick = {
                                selectedImage = CircleMenuImage(
                                    type = CircleMenuImageTypes.DefaultImage,
                                    data = it
                                )
                            }
                        )
                    }

                    R.string.user_image_tab -> {
                        PickUserImageTabContent(
                            picked =
                            if (selectedImage.type == CircleMenuImageTypes.UserImage) {
                                selectedImage.data.getAs(UserImage::class.java)
                            } else null,
                            onPick = {
                                selectedImage = CircleMenuImage(
                                    type = CircleMenuImageTypes.UserImage,
                                    data = it
                                )
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(text = "Cancel")
                }
                TextButton(
                    onClick = {
                        onPick(selectedImage)
                    }
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}