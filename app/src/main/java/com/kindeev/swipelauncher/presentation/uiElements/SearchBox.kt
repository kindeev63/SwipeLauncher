package com.kindeev.swipelauncher.presentation.uiElements

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.domain.DataObject
import com.kindeev.swipelauncher.domain.DataObject.SettingDataObject.openLastAppSettingValue
import com.kindeev.swipelauncher.domain.viewModels.LauncherScreen.LauncherScreenVM

@Composable
fun SearchBox(
    viewModel: LauncherScreenVM,
) {

    BackHandler { viewModel.closeSearchBox() }

    // UI
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.1f)
        )
        SearchElement(viewModel = viewModel)
        Spacer(modifier = Modifier.height(10.dp))
        SearchResults(viewModel = viewModel)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
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
                color = MaterialTheme.colorScheme.onPrimary,
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
    val allApplicationData by DataObject.allApplicationData.observeAsState(emptyList())
    val allSettings by viewModel.mainAppVM.allSettings.observeAsState(emptyList())
    val searchText by viewModel.searchText.observeAsState("")
    val filteredApps = viewModel.filterAllAppsToSearchBoxUseCase.invoke(allApplicationData, searchText)
    if (filteredApps.size == 1 && openLastAppSettingValue(allSettings = allSettings)) {
        viewModel.selectSearchElement(filteredApps.first().packageName)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = filteredApps,
            key = { it.packageName }
        ) { applicationData ->
            AppItem(
                applicationData = applicationData,
                textColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    viewModel.selectSearchElement(applicationData.packageName)
                },
                onLongClick = {
                    viewModel.deleteAppUseCase.invoke(applicationData.packageName)
                }
            )
        }
    }
}