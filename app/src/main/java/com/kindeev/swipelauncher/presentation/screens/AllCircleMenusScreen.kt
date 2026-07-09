package com.kindeev.swipelauncher.presentation.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.di.container
import com.kindeev.swipelauncher.presentation.viewModels.allCircleMenus.AllCircleMenusScreenVM
import com.kindeev.swipelauncher.presentation.viewModels.allCircleMenus.AllCircleMenusScreenVMFactory
import com.kindeev.swipelauncher.presentation.ui.dialogs.QuestionDialog
import com.kindeev.swipelauncher.presentation.ui.elements.MiniCircleMenuItem
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AllCircleMenusScreen(
    navigateToCircleMenu: (Int?) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val window = (LocalContext.current as Activity).window
    val view = LocalView.current
    val controller = WindowInsetsControllerCompat(window, view)
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        controller.isAppearanceLightStatusBars = false
    }
    BackHandler {
        scope.launch { controller.isAppearanceLightStatusBars = true }
        onBackPressed()
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val viewModel: AllCircleMenusScreenVM = viewModel(
        factory = AllCircleMenusScreenVMFactory(context)
    )
    val allCircleMenus by context.container.circleMenus.collectAsState()
    val selectedMenuIds by viewModel.selectedMenuIds.collectAsState()

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
    var showDeleteMenusDialog by remember {
        mutableStateOf(false)
    }
    if (showDeleteMenusDialog) {
        QuestionDialog(
            text = stringResource(id = R.string.delete_circle_menus_question),
            onDismissRequest = { showDeleteMenusDialog = false },
            onClickYes = {
                viewModel.deleteSelectedMenus(allCircleMenus)
                showDeleteMenusDialog = false
            }
        )
    }
    val permissionState = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    Scaffold(
        topBar = {
            AllCircleMenusToolbar(
                selectedMenusText = if (selectedMenuIds.isEmpty()) null else "${selectedMenuIds.count()} / ${allCircleMenus.count()}",
                onClickSelectAll = { viewModel.selectAllMenus(allCircleMenus) },
                onClickDelete = { showDeleteMenusDialog = true },
                onClickImport = { pickJsonFile.launch("application/zip") },
                onClickExport = {
                    if (permissionState.status.isGranted || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        viewModel.exportSelectedMenus(allCircleMenus) { result ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    context.resources.getString(
                                        if (result) R.string.backup_successfuly else R.string.error
                                    )
                                )
                            }
                        }
                    } else {
                        if (permissionState.status.shouldShowRationale) {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.resources.getString(R.string.write_storage_denied))
                            }
                        } else {
                            scope.launch {
                                permissionState.launchPermissionRequest()
                            }
                        }
                    }

                },
                onClickClose = { viewModel.finishSelect() },
                onBackPressed = {
                    scope.launch { controller.isAppearanceLightStatusBars = true }
                    onBackPressed()
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSurface,
                onClick = { navigateToCircleMenu(null) }
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
                items = allCircleMenus
            ) { circleMenu ->
                MiniCircleMenuItem(
                    size = LocalConfiguration.current.screenWidthDp / 2f,
                    selected = circleMenu.id in selectedMenuIds,
                    root = circleMenu.id == 0,
                    circleMenu = circleMenu,
                    onClick = {
                        if (selectedMenuIds.isEmpty()) {
                            navigateToCircleMenu(circleMenu.id)
                        } else {
                            viewModel.changeSelectionStateOf(circleMenu)
                        }
                    },
                    onLongClick = {
                        viewModel.changeSelectionStateOf(circleMenu)
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