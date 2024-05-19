package com.kindeev.swipelauncher.presentation.ui.elements.searchBox

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kindeev.swipelauncher.presentation.entities.searchBox.AppSBR
import com.kindeev.swipelauncher.presentation.entities.searchBox.SearchBoxResult
import com.kindeev.swipelauncher.presentation.ui.elements.searchBox.results.AppSBRItem

@Composable
fun SearchBoxResults(
    results: List<SearchBoxResult>,
    onClose: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = results
        ) { result ->
            when (result) {
                is AppSBR -> {
                    AppSBRItem(data = result, onClose = onClose)
                }
            }
        }
    }
}