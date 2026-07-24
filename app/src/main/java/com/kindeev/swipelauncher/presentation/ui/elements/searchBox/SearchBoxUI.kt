package com.kindeev.swipelauncher.presentation.ui.elements.searchBox

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.LauncherScreenVM
import com.kindeev.swipelauncher.presentation.ui.dialogs.ApplicationInfoDialog

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

    applicationInfoDialog?.let { applicationInfo ->
        ApplicationInfoDialog(
            viewModel = viewModel,
            applicationInfo = applicationInfo,
            onDismissRequest = { applicationInfoDialog = null }
        )
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
        )
        SearchBoxSearchElement(
            searchText = searchText,
            textColor = if (settings.blackTextColorOnWallpaper) Color.Black else Color.White,
            onChangeText = viewModel::search
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize()
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