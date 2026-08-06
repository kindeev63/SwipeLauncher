package com.kindeev.swipelauncher.presentation.ui.screens.settings.allCircleMenusScreen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.presentation.viewModels.settings.AllCircleMenusScreenVM
import com.kindeev.swipelauncher.presentation.ui.dialogs.QuestionDialog
import com.kindeev.swipelauncher.presentation.ui.elements.MiniCircleMenuItem
import kotlinx.coroutines.launch

@Composable
fun AllCircleMenusScreen(
    viewModel: AllCircleMenusScreenVM,
) {
    viewModel.setScreenWidth(LocalConfiguration.current.screenWidthDp)
    val context = LocalContext.current
    val window = LocalActivity.current!!.window
    val view = LocalView.current
    val controller = WindowInsetsControllerCompat(window, view)
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        controller.isAppearanceLightStatusBars = false
    }
    BackHandler {
        viewModel.onBackPressed(
            changeStatusBar = {
                controller.isAppearanceLightStatusBars = true
            }
        )
    }
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
                           if (result) R.string.backup_successfuly else R.string.error
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
                                        if (result) R.string.backup_successfuly else R.string.error
                                    )
                                )
                            }
                        }
                    } else {
                        writeExternalStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                },
                onClickClose = { viewModel.finishSelect() },
                onBackPressed = {
                    viewModel.onBackPressed(
                        changeStatusBar = {
                            controller.isAppearanceLightStatusBars = true
                        }
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSurface,
                onClick = viewModel::addNewCircleMenu
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
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
                    root = circleMenu.id == 0,
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(MaterialTheme.colorScheme.primary)
            .shadow(elevation = 1.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (selectedMenusText == null) onBackPressed() else onClickClose()
            }
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = if (selectedMenusText == null) R.drawable.ic_back else R.drawable.ic_close),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = selectedMenusText ?: stringResource(id = R.string.all_circle_menus),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp
        )
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedMenusText == null) {
                IconButton(
                    onClick = {
                        onClickImport()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.import_image),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        onClickSelectAll()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.select_all_image),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                IconButton(
                    onClick = {
                        onClickExport()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.export_image),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                IconButton(
                    onClick = {
                        onClickDelete()
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.delete_image),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}