package com.kindeev.swipelauncher.presentation.ui.elements.searchBox

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun SearchBoxSearchElement(
    searchText: TextFieldValue,
    focusRequester: FocusRequester,
    pickFirstItem: () -> Unit,
    onChangeText: (TextFieldValue) -> Unit
) {
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Go
        ),
        textStyle = MaterialTheme.typography.headlineMedium.copy(
            color = Color.White
        ),
        maxLines = 1,
        keyboardActions = KeyboardActions(
            onGo = {
                pickFirstItem()
            }
        ),
        value = searchText,
        onValueChange = onChangeText
    )
}