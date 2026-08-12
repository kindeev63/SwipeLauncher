package com.kindeev.swipelauncher.presentation.ui.elements.searchBox

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp

@Composable
fun SearchBoxSearchElement(
    searchText: TextFieldValue,
    textColor: Color,
    focusRequester: FocusRequester,
    onChangeText: (TextFieldValue) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.weight(0.05f))
        BasicTextField(
            modifier = Modifier
                .weight(0.85f)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            textStyle = TextStyle(
                color = textColor,
                fontSize = (LocalConfiguration.current.screenWidthDp / 15).sp
            ),
            value = searchText,
            onValueChange = onChangeText
        )
        Spacer(modifier = Modifier.weight(0.1f))
    }
}