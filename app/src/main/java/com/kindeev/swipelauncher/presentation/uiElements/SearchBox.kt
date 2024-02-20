package com.kindeev.swipelauncher.presentation.uiElements

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.data.DataObject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchBox(
    onDismissRequest: () -> Unit
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
            .combinedClickable(
                onClick = {},
                onDoubleClick = onDismissRequest,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
        )
        SearchElement(
            focusRequester = focusRequester,
            searchText = searchText,
            onTextChanged = { searchText = it }
        )
        Spacer(modifier = Modifier.height(10.dp))
        SearchResults(
            searchText = searchText,
            onDismissRequest = onDismissRequest
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
                fontSize = (LocalConfiguration.current.screenWidthDp/15).sp
            ),
            value = searchText,
            onValueChange = onTextChanged
        )
        Spacer(modifier = Modifier.weight(0.1f))
    }

}

@Composable
private fun SearchResults(
    searchText: String,
    onDismissRequest: () -> Unit
) {
    val allApplicationData = DataObject.allApplicationData.observeAsState(emptyList())
    val context = LocalContext.current
    val filteredApps = allApplicationData.value.filter { it.name.lowercase().contains(searchText.lowercase()) }
    if (filteredApps.size == 1) {
        DataObject.openApp(filteredApps.first().packageName, context)
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
                    DataObject.openApp(applicationData.packageName, context)
                    onDismissRequest()
                },
                onLongClick = {
                    DataObject.deleteApp(applicationData.packageName, context)
                }
            )
        }
    }
}