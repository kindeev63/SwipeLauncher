package com.kindeev.swipelauncher.presentation.ui.elements.searchBox

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.OpenLastApp
import com.kindeev.swipelauncher.domain.utils.executeSearchResult
import com.kindeev.swipelauncher.domain.utils.getValueOf
import com.kindeev.swipelauncher.domain.viewModels.screens.launcherScreen.LauncherScreenVM

@Composable
fun SearchBoxUI(
    viewModel: LauncherScreenVM,
    onClose: () -> Unit
) {
    BackHandler(onBack = onClose)
    val searchText by viewModel.searchText.observeAsState("")
    val allApplicationInfo by LauncherData.allApplicationInfo.observeAsState(emptyList())
    val settings by LauncherData.settings.observeAsState(emptyList())
    val searchResults = viewModel.getSearchResults(allApplicationInfo)
    if (searchResults.size == 1 && settings.getValueOf(SettingNames.OpenLastApp, OpenLastApp::class.java)?.enabled == true) {
        searchResults.firstOrNull()?.let { LocalContext.current.executeSearchResult(it) }
        onClose()
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
        )
        SearchBoxSearchElement(searchText = searchText, onChangeText = { viewModel.search(it) })
        Spacer(modifier = Modifier.height(10.dp))
        SearchBoxResults(results = searchResults, onClose = onClose)
    }
}