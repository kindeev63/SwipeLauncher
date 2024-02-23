package com.kindeev.swipelauncher.presentation.uiElements.dialogs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.data.DataObject.getAs
import com.kindeev.swipelauncher.data.ImageDialogTabs
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.UserImage
import com.kindeev.swipelauncher.presentation.uiElements.AppItem
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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
                CircleMenuImageTypes.DefaultImage -> ImageDialogTabs.DefaultImageTab
                CircleMenuImageTypes.AppImage -> ImageDialogTabs.AppImageTab
                CircleMenuImageTypes.UserImage -> ImageDialogTabs.UserImageTab
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
                DialogTabs(selectedTab = selectedTab, onSelectTab = { selectedTab = it })
                when (selectedTab) {
                    ImageDialogTabs.AppImageTab -> {
                        PickAppImage(
                            picked =
                            if (selectedImage.type == CircleMenuImageTypes.AppImage) {
                                selectedImage.data.getAs(AppImage::class.java)
                            } else null,
                            onPick = { selectedImage = it })
                    }

                    ImageDialogTabs.DefaultImageTab -> {
                        PickDefaultImage(
                            picked =
                            if (selectedImage.type == CircleMenuImageTypes.DefaultImage) {
                                selectedImage.data.getAs(DefaultImage::class.java)
                            } else null,
                            onPick = { selectedImage = it })
                    }

                    ImageDialogTabs.UserImageTab -> {
                        PickOwnImage(
                            picked =
                            if (selectedImage.type == CircleMenuImageTypes.UserImage) {
                                selectedImage.data.getAs(UserImage::class.java)
                            } else null,
                            onPick = { selectedImage = it })
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

@Composable
private fun PickAppImage(
    picked: AppImage?,
    onPick: (CircleMenuImage) -> Unit
) {
    val allApplicationData = DataObject.allApplicationData.observeAsState(emptyList())
    LazyColumn {
        items(
            items = allApplicationData.value,
            key = { it.packageName }
        ) { applicationData ->
            AppItem(
                applicationData = applicationData,
                picked = applicationData.packageName == picked?.packageName
            ) {
                onPick(
                    CircleMenuImage(
                        type = CircleMenuImageTypes.AppImage,
                        data = AppImage(packageName = applicationData.packageName)
                    )
                )
            }
        }
    }
}

@Composable
private fun PickDefaultImage(
    picked: DefaultImage?,
    onPick: (CircleMenuImage) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(((LocalConfiguration.current.screenWidthDp - 20) / 50).toInt())
    ) {
        items(
            items = DataObject.defaultImages.keys.toList()
        ) { defaultImage ->
            Image(
                modifier = Modifier
                    .size(50.dp)
                    .background(if (defaultImage == picked) Color.Gray.copy(alpha = 0.5f) else Color.Transparent)
                    .clickable {
                        onPick(
                            CircleMenuImage(
                                type = CircleMenuImageTypes.DefaultImage,
                                data = defaultImage
                            )
                        )
                    },
                painter = painterResource(
                    id = DataObject.defaultImages[defaultImage] ?: R.drawable.ic_error
                ),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun PickOwnImage(
    picked: UserImage?,
    onPick: (CircleMenuImage) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val ids = DataObject.userImages.map { it.key }
            var newId = 0
            while (newId in ids) {
                newId++
            }
            val bitmap = createBitmapByUri(context, uri)
            DataObject.userImages = DataObject.userImages.toMutableMap().apply {
                this[newId] = bitmap.asImageBitmap()
            }.toMap()
            createNewImageFile(context, "$newId.png", bitmap)
            onPick(
                CircleMenuImage(
                    type = CircleMenuImageTypes.UserImage,
                    data = UserImage(id = newId)
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (picked == null) {
            Button(onClick = { launcher.launch("image/*") }) {
                Text(text = stringResource(id = R.string.pick_image))
            }
        } else {
            val painter = DataObject.getItemImage(
                circleMenuImage = CircleMenuImage(
                    type = CircleMenuImageTypes.UserImage,
                    data = picked
                )
            )
            if (painter == null) {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text(text = stringResource(id = R.string.pick_image))
                }
            } else {
                Image(
                    modifier = Modifier.size((LocalConfiguration.current.screenWidthDp / 4).dp),
                    painter = painter,
                    contentDescription = null
                )

            }
        }
    }
}

private fun createNewImageFile(context: Context, fileName: String, bitmap: Bitmap) {
    val file = File(context.filesDir, fileName)
    file.createNewFile()
    val fos = FileOutputStream(file)
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
    fos.write(bos.toByteArray())
    fos.flush()
    fos.close()
}

private fun createBitmapByUri(context: Context, uri: Uri) = if (Build.VERSION.SDK_INT < 28) {
    MediaStore.Images
        .Media.getBitmap(context.contentResolver, uri)

} else {
    ImageDecoder.decodeBitmap(
        ImageDecoder
            .createSource(context.contentResolver, uri)
    )
}

@Composable
private fun DialogTabs(
    selectedTab: ImageDialogTabs,
    onSelectTab: (ImageDialogTabs) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = DataObject.imageDialogTabs.indexOf(selectedTab),
        edgePadding = 0.dp
    ) {
        DataObject.imageDialogTabs.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = {
                    if (selectedTab != tab) onSelectTab(tab)
                },
                text = {
                    Text(
                        text = stringResource(id = tab.nameResourceId),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            )
        }
    }
}