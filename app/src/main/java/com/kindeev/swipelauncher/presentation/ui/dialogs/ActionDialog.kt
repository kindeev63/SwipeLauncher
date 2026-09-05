package com.kindeev.swipelauncher.presentation.ui.dialogs

import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.utils.CallPermission
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.utils.ReadContactsPermission
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenAppAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenUrlAction
import com.kindeev.swipelauncher.domain.entities.actionTypes.AllActionTypes
import com.kindeev.swipelauncher.domain.entities.actionTypes.FlashlightActionType
import com.kindeev.swipelauncher.domain.entities.actionTypes.TelephoneActionType
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategories
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategory
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.FlashLightOffAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.FlashLightOnAction
import com.kindeev.swipelauncher.presentation.entities.CircleMenuToDraw
import com.kindeev.swipelauncher.presentation.entities.PhoneNumberVisualTransformation
import com.kindeev.swipelauncher.presentation.ui.elements.AppItem
import com.kindeev.swipelauncher.presentation.ui.elements.DialogSearchElement
import com.kindeev.swipelauncher.presentation.ui.elements.MiniCircleMenuItem
import com.kindeev.swipelauncher.presentation.viewModels.settings.actionDialog.ActionDialogVM
import com.kindeev.swipelauncher.presentation.viewModels.settings.actionDialog.entities.ActionDialogState
import kotlinx.coroutines.launch

@Composable
fun ActionDialog(
    viewModel: ActionDialogVM,
    onDismissRequest: () -> Unit,
    onPick: (CircleMenuAction) -> Unit
) {
    val pickContact = pickContactLauncher(onPick = viewModel::onPickContact)
    LaunchedEffect(Unit) {
        launch {
            viewModel.pickAction.collect { action ->
                onPick(action)
                onDismissRequest()
            }
        }
        launch {
            viewModel.pickContact.collect {
                pickContact.launch(null)
            }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val currentState = state) {
        is ActionDialogState.PickCategory -> {
            PickCategory(
                searchText = currentState.searchText,
                categories = currentState.actionCategories,
                onSearch = viewModel::search,
                onPick = viewModel::pickCategory,
                onDismissRequest = onDismissRequest
            )
        }

        is ActionDialogState.OpenAppCategory -> {
            OpenAppCategory(
                searchText = currentState.searchText,
                applications = currentState.applications,
                onSearch = viewModel::search,
                onPick = viewModel::pickAction,
                onDismissRequest = viewModel::openPickCategory
            )
        }

        is ActionDialogState.OpenCircleMenuCategory -> {
            OpenCircleMenuCategory(
                searchText = currentState.searchText,
                circleMenus = currentState.circleMenus,
                onSearch = viewModel::search,
                onPick = viewModel::pickAction,
                onDismissRequest = viewModel::openPickCategory
            )
        }

        is ActionDialogState.FlashlightCategory -> {
            FlashlightCategory(
                searchText = currentState.searchText,
                actionTypes = currentState.flashlightActionTypes,
                onSearch = viewModel::search,
                onPick = { actionType ->
                    when (actionType) {
                        AllActionTypes.FlashLightOn -> viewModel.pickAction(FlashLightOnAction)
                        AllActionTypes.FlashLightOff -> viewModel.pickAction(FlashLightOffAction)
                        AllActionTypes.ChangeFlashLightCondition -> viewModel.pickAction(FlashLightOffAction)
                        else -> throw IllegalStateException("Illegal flashlight action type")
                    }
                },
                onDismissRequest = viewModel::openPickCategory
            )
        }

        is ActionDialogState.TelephoneCategory -> {
            if (currentState.requestCallPermission) {
                val context = LocalContext.current
                CallPermission(
                    result = { result ->
                        if (!result) {
                            Toast.makeText(context, R.string.call_denied, Toast.LENGTH_LONG).show()
                        }
                        viewModel.callPermissionResult(result)
                    }
                )
            }
            TelephoneActionData(
                searchText = currentState.searchText,
                actionTypes = currentState.telephoneActionTypes,
                onSearch = viewModel::search,
                onPick = { actionType ->
                    when (actionType) {
                        AllActionTypes.Call -> viewModel.openCallActionWithPermission()
                        AllActionTypes.Dial -> viewModel.openEnterNumberDialog(actionType)
                        else -> throw IllegalStateException("Illegal telephone action type")
                    }
                },
                onDismissRequest = viewModel::openPickCategory
            )
        }

        is ActionDialogState.OpenUrlCategory -> {
            OpenUrlCategory(
                url = currentState.url,
                onChangeUrl = viewModel::changeUrl,
                onPick = viewModel::pickAction,
                onDismissRequest = viewModel::openPickCategory
            )
        }

        is ActionDialogState.EnterNumberDialog -> {
            if (currentState.requestReadContactsPermission) {
                val context = LocalContext.current
                ReadContactsPermission(
                    result = { result ->
                        if (!result) {
                            Toast.makeText(context, R.string.read_contacts_denied, Toast.LENGTH_LONG).show()
                        }
                        viewModel.readContactsPermissionResult(result)
                    }
                )
            }
            EnterNumberDialog(
                phoneNumber = currentState.phoneNumber,
                onChangeNumber = viewModel::onChangeNumber,
                pickContact = viewModel::pickContact,
                onEnter = viewModel::pickTelephoneAction,
                onDismissRequest = viewModel::openTelephoneActions
            )
        }
    }
}

@Composable
private fun PickCategory(
    searchText: TextFieldValue,
    categories: List<ActionCategory>,
    onSearch: (TextFieldValue) -> Unit,
    onPick: (ActionCategories) -> Unit,
    onDismissRequest: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(windowInfo.containerDpSize.width - 20.dp)
                .height(windowInfo.containerDpSize.height / 3 * 2)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(items = categories) { actionCategory ->
                    ActionCategoryElement(
                        name = actionCategory.name,
                        imageResId = actionCategory.imageResId,
                        onClick = { onPick(actionCategory.type) }
                    )
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = onSearch)
        }
    }
}

