package com.kindeev.swipelauncher.presentation.screens

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.getOnlyChanged
import com.kindeev.swipelauncher.presentation.ui.elements.MiniCircleMenuItem
import com.kindeev.swipelauncher.presentation.ui.dialogs.QuestionDialog
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllCircleMenusScreen(
    navigateToCircleMenu: (Int?) -> Unit,
    onBackPressed: () -> Unit
) {

    val window = (LocalContext.current as Activity).window
    val view = LocalView.current
    val controller = WindowInsetsControllerCompat(window, view)
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        controller.isAppearanceLightStatusBars = false
    }
    BackHandler {
        scope.launch { controller.isAppearanceLightStatusBars = true }
        onBackPressed()
    }

    val context = LocalContext.current
    val allCircleMenus = LauncherData.allCircleMenus.observeAsState()
    var deleteCircleMenuDialog by remember {
        mutableStateOf<CircleMenu?>(null)
    }
    deleteCircleMenuDialog?.let { circleMenu ->
        QuestionDialog(
            text = stringResource(id = R.string.delete_circle_menu_question),
            onDismissRequest = { deleteCircleMenuDialog = null },
            onClickYes = {
                if (circleMenu.id != 0) {
                    scope.launch {
                        LauncherData.deleteCircleMenu(circleMenu)
                        LauncherData.allCircleMenus.value?.getOnlyChanged(context)?.let { LauncherData.insertCircleMenus(it) }
                    }
                }
                deleteCircleMenuDialog = null
            }
        )
    }
    Scaffold(
        topBar = {
            AllCircleMenusToolbar(
                onBackPressed = {
                    scope.launch { controller.isAppearanceLightStatusBars = true }
                    onBackPressed()
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSurface,
                onClick = { navigateToCircleMenu(null) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                )
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            columns = GridCells.Fixed(2)
        ) {
            items(
                items = allCircleMenus.value ?: emptyList()
            ) { circleMenu ->
                MiniCircleMenuItem(
                    size = LocalConfiguration.current.screenWidthDp / 2f,
                    root = circleMenu.id == 0,
                    circleMenu = circleMenu,
                    onClick = {
                        navigateToCircleMenu(circleMenu.id)
                    },
                    onLongClick = {
                        if (circleMenu.id == 0) navigateToCircleMenu(0) else deleteCircleMenuDialog =
                            circleMenu
                    }
                )
            }
        }
    }
}

@Composable
private fun AllCircleMenusToolbar(
    onBackPressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onBackPressed()
            }
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(id = R.string.all_circle_menus),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp
        )
    }
}