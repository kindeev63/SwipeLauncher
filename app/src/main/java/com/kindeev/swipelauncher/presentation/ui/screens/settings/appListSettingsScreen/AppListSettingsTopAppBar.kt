package com.kindeev.swipelauncher.presentation.ui.screens.settings.appListSettingsScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.presentation.ui.elements.MaterialIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListSettingsTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onBackPressed: () -> Unit
) {
    MediumTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_list_category_title)
            )
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBackPressed),
                contentAlignment = Alignment.Center
            ) {
                MaterialIcon(
                    modifier = Modifier
                        .size(24.dp),
                    unicode = "\ue5c4"
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}