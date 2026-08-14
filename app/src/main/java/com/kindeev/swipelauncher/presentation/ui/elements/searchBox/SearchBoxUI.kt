package com.kindeev.swipelauncher.presentation.ui.elements.searchBox

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.LauncherScreenVM
import com.kindeev.swipelauncher.presentation.ui.dialogs.ApplicationInfoDialog
import com.kindeev.swipelauncher.presentation.ui.elements.MaterialIcon

@Composable
fun SearchBoxUI(
    viewModel: LauncherScreenVM,
    onClose: () -> Unit
) {
    BackHandler(onBack = onClose)
    val searchText by viewModel.searchText.collectAsState()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val settings by viewModel.settingsStateFlowUseCase.settings.collectAsStateWithLifecycle()
    var applicationInfoDialog by rememberSaveable {
        mutableStateOf<ApplicationInfo?>(null)
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    applicationInfoDialog?.let { applicationInfo ->
        ApplicationInfoDialog(
            viewModel = viewModel,
            applicationInfo = applicationInfo,
            onDismissRequest = { applicationInfoDialog = null }
        )
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                onClick = {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            ) {
                MaterialIcon(
                    modifier = Modifier
                        .size(24.dp),
                    unicode = "\ue312",
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
        },
        containerColor = Color.Black.copy(alpha = 0.2f)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.1f)
            )
            SearchBoxSearchElement(
                searchText = searchText,
                focusRequester= focusRequester,
                textColor = if (settings.blackTextColorOnWallpaper) Color.Black else Color.White,
                onChangeText = viewModel::search
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(
                    items = searchResults
                ) { applicationInfo ->
                    SearchAppItem(
                        title = applicationInfo.title,
                        packageName = applicationInfo.packageName,
                        textColor = if (settings.blackTextColorOnWallpaper) Color.Black else Color.White,
                        onClick = {
                            viewModel.openAppUseCase.open(applicationInfo.packageName)
                            onClose()
                        },
                        onLongClick = {
                            applicationInfoDialog = applicationInfo
                        }
                    )
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        if (settings.showKeyboardOnStartSearch) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }
}