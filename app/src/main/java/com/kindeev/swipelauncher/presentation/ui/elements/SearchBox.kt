package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.settings.Setting
import com.kindeev.swipelauncher.domain.getAppDetails
import com.kindeev.swipelauncher.domain.getValueOf
import com.kindeev.swipelauncher.domain.viewModels.LauncherScreenVM

@Composable
fun SearchBox(
    viewModel: LauncherScreenVM,
) {

    BackHandler { viewModel.closeSearchBox() }

    // UI
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
        )
        SearchElement(viewModel = viewModel)
        Spacer(modifier = Modifier.height(10.dp))
        SearchResults(viewModel = viewModel)
    }
}

@Composable
private fun SearchElement(
    viewModel: LauncherScreenVM
) {
    val focusRequester = remember { FocusRequester() }
    val searchText by viewModel.searchText.observeAsState("")
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.weight(0.05f))
        BasicTextField(
            modifier = Modifier
                .weight(0.85f)
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        keyboardController?.show()
                    } else {
                        keyboardController?.hide()
                    }
                },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            textStyle = TextStyle(
                color = LauncherData.textColorOnWallpaper,
                fontSize = (LocalConfiguration.current.screenWidthDp / 15).sp
            ),
            value = searchText,
            onValueChange = { viewModel.search(it) }
        )
        Spacer(modifier = Modifier.weight(0.1f))
    }
}

@Composable
private fun SearchResults(viewModel: LauncherScreenVM) {
    val context = LocalContext.current
    val allApplicationData by LauncherData.allApplicationData.observeAsState(emptyList())
    val settings by LauncherData.settings.observeAsState(emptyList())
    val searchText by viewModel.searchText.observeAsState("")
    val filteredApps = allApplicationData.filter {
        it.name.lowercase().contains(searchText.lowercase())
    }.sortedBy { it.name }
    if (filteredApps.size == 1 && settings.getValueOf(Setting.OpenLastApp, Boolean::class.java) == true) {
        viewModel.selectSearchElement(filteredApps.first().packageName)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = filteredApps,
            key = { it.packageName }
        ) { applicationData ->
            SwipeAppItem(
                applicationData = applicationData,
                onDelete = {
                    viewModel.deleteAppUseCase.invoke(applicationData.packageName)
                },
                onGetAppInfo = {
                    context.getAppDetails(applicationData.packageName)
                    viewModel.closeSearchBox()
                },
                onClick = {
                    viewModel.selectSearchElement(applicationData.packageName)
                }
            )
        }
    }
}