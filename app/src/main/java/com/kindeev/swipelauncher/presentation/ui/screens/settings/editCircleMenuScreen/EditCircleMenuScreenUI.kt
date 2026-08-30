package com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen.entities.ActionItemState
import com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen.entities.SelectedItemBoxData
import com.kindeev.swipelauncher.presentation.ui.elements.CircleMenuItems
import com.kindeev.swipelauncher.presentation.ui.elements.MaterialIcon
import com.kindeev.swipelauncher.presentation.ui.elements.settingsListItems.CircleMenuActionListItem
import com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen.entities.CircleMenuItemForEdit
import com.kindeev.swipelauncher.presentation.viewModels.settings.editCircleMenuScreen.EditCircleMenuScreenVM

@Composable
fun EditCircleMenuScreenUI(
    viewModel: EditCircleMenuScreenVM,
) {
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT && (Constants.minScreenLength / 6 * 5f * 1.5f).dp < LocalWindowInfo.current.containerDpSize.height) {
        viewModel.updateMenuSize(Constants.minScreenLength / 6 * 5f)
        PortraitUI(viewModel = viewModel)
    } else {
        viewModel.updateMenuSize((Constants.minScreenLength - 80) / 3 * 2f)
        LandscapeUI(viewModel = viewModel)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LandscapeUI(
    viewModel: EditCircleMenuScreenVM
) {
    val actionItemData by viewModel.actionItemData.collectAsState()
    val selectedBoxData by viewModel.selectedBoxData.collectAsState()
    val selectedItem by viewModel.selectedItemForEdit.collectAsState()
    val circleMenuToDraw by viewModel.circleMenuToDraw.collectAsStateWithLifecycle()

    // UI
    Scaffold(
        topBar = {
            EditCircleMenuToolbarUI(
                onBackPressed = viewModel::onBackPressed
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // CircleMenu and Title
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // CircleMenu
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(circleMenuToDraw.menuSize.dp)
                            .pointerInteropFilter(
                                onTouchEvent = viewModel.onSwipe()
                            )
                    ) {
                        val ghostItem by viewModel.ghostItem.collectAsState()
                        when (actionItemData.state) {

                            ActionItemState.Add -> AddCircleMenuItemUI(
                                circleMenuToDraw.menuSize,
                                actionItemData.size
                            )

                            ActionItemState.Delete -> DeleteCircleMenuItemUI(
                                circleMenuToDraw.menuSize,
                                actionItemData.size,
                                false
                            )

                            ActionItemState.DeleteActive -> DeleteCircleMenuItemUI(
                                circleMenuToDraw.menuSize,
                                actionItemData.size,
                                true
                            )
                        }
                        ghostItem?.let { item ->
                            GhostCircleMenuItemUI(item = item)
                        }
                        if (ghostItem == null) {
                            selectedBoxData?.let { SelectedItemBox(data = it) }
                        }
                        CircleMenuItems(
                            modifier = Modifier.size(circleMenuToDraw.menuSize.dp),
                            items = circleMenuToDraw.items,
                            itemSize = circleMenuToDraw.itemSize
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title
                CircleMenuTitle(viewModel)
            }
            selectedItem?.let { item ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    ImageAndActionEdit(
                        viewModel = viewModel,
                        circleMenuItemForEdit = item,
                        openActionDialog = viewModel::openActionDialog,
                        openImageDialog = viewModel::openImageDialog,
                        onChangeAction = viewModel::updateAction
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun PortraitUI(
    viewModel: EditCircleMenuScreenVM
) {
    val actionItemData by viewModel.actionItemData.collectAsState()
    val selectedBoxData by viewModel.selectedBoxData.collectAsState()
    val selectedItem by viewModel.selectedItemForEdit.collectAsState()
    val circleMenuToDraw by viewModel.circleMenuToDraw.collectAsStateWithLifecycle()

    // UI
    Scaffold(
        topBar = {
            EditCircleMenuToolbarUI(
                onBackPressed = viewModel::onBackPressed
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Spacer(modifier = Modifier.height(30.dp))

            // CircleMenu
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(circleMenuToDraw.menuSize.dp)
                            .pointerInteropFilter(
                                onTouchEvent = viewModel.onSwipe()
                            )
                    ) {
                        val ghostItem by viewModel.ghostItem.collectAsState()
                        when (actionItemData.state) {

                            ActionItemState.Add -> AddCircleMenuItemUI(
                                circleMenuToDraw.menuSize,
                                actionItemData.size
                            )

                            ActionItemState.Delete -> DeleteCircleMenuItemUI(
                                circleMenuToDraw.menuSize,
                                actionItemData.size,
                                false
                            )

                            ActionItemState.DeleteActive -> DeleteCircleMenuItemUI(
                                circleMenuToDraw.menuSize,
                                actionItemData.size,
                                true
                            )
                        }
                        ghostItem?.let { item ->
                            GhostCircleMenuItemUI(item = item)
                        }
                        if (ghostItem == null) {
                            selectedBoxData?.let { SelectedItemBox(data = it) }
                        }
                        CircleMenuItems(
                            modifier = Modifier.size(circleMenuToDraw.menuSize.dp),
                            items = circleMenuToDraw.items,
                            itemSize = circleMenuToDraw.itemSize
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

            // Title
                CircleMenuTitle(viewModel)

            // Item edit
                selectedItem?.let { item ->
                    Spacer(modifier = Modifier.height(40.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ImageAndActionEdit(
                            viewModel = viewModel,
                            circleMenuItemForEdit = item,
                            openActionDialog = viewModel::openActionDialog,
                            openImageDialog = viewModel::openImageDialog,
                            onChangeAction = viewModel::updateAction
                        )
                    }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCircleMenuToolbarUI(onBackPressed: () -> Unit) {
    TopAppBar(
        title = {},
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
        }
    )
}

@Composable
private fun CircleMenuTitle(
    viewModel: EditCircleMenuScreenVM
) {
    val title by viewModel.circleMenuTitle.collectAsState()
    val menuSize by viewModel.menuSize.collectAsStateWithLifecycle()
    val fontSize = 24.sp
    Box(
        modifier = Modifier
            .width(menuSize.dp)
            .padding(horizontal = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        if (title.text.isEmpty()) {
            Text(
                text = stringResource(R.string.title),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = fontSize
            )
        }
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                fontSize = fontSize,
                fontWeight = FontWeight.Black
            ),
            value = title,
            onValueChange = viewModel::changeTitle,
            singleLine = true
        )
    }
}

@Composable
private fun SelectedItemBox(
    data: SelectedItemBoxData
) {
    Box(
        modifier = Modifier
            .offset(
                x = data.offset.x.dp,
                y = data.offset.y.dp
            )
            .size(data.size.dp)
            .background(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(16.dp),
            )
    )
}

@Composable
private fun ImageAndActionEdit(
    viewModel: EditCircleMenuScreenVM,
    circleMenuItemForEdit: CircleMenuItemForEdit,
    openActionDialog: () -> Unit,
    openImageDialog: () -> Unit,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    val menuSize by viewModel.menuSize.collectAsStateWithLifecycle()
    Row(
        modifier = Modifier
            .width(menuSize.dp + 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Image

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.image),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Image(
                bitmap = circleMenuItemForEdit.image,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(onClick = openImageDialog)
                    .padding(10.dp)
                    .size(50.dp),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
        VerticalDivider(
            modifier = Modifier.height(80.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(10.dp))

        // Action

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.action),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            CircleMenuActionListItem(
                modifier = Modifier.fillMaxWidth(),
                actionItemData = circleMenuItemForEdit.action,
                changeAction = onChangeAction,
                openActionDialog = openActionDialog
            )
        }
    }
}
