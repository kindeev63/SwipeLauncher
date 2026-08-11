package com.kindeev.swipelauncher.presentation.ui.dialogs

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImages
import com.kindeev.swipelauncher.domain.entities.imageTypes.AllImageTypes
import com.kindeev.swipelauncher.domain.entities.imageTypes.ImageType
import com.kindeev.swipelauncher.presentation.ui.elements.AppItem
import com.kindeev.swipelauncher.presentation.ui.elements.DialogSearchElement
import com.kindeev.swipelauncher.presentation.viewModels.settings.imageDialog.entities.ImageDialogState
import com.kindeev.swipelauncher.presentation.viewModels.settings.imageDialog.ImageDialogVM

@Composable
fun ImageDialog(
    viewModel: ImageDialogVM,
    onDismissRequest: () -> Unit,
    onPick: (CircleMenuImage) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract =
            ActivityResultContracts.GetContent(),
        onResult = { uri ->
            viewModel.onPickUserImage(
                uri = uri,
                onSuccess = { userImage ->
                    onPick(userImage)
                    onDismissRequest()
                },
                onError = {
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    )
    LaunchedEffect(Unit) {
        viewModel.pickUserImage.collect {
            launcher.launch("image/*")
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val currentState = state) {
        is ImageDialogState.PickType -> {
            PickType(
                searchText = currentState.searchText,
                onSearch = viewModel::search,
                imageTypes = currentState.imageTypes,
                onPick = viewModel::pickType,
                onDismissRequest = onDismissRequest
            )
        }

        is ImageDialogState.PickAppImage -> {
            PickAppImage(
                searchText = currentState.searchText,
                onSearch = viewModel::search,
                applications = currentState.applications,
                onPick = { image ->
                    onPick(image)
                    onDismissRequest()
                },
                onDismissRequest = viewModel::openPickType,
            )
        }

        is ImageDialogState.PickDefaultImage -> {
            PickDefaultImage(
                searchText = currentState.searchText,
                onSearch = viewModel::search,
                defaultImages = currentState.defaultImages,
                onPick = { image ->
                    onPick(image)
                    onDismissRequest()
                },
                onDismissRequest = viewModel::openPickType
            )
        }
    }
}

@Composable
private fun PickType(
    searchText: TextFieldValue,
    onSearch: (TextFieldValue) -> Unit,
    imageTypes: List<ImageType>,
    onPick: (AllImageTypes) -> Unit,
    onDismissRequest: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(windowInfo.containerDpSize.width - 20.dp)
                .height(windowInfo.containerDpSize.height / 3 * 2)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(
                    items = imageTypes
                ) { imageType ->
                    ImageTypeElement(
                        name = imageType.name,
                        imageResId = imageType.imageResId,
                        onClick = { onPick(imageType.type) }
                    )
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = onSearch)
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
                .background(MaterialTheme.colorScheme.primary)
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
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun PickAppImage(
    searchText: TextFieldValue,
    onSearch: (TextFieldValue) -> Unit,
    applications: List<ApplicationInfo>,
    onPick: (AppImage) -> Unit,
    onDismissRequest: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(windowInfo.containerDpSize.width - 20.dp)
                .height(windowInfo.containerDpSize.height / 3 * 2)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            LazyColumn {
                item { Spacer(modifier = Modifier.height(40.dp)) }
                items(
                    items = applications,
                    key = { it.packageName }
                ) { applicationInfo ->
                    AppItem(
                        title = applicationInfo.title,
                        packageName = applicationInfo.packageName
                    ) {
                        onPick(AppImage(applicationInfo.packageName))
                        onDismissRequest()
                    }
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = onSearch)
        }
    }
}

@Composable
private fun PickDefaultImage(
    searchText: TextFieldValue,
    onSearch: (TextFieldValue) -> Unit,
    defaultImages: List<DefaultImages>,
    onPick: (DefaultImage) -> Unit,
    onDismissRequest: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(windowInfo.containerDpSize.width - 20.dp)
                .height(windowInfo.containerDpSize.height / 3 * 2)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(((Constants.minScreenLength.toInt() - 20) / 50))
            ) {
                repeat(((Constants.minScreenLength.toInt() - 20) / 50)) {
                    item { Spacer(modifier = Modifier.height(50.dp)) }
                }
                items(
                    items = defaultImages,
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
            DialogSearchElement(searchText = searchText, onTextChange = onSearch)
        }
    }
}