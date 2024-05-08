package com.kindeev.swipelauncher.presentation.screens

import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.CircleMenuDirection
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.viewModels.EditCircleMenuScreenVM
import com.kindeev.swipelauncher.domain.viewModels.EditCircleMenuScreenVMFactory
import com.kindeev.swipelauncher.presentation.ui.elements.CircleMenuForEditUI
import com.kindeev.swipelauncher.presentation.ui.elements.editImageAndAction.ImageAndAction
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCircleMenuScreen(
    circleMenuId: Int?,
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
    // ViewModel
    val viewModel: EditCircleMenuScreenVM = viewModel(
        factory = EditCircleMenuScreenVMFactory(circleMenuId)
    )

    // Checking for update circle menus
    LauncherData.allCircleMenus.observe(LocalLifecycleOwner.current) {
        viewModel.updateCircleMenusEvent(it)
    }

    // States
    val circleMenu = viewModel.circleMenu.observeAsState()
    val direction = viewModel.direction.observeAsState(initial = CircleMenuDirection.Up)
    val selectedCircleMenuItem = viewModel.selectedCircleMenuItem.observeAsState()

    // UI
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            EditCircleMenuToolbar(
                viewModel = viewModel,
                onBackPressed = {
                    scope.launch { controller.isAppearanceLightStatusBars = true }
                    onBackPressed()
                }
            )
        }
    ) { paddingValues ->

        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CircleMenu UI
                circleMenu.value?.let { notNullCircleMenu ->
                    Box(
                        modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp / 2),
                        contentAlignment = Alignment.Center
                    ) {
                        CircleMenuBox(
                            circleMenu = notNullCircleMenu,
                            menuSize = LocalConfiguration.current.screenWidthDp / 2f - 40f,
                            direction = direction.value
                        ) { circleMenuDirection ->
                            viewModel.setDirection(circleMenuDirection)
                        }
                    }

                }
                // CircleMenu image and action panel
                selectedCircleMenuItem.value?.let { circleMenuItem ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))
                        ImageAndAction(
                            width = LocalConfiguration.current.screenWidthDp.dp / 2 - 10.dp,
                            circleMenuItem = circleMenuItem,
                            onChangeAction = {
                                viewModel.updateCircleMenuItem((circleMenuItem.copy(action = it)))
                            },
                            onChangeImage = {
                                viewModel.updateCircleMenuItem(circleMenuItem.copy(image = it))
                            }
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                    }

                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                // CircleMenu UI
                circleMenu.value?.let { notNullCircleMenu ->
                    CircleMenuBox(
                        circleMenu = notNullCircleMenu,
                        direction = direction.value
                    ) { circleMenuDirection ->
                        viewModel.setDirection(circleMenuDirection)
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                // CircleMenu image and action panel
                selectedCircleMenuItem.value?.let { circleMenuItem ->
                    ImageAndAction(
                        circleMenuItem = circleMenuItem,
                        onChangeAction = {
                            viewModel.updateCircleMenuItem((circleMenuItem.copy(action = it)))
                        },
                        onChangeImage = {
                            viewModel.updateCircleMenuItem(circleMenuItem.copy(image = it))
                        }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun EditCircleMenuToolbar(
    viewModel: EditCircleMenuScreenVM,
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
        val window = (LocalContext.current as Activity).window
        val view = LocalView.current
        val controller = remember { WindowInsetsControllerCompat(window, view) }
        controller.isAppearanceLightStatusBars = false
        val circleMenu = viewModel.circleMenu.observeAsState()
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
        circleMenu.value?.let { menu ->
            if (menu.id == 0) {
                Text(
                    text = menu.title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 20.sp
                )
            } else {
                var title by remember {
                    mutableStateOf(TextFieldValue())
                }
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp
                    ),
                    value = title.copy(text = circleMenu.value?.title ?: ""),
                    onValueChange = { newTitle ->
                        viewModel.insertCircleMenu(menu.copy(title = newTitle.text))
                        title = newTitle
                    }
                )
            }
        }
    }
}

@Composable
private fun CircleMenuBox(
    circleMenu: CircleMenu,
    direction: CircleMenuDirection?,
    menuSize: Float = Constants.minScreenLength / 3 * 2,
    onSelectAction: (CircleMenuDirection) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp)
    ) {
        CircleMenuForEditUI(
            menuSize = menuSize,
            menuImages = circleMenu.menuImages,
            upImageClick = { onSelectAction(CircleMenuDirection.Up) },
            downImageClick = { onSelectAction(CircleMenuDirection.Down) },
            rightImageClick = { onSelectAction(CircleMenuDirection.Right) },
            leftImageClick = { onSelectAction(CircleMenuDirection.Left) },
            selectedDirection = direction
        )
    }
}