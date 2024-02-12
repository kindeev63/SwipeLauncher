package com.kindeev.swipelauncher.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.presentation.viewModels.AllCircleMenusScreenViewModel
import com.kindeev.swipelauncher.presentation.viewModels.factories.AllCircleMenusScreenViewModelFactory
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.uiElements.MiniCircleMenuItem
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.DeleteCircleMenuDialog

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCircleMenusScreen(
    mainAppViewModel: MainAppViewModel,
    navigateToCircleMenu: (Int?) -> Unit
) {
    val viewModel: AllCircleMenusScreenViewModel = viewModel(
        factory = AllCircleMenusScreenViewModelFactory(mainAppViewModel)
    )
    val allCircleMenus = mainAppViewModel.allCircleMenu.observeAsState()
    var deleteCircleMenuDialog by remember {
        mutableStateOf<CircleMenu?>(null)
    }
    deleteCircleMenuDialog?.let { circleMenu ->
        DeleteCircleMenuDialog(
            onDismissRequest = { deleteCircleMenuDialog = null },
            onClickDelete = {
                viewModel.deleteCircleMenu(circleMenu)
                deleteCircleMenuDialog = null
            }
        )
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateToCircleMenu(null) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(
                items = allCircleMenus.value ?: emptyList()
            ) { circleMenu ->
                MiniCircleMenuItem(
                    size = LocalConfiguration.current.screenWidthDp / 2f,
                    picked = circleMenu.id == 0,
                    circleMenu = circleMenu,
                    onClick = {
                        navigateToCircleMenu(circleMenu.id)
                    },
                    onLongClick = {
                        if (circleMenu.id == 0) navigateToCircleMenu(0) else deleteCircleMenuDialog = circleMenu
                    }
                )
            }
        }
    }
}