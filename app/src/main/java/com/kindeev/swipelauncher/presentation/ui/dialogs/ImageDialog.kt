package com.kindeev.swipelauncher.presentation.ui.dialogs

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.ApplicationData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImage
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.imageTypes.AllImageTypes
import com.kindeev.swipelauncher.presentation.ui.elements.AppItem
import com.kindeev.swipelauncher.presentation.ui.elements.DialogSearchElement

@Composable
fun ImageDialog(
    onDismissRequest: () -> Unit,
    addUserImage: (Uri) -> UserImage?,
    getItemImage: (CircleMenuImage) -> ImageBitmap?,
    getAllApplicationsData: (List<ApplicationInfo>) -> List<ApplicationData>,
    onLaunchGetUserImage: () -> Unit = {},
    onPick: (CircleMenuImage) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val userImage = addUserImage(uri)
            if (userImage == null) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
            } else {
                onPick(userImage)
                onDismissRequest()
            }
        }
    }
    var imageType by rememberSaveable {
        mutableStateOf<AllImageTypes?>(null)
    }
    AllImageTypes(
        onPick = {
            if (it == AllImageTypes.UserImage) {
                onLaunchGetUserImage()
                launcher.launch("image/*")
            } else {
                imageType = it
            }
        },
        onDismissRequest = onDismissRequest
    )
    when (imageType) {
        AllImageTypes.AppImage -> {
            AppImageData(
                getItemImage = getItemImage,
                getAllApplicationsData = getAllApplicationsData,
                onPick = {
                    onPick(it)
                    onDismissRequest()
                },
                onDismissRequest = { imageType = null }
            )
        }

        AllImageTypes.DefaultImage -> {
            DefaultImageData(
                onPick = {
                    onPick(it)
                    onDismissRequest()
                },
                onDismissRequest = { imageType = null }
            )
        }
        else -> {}
    }
}

@Composable
private fun AllImageTypes(
    onPick: (AllImageTypes) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFBBDEFB))
                .padding(20.dp)
        ) {
            var searchText by rememberSaveable {
                mutableStateOf("")
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(items = Constants.imageTypes.filter {
                    it.name.lowercase().contains(searchText.lowercase())
                }) { imageType ->
                    ImageTypeElement(
                        name = imageType.name,
                        imageResId = imageType.imageResId,
                        onClick = { onPick(imageType.type) }
                    )
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = { searchText = it })
        }
    }
}

@Composable
private fun ImageTypeElement(
    name: String,
    imageResId: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1976D2))
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(id = imageResId),
                contentDescription = "Image type image"
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = name,
                color = Color.White
            )
        }
    }
}

@Composable
fun AppImageData(
    getItemImage: (CircleMenuImage) -> ImageBitmap?,
    getAllApplicationsData: (List<ApplicationInfo>) -> List<ApplicationData>,
    onPick: (CircleMenuImage) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFBBDEFB))
                .padding(20.dp)
        ) {
            val allApplicationInfo by LauncherData.allApplicationInfo.collectAsState()
            var searchText by rememberSaveable {
                mutableStateOf("")
            }
            LazyColumn {
                item { Spacer(modifier = Modifier.height(40.dp)) }
                items(
                    items = getAllApplicationsData(allApplicationInfo).filter {
                        it.title.lowercase().contains(searchText.lowercase())
                    },
                    key = { it.packageName }
                ) { applicationData ->
                    getItemImage(applicationData.image)?.let { image ->
                        AppItem(
                            title = applicationData.title,
                            image = image
                        ) {
                            onPick(AppImage(applicationData.packageName))
                            onDismissRequest()
                        }
                    }
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = { searchText = it })
        }
    }
}

@Composable
fun DefaultImageData(
    onPick: (CircleMenuImage) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFBBDEFB))
                .padding(20.dp)
        ) {
            var searchText by rememberSaveable {
                mutableStateOf("")
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(((Constants.minScreenLength.toInt() - 20) / 50))
            ) {
                repeat(((Constants.minScreenLength.toInt() - 20) / 50)) {
                    item { Spacer(modifier = Modifier.height(50.dp)) }
                }
                items(
                    items = Constants.defaultImages.keys.toList()
                        .filter { it.name.lowercase().contains(searchText.lowercase()) }
                ) { defaultImage ->
                    Image(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                onPick(DefaultImage(defaultImage))
                            },
                        painter = painterResource(
                            id = Constants.defaultImages[defaultImage] ?: R.drawable.ic_error
                        ),
                        contentDescription = null
                    )
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = { searchText = it })
        }
    }
}