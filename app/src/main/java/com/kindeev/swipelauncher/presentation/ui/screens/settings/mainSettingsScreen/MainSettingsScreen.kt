package com.kindeev.swipelauncher.presentation.ui.screens.settings.mainSettingsScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.kindeev.swipelauncher.presentation.entities.SettingsListItem
import com.kindeev.swipelauncher.presentation.ui.elements.settingsListItems.CategoryItem
import com.kindeev.swipelauncher.presentation.ui.elements.settingsListItems.HeaderItem
import com.kindeev.swipelauncher.presentation.viewModels.settings.mainSettingsScreen.MainSettingsScreenVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSettingsScreen(
    viewModel: MainSettingsScreenVM
) {
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainSettingsTopAppBar(scrollBehavior)
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(
                items = viewModel.settingCategories
            ) { item ->
                when (item) {
                    is SettingsListItem.Header -> {
                        HeaderItem(item.header)
                    }
                    is SettingsListItem.Category -> {
                        CategoryItem(
                            title = item.title,
                            description = item.description,
                            iconUnicode = item.iconUnicode,
                            onClick = {
                                viewModel.clickOnCategory(item.id)
                            }
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}