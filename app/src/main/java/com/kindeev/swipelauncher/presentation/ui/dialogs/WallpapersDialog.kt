package com.kindeev.swipelauncher.presentation.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.viewModels.screens.wallpaperScreen.WallpaperScreenVM
import java.io.File

@Composable
fun WallpapersDialog(
    viewModel: WallpaperScreenVM,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    val wallpapers by viewModel.wallpapers.observeAsState(emptyList())
    val selectedWallpaperIds by viewModel.selectedWallpapersId.observeAsState(emptyList())
    val deleteWallpapersDialog by viewModel.deleteWallpapersDialog.observeAsState(false)

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.addWallpaper(uri) }
    }
    if (deleteWallpapersDialog) {
        QuestionDialog(
            text = stringResource(id = R.string.delete_wallpapers_question),
            onDismissRequest = { viewModel.hideDeleteWallpapersDialog() },
            onClickYes = { viewModel.deleteSelectedWallpapers() }
        )
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .heightIn(max = screenConfiguration.screenHeightDp.dp - 40.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2)
            ) {
                items(
                    items = wallpapers
                ) { wallpaper ->
                    WallpaperItem(
                        bitmap = wallpaper.bitmap,
                        picked = selectedWallpaperIds.contains(wallpaper.id),
                        onClick = { viewModel.clickOnWallpaper(wallpaper.id) },
                        onLongClick = { viewModel.longClickOnWallpaper(wallpaper.id) }
                    )
                }
            }
            WallpaperFAB(
                hasSelectedItems = selectedWallpaperIds.isNotEmpty(),
                onClickAdd = { launcher.launch("image/*") },
                onClickDelete = { viewModel.showDeleteWallpapersDialog() },
                onClickClose = { viewModel.clearSelectedWallpapers() },
                onClickSelectAll = { viewModel.selectAllWallpapers() }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WallpaperItem(
    bitmap: ImageBitmap,
    picked: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width((LocalConfiguration.current.screenWidthDp / 2.5).dp)
            .height((LocalConfiguration.current.screenHeightDp / 2.5).dp)
            .padding(3.dp)
    ) {
        Card(
            modifier = Modifier
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
            border = if (picked) BorderStroke(7.dp, Color.Black) else null,
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = "wallpaper",
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun WallpaperFAB(
    hasSelectedItems: Boolean,
    onClickAdd: () -> Unit,
    onClickDelete: () -> Unit,
    onClickClose: () -> Unit,
    onClickSelectAll: () -> Unit,
) {
    val fabSize = LocalConfiguration.current.screenWidthDp / 7
    val rotate by animateFloatAsState(
        targetValue = if (hasSelectedItems) -45f else 0f,
        label = "add or close"
    )
    Row(
        modifier = Modifier.padding(end = 20.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = hasSelectedItems,
            enter = fadeIn() + expandHorizontally(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    modifier = Modifier
                        .size(fabSize.dp)
                        .clip(RoundedCornerShape(20))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .clickable(onClick = onClickSelectAll)
                        .padding(fabSize.dp / 4),
                    painter = painterResource(id = R.drawable.select_all_image),
                    contentDescription = "select all"
                )
                Spacer(
                    modifier = Modifier.width((LocalConfiguration.current.screenWidthDp.dp - 100.dp - fabSize.dp * 9 / 2) / 2)
                )
                Image(
                    modifier = Modifier
                        .height(fabSize.dp)
                        .width((fabSize * 2.5).dp)
                        .clip(RoundedCornerShape(20))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .clickable(onClick = onClickDelete)
                        .padding(vertical = fabSize.dp / 4),
                    painter = painterResource(id = R.drawable.delete_image),
                    contentDescription = "select all"
                )
                Spacer(
                    modifier = Modifier.width((LocalConfiguration.current.screenWidthDp.dp - 100.dp - fabSize.dp * 9 / 2) / 2)
                )
            }
        }
        Image(
            modifier = Modifier
                .size(fabSize.dp)
                .clip(RoundedCornerShape(20))
                .background(MaterialTheme.colorScheme.tertiary)
                .clickable(onClick = if (hasSelectedItems) onClickClose else onClickAdd)
                .padding(fabSize.dp / 4)
                .rotate(rotate),
            painter = painterResource(id = R.drawable.ic_add),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface),
            contentDescription = "select all"
        )
    }
}