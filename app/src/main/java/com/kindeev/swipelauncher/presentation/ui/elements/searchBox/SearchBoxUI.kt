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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.settings.Setting
import com.kindeev.swipelauncher.domain.executeSearchResult
import com.kindeev.swipelauncher.domain.getNotHidden
import com.kindeev.swipelauncher.domain.getValueOf
import com.kindeev.swipelauncher.presentation.entities.searchBox.AppSBR
import com.kindeev.swipelauncher.presentation.entities.searchBox.SearchBoxResult

@Composable
fun SearchBoxUI(
    onClose: () -> Unit
) {
    BackHandler(onBack = onClose)
    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    val allApplicationInfo by LauncherData.allApplicationInfo.observeAsState(emptyList())
    val settings by LauncherData.settings.observeAsState(emptyList())
    val searchResults = searchText.getSearchResults(allApplicationInfo)
    if (searchResults.size == 1 && settings.getValueOf(Setting.OpenLastApp, Boolean::class.java) == true) {
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
        SearchBoxSearchElement(searchText = searchText, onChangeText = { searchText = it })
        Spacer(modifier = Modifier.height(10.dp))
        SearchBoxResults(results = searchResults, onClose = onClose)
    }
}

fun String.getSearchResults(allApplicationInfo: List<ApplicationInfo>): List<SearchBoxResult> {
    return allApplicationInfo.getNotHidden().filter { it.title.contains(this) }.map { AppSBR(it) }
}