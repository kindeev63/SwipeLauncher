package com.kindeev.swipelauncher.presentation.ui.elements.searchBox

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.di.container

@Composable
fun SearchBoxSearchElement(
    searchText: TextFieldValue,
    onChangeText: (TextFieldValue) -> Unit
) {
    val context = LocalContext.current
    val settings by context.container.settings.collectAsState()
    val focusRequester = remember { FocusRequester() }
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
                color = if (settings.blackTextColorOnWallpaper) Color.Black else Color.White,
                fontSize = (LocalConfiguration.current.screenWidthDp / 15).sp
            ),
            value = searchText,
            onValueChange = onChangeText
        )
        Spacer(modifier = Modifier.weight(0.1f))
    }
}