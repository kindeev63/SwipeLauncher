package com.kindeev.swipelauncher.presentation.ui.dialogs

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.pickUserImageLauncher
import com.kindeev.swipelauncher.presentation.ui.elements.AppItem

@Composable
fun ImageDialog(
    onDismissRequest: () -> Unit,
    onPick: (CircleMenuImage) -> Unit
) {
    val launcher = pickUserImageLauncher(
        onPick = {
            onPick(it)
            onDismissRequest()
        }
    )
    var imageType by rememberSaveable {
        mutableStateOf<CircleMenuImageTypes?>(null)
    }
    AllImageTypes(
        onPick = {
            if (it == CircleMenuImageTypes.UserImage) {
                launcher.launch("image/*")
            } else {
                imageType = it
            }
        },
        onDismissRequest = onDismissRequest
    )
    when (imageType) {
        CircleMenuImageTypes.AppImage -> {
            AppImageData(
                onPick = {
                    onPick(it)
                    onDismissRequest()
                },
                onDismissRequest = { imageType = null }
            )
        }

        CircleMenuImageTypes.DefaultImage -> {
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
    onPick: (CircleMenuImageTypes) -> Unit,
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
            SearchElement(searchText = searchText, onTextChange = { searchText = it })
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
                contentDescription = "Action type image"
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
private fun SearchElement(searchText: String, onTextChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF2196F3))
                .padding(horizontal = 15.dp, vertical = 5.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (searchText.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.search),
                    color = Color.White
                )
            }
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                textStyle = TextStyle(
                    color = Color.White,
                ),
                value = searchText,
                onValueChange = onTextChange
            )
        }
    }
}

@Composable
fun AppImageData(
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
            val allApplicationData = LauncherData.allApplicationData.observeAsState(emptyList())
            var searchText by rememberSaveable {
                mutableStateOf("")
            }
            LazyColumn {
                item { Spacer(modifier = Modifier.height(40.dp)) }
                items(
                    items = allApplicationData.value.filter {
                        it.name.lowercase().contains(searchText.lowercase())
                    },
                    key = { it.packageName }
                ) { applicationData ->
                    AppItem(
                        applicationData = applicationData
                    ) {
                        onPick(
                            CircleMenuImage(
                                type = CircleMenuImageTypes.AppImage,
                                data = AppImage(packageName = applicationData.packageName)
                            )
                        )
                        onDismissRequest()
                    }
                }
            }
            SearchElement(searchText = searchText, onTextChange = { searchText = it })
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
                columns = GridCells.Fixed(((LocalConfiguration.current.screenWidthDp - 20) / 50))
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(
                    items = Constants.defaultImages.keys.toList()
                        .filter { it.name.lowercase().contains(searchText.lowercase()) }
                ) { defaultImage ->
                    Image(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                onPick(
                                    CircleMenuImage(
                                        type = CircleMenuImageTypes.DefaultImage,
                                        data = defaultImage
                                    )
                                )
                            },
                        painter = painterResource(
                            id = Constants.defaultImages[defaultImage] ?: R.drawable.ic_error
                        ),
                        contentDescription = null
                    )
                }
            }
            SearchElement(searchText = searchText, onTextChange = { searchText = it })
        }
    }
}