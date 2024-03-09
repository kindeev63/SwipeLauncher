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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.data.DataObject.AppDataObject.deleteApp
import com.kindeev.swipelauncher.data.DataObject.AppDataObject.openApp
import com.kindeev.swipelauncher.data.DataObject.SettingDataObject.openLastAppSettingValue
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel

@Composable
fun SearchBox(
    mainAppViewModel: MainAppViewModel,
    onDismissRequest: () -> Unit,
    openSettings: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var searchText by remember {
        mutableStateOf("")
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    BackHandler {
        onDismissRequest()
    }

    // UI
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.1f)
        )
        SearchElement(
            focusRequester = focusRequester,
            searchText = searchText,
            onTextChanged = { searchText = it }
        )
        Spacer(modifier = Modifier.height(10.dp))
        SearchResults(
            mainAppViewModel = mainAppViewModel,
            searchText = searchText,
            onDismissRequest = onDismissRequest,
            openSettings = openSettings,
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchElement(
    focusRequester: FocusRequester,
    searchText: String,
    onTextChanged: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
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
            onValueChange = onTextChanged
        )
        Spacer(modifier = Modifier.weight(0.1f))
    }

}

@Composable
private fun SearchResults(
    mainAppViewModel: MainAppViewModel,
    searchText: String,
    openSettings: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val allApplicationData by DataObject.allApplicationData.observeAsState(emptyList())
    val allSettings by mainAppViewModel.allSettings.observeAsState(emptyList())
    val settingsString = stringResource(
        id = R.string.launcher_settings
    )
    val context = LocalContext.current
    val allApplicationDataWithSettings = allApplicationData.toMutableList().apply {
        this.replaceAll { applicationData ->
            if (applicationData.packageName == context.packageName) {
                applicationData.copy(name = settingsString)
            } else applicationData
        }
    }
    val filteredApps =
        allApplicationDataWithSettings.filter {
            it.name.lowercase().contains(searchText.lowercase())
        }.sortedBy { it.name }
    if (filteredApps.size == 1 && openLastAppSettingValue(allSettings = allSettings)) {
        openApp(filteredApps.first().packageName, context)
        onDismissRequest()
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
                    if (applicationData.packageName == context.packageName) {
                        openSettings()
                    } else {
                        openApp(applicationData.packageName, context)
                    }
                    onDismissRequest()
                },
                onLongClick = {
                    deleteApp(applicationData.packageName, context)
                }
            )
        }
    }
}