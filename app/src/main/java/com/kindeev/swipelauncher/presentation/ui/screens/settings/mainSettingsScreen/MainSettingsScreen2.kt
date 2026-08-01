package com.kindeev.swipelauncher.presentation.ui.screens.settings.mainSettingsScreen

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.kindeev.swipelauncher.presentation.ui.screens.settings.mainSettingsScreen.listItems.CategoryItem
import com.kindeev.swipelauncher.presentation.ui.screens.settings.mainSettingsScreen.listItems.HeaderItem
import com.kindeev.swipelauncher.presentation.ui.theme.LauncherTheme
import com.kindeev.swipelauncher.presentation.viewModels.mainSettingsScreen.MainSettingsScreen2VM
import com.kindeev.swipelauncher.presentation.viewModels.mainSettingsScreen.entities.SettingListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSettingsScreen2(
    viewModel: MainSettingsScreen2VM
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
                    is SettingListItem.Header -> {
                        HeaderItem(item)
                    }
                    is SettingListItem.Category -> {
                        CategoryItem(item) {
                            viewModel.clickOnCategory(item.category)
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun MainSettingsScreenPreview() {
    LauncherTheme {
        MainSettingsScreen2(
            MainSettingsScreen2VM(LocalContext.current)
        )
    }
}