@Composable
private fun ActionCategoryElement(
    name: String,
    imageResId: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(id = imageResId),
                contentDescription = "Action type image"
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


@Composable
fun OpenCircleMenuCategory(
    searchText: TextFieldValue,
    circleMenus: List<CircleMenuToDraw>,
    onSearch: (TextFieldValue) -> Unit,
    onPick: (CircleMenuAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(windowInfo.containerDpSize.width - 20.dp)
                .height(windowInfo.containerDpSize.height / 3 * 2)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive((Constants.minScreenLength - 20f).dp / 3)
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(
                    items = circleMenus
                ) { circleMenu ->
                    MiniCircleMenuItem(
                        size = (Constants.minScreenLength - 20f) / 3,
                        circleMenu = circleMenu
                    ) {
                        onPick(OpenCircleMenuAction(id = circleMenu.id))
                        onDismissRequest()
                    }
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = onSearch)
        }
    }
}

@Composable
fun OpenAppCategory(
    searchText: TextFieldValue,
    applications: List<ApplicationInfo>,
    onSearch: (TextFieldValue) -> Unit,
    onPick: (CircleMenuAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(windowInfo.containerDpSize.width - 20.dp)
                .height(windowInfo.containerDpSize.height / 3 * 2)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            LazyColumn {
                item { Spacer(modifier = Modifier.height(40.dp)) }
                items(
                    items = applications,
                    key = { it.packageName }
                ) { applicationInfo ->
                    AppItem(
                        packageName = applicationInfo.packageName,
                        title = applicationInfo.title,
                    ) {
                        onPick(OpenAppAction(packageName = applicationInfo.packageName))
                        onDismissRequest()
                    }
                }

            }
            DialogSearchElement(searchText = searchText, onTextChange = onSearch)
        }
    }
}

