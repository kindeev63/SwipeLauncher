package com.kindeev.swipelauncher.presentation.ui.screens.settings.allCircleMenusScreen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.presentation.viewModels.settings.AllCircleMenusScreenVM
import com.kindeev.swipelauncher.presentation.ui.dialogs.QuestionDialog
import com.kindeev.swipelauncher.presentation.ui.elements.MaterialIcon
import com.kindeev.swipelauncher.presentation.ui.elements.MiniCircleMenuItem
import kotlinx.coroutines.launch

@Composable
fun AllCircleMenusScreen(
    viewModel: AllCircleMenusScreenVM,
) {
    viewModel.setScreenWidth(LocalConfiguration.current.screenWidthDp)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    BackHandler(onBack = viewModel::onBackPressed)
    val snackbarHostState = remember { SnackbarHostState() }
    val circleMenus by viewModel.circleMenus.collectAsStateWithLifecycle()
    val selectedMenuIds by viewModel.selectedMenuIds.collectAsStateWithLifecycle()
    val showDeleteMenusDialog by viewModel.showDeleteCircleMenusDialog.collectAsStateWithLifecycle()

    val pickJsonFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.importCircleMenus(uri) { result ->
                    scope.launch { snackbarHostState.showSnackbar(context.resources.getString(if (result) R.string.successfully else R.string.error)) }
                }
            }
        }
    )
    if (showDeleteMenusDialog) {
        QuestionDialog(
            text = stringResource(id = R.string.delete_circle_menus_question),
            onDismissRequest = viewModel::closeDeleteCircleMenusDialog,
            onClickYes = viewModel::deleteSelectedMenus
        )
    }
    val writeExternalStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
       if (isGranted) {
           viewModel.exportSelectedMenus { result ->
               scope.launch {
                   snackbarHostState.showSnackbar(
                       context.resources.getString(
                           if (result) R.string.backup_successfully else R.string.error
                       )
                   )
               }
           }
       } else {
           scope.launch {
               snackbarHostState.showSnackbar(context.resources.getString(R.string.write_storage_denied))
           }
       }
    }

    Scaffold(
        topBar = {
            AllCircleMenusToolbar(
                selectedMenusText = if (selectedMenuIds.isEmpty()) null else "${selectedMenuIds.count()} / ${circleMenus.count()}",
                onClickSelectAll = { viewModel.selectAllMenus() },
                onClickDelete = viewModel::showDeleteCircleMenusDialog,
                onClickImport = { pickJsonFile.launch("application/zip") },
                onClickExport = {
                    val isGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                    if (isGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        viewModel.exportSelectedMenus { result ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    context.resources.getString(
                                        if (result) R.string.backup_successfully else R.string.error
                                    )
                                )
                            }
                        }
                    } else {
                        writeExternalStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                },
                onClickClose = { viewModel.finishSelect() },
                onBackPressed = viewModel::onBackPressed
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                onClick = viewModel::addNewCircleMenu
            ) {
                MaterialIcon(
                    modifier = Modifier
                        .size(24.dp),
                    unicode = "\ue145",
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            columns = GridCells.Fixed(2)
        ) {
            items(
                items = circleMenus
            ) { circleMenu ->
                MiniCircleMenuItem(
                    size = LocalConfiguration.current.screenWidthDp / 2f,
                    selected = circleMenu.id in selectedMenuIds,
                    circleMenu = circleMenu,
                    onClick = {
                        viewModel.clickOnCircleMenuItem(circleMenu.id)
                    },
                    onLongClick = {
                        viewModel.longClickOnCircleMenuItem(circleMenu.id)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllCircleMenusToolbar(
    selectedMenusText: String?,
    onClickSelectAll: () -> Unit,
    onClickDelete: () -> Unit,
    onClickImport: () -> Unit,
    onClickExport: () -> Unit,
    onClickClose: () -> Unit,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = selectedMenusText ?: stringResource(id = R.string.all_circle_menus)
            )
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (selectedMenusText == null) onBackPressed() else onClickClose()
                    },
                contentAlignment = Alignment.Center
            ) {
                MaterialIcon(
                    modifier = Modifier
                        .size(24.dp),
                    unicode = "\ue5c4"
                )
            }
        },
        actions = {
            if (selectedMenusText == null) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClickImport),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialIcon(
                        modifier = Modifier
                            .size(24.dp),
                        unicode = "\uf090"
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClickSelectAll),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialIcon(
                        modifier = Modifier
                            .size(24.dp),
                        unicode = "\ue162"
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClickExport),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialIcon(
                        modifier = Modifier
                            .size(24.dp),
                        unicode = "\uf09b"
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onClickDelete),
                    contentAlignment = Alignment.Center
                ) {
                    MaterialIcon(
                        modifier = Modifier
                            .size(24.dp),
                        unicode = "\ue872"
                    )
                }
            }
        }
    )
}