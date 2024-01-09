package com.kindeev.swipelauncher.presentation.screens

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.presentation.viewModels.AllCircleMenusScreenViewModel
import com.kindeev.swipelauncher.presentation.viewModels.factories.AllCircleMenusScreenViewModelFactory
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.uiElements.MiniCircleMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCircleMenusScreen(
    mainAppViewModel: MainAppViewModel,
    navigateToCircleMenu: (Int) -> Unit
) {
    val viewModel: AllCircleMenusScreenViewModel = viewModel(
        factory = AllCircleMenusScreenViewModelFactory(mainAppViewModel)
    )
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {

            }
        }
    ) {
        it
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(
                items = mainAppViewModel.allCircleMenu.value ?: emptyList()
            ) { circleMenu ->
                MiniCircleMenuItem(
                    size = LocalConfiguration.current.screenWidthDp / 2f,
                    circleMenu = circleMenu,
                    onClick = {
                        navigateToCircleMenu(circleMenu.id)
                    },
                    onLongClick = {

                    }
                )
            }
        }
    }
}