@Composable
fun FlashlightCategory(
    searchText: TextFieldValue,
    actionTypes: List<FlashlightActionType>,
    onSearch: (TextFieldValue) -> Unit,
    onPick: (AllActionTypes) -> Unit,
    onDismissRequest: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(windowInfo.containerDpSize.width - 20.dp)
                .height(windowInfo.containerDpSize.height / 3 * 2)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(items = actionTypes) { flashlightActionType ->
                    ActionCategoryElement(
                        name = flashlightActionType.name,
                        imageResId = flashlightActionType.imageResId,
                        onClick = {
                            onPick(flashlightActionType.type)
                            onDismissRequest()
                        }
                    )
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = onSearch)
        }
    }
}

@Composable
fun TelephoneActionData(
    searchText: TextFieldValue,
    actionTypes: List<TelephoneActionType>,
    onSearch: (TextFieldValue) -> Unit,
    onPick: (AllActionTypes) -> Unit,
    onDismissRequest: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(windowInfo.containerDpSize.width - 20.dp)
                .height(windowInfo.containerDpSize.height / 3 * 2)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(items = actionTypes) { telephoneActionType ->
                    ActionCategoryElement(
                        name = telephoneActionType.name,
                        imageResId = telephoneActionType.imageResId,
                        onClick = {
                            onPick(telephoneActionType.type)
                        }
                    )
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = onSearch)
        }
    }
}

@Composable
fun pickContactLauncher(onPick: (String) -> Unit): ManagedActivityResultLauncher<Void?, Uri?> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact(),
        onResult = { uri ->
            if (uri != null) {
                val cursor = context.contentResolver.query(
                    uri,
                    null,
                    null,
                    null,
                    null
                )
                cursor?.use {
                    if (it.moveToFirst()) {
                        val contactIdIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                        val contactId = it.getString(contactIdIndex)
                        val cursor2 = context.contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                            null,
                            null
                        )
                        cursor2?.use {
                            if (cursor2.moveToNext()) {
                                val contactNumberIndex =
                                    cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                val contactNumber = cursor2.getString(contactNumberIndex)
                                onPick(contactNumber.replace("+7", "8"))
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun EnterNumberDialog(
    phoneNumber: String,
    onChangeNumber: (String) -> Unit,
    pickContact: () -> Unit,
    onEnter: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .width(windowInfo.containerDpSize.width - 20.dp)
                .height(windowInfo.containerDpSize.width / 2)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.15f))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                BasicTextField(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 25.dp, vertical = 10.dp),
                    value = phoneNumber,
                    onValueChange = { value ->
                        onChangeNumber(value.filter { it.isDigit() })
                    },
                    singleLine = true,
                    visualTransformation = if (phoneNumber.length <= 11) PhoneNumberVisualTransformation else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Phone
                    ),
                    textStyle = TextStyle.Default.copy(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = pickContact),
                    painter = painterResource(id = R.drawable.contact_image),
                    contentDescription = "Contact image"
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround
            )
            {
                Box(
                    modifier = Modifier
                        .width(windowInfo.containerDpSize.width / 3)
                        .height(windowInfo.containerDpSize.width / 9)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onDismissRequest() }
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .clickable { onDismissRequest() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .width(windowInfo.containerDpSize.width / 3)
                        .height(windowInfo.containerDpSize.width / 9)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            if (phoneNumber.isNotEmpty()) {
                                onEnter()
                                onDismissRequest()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.save),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OpenUrlCategory(
    url: TextFieldValue,
    onChangeUrl: (TextFieldValue) -> Unit,
    onPick: (CircleMenuAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    val windowInfo = LocalWindowInfo.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .width(windowInfo.containerDpSize.width - 20.dp)
                .heightIn(max = windowInfo.containerDpSize.width)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 25.dp, vertical = 10.dp),
                    value = url,
                    onValueChange = onChangeUrl,
                    textStyle = TextStyle.Default.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceAround
            )
            {
                Box(
                    modifier = Modifier
                        .width(windowInfo.containerDpSize.width / 3)
                        .height(windowInfo.containerDpSize.width / 9)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { onDismissRequest() }
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .clickable { onDismissRequest() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .width(windowInfo.containerDpSize.width / 3)
                        .height(windowInfo.containerDpSize.width / 9)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            if (url.text.isNotEmpty()) {
                                onPick(OpenUrlAction(url.text))
                                onDismissRequest()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.save),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}