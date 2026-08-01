package com.kindeev.swipelauncher.presentation.ui.screens.settings.mainSettingsScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kindeev.swipelauncher.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSettingsTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    @OptIn(ExperimentalMaterial3Api::class)
    LargeTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.settings)
            )
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}