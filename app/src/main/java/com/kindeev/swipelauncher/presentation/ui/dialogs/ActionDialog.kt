package com.kindeev.swipelauncher.presentation.ui.dialogs

import android.provider.ContactsContract
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.utils.CallPermission
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.utils.ReadContactsPermission
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.CallAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.DialAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenSettingsAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenUrlAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.actionTypes.AllActionTypes
import com.kindeev.swipelauncher.domain.entities.actionTypes.actionCategory.ActionCategories
import com.kindeev.swipelauncher.domain.utils.getFlashlightAction
import com.kindeev.swipelauncher.domain.viewModels.dialogs.actionDialog.ActionDialogVM
import com.kindeev.swipelauncher.domain.viewModels.dialogs.actionDialog.ActionDialogVMFactory
import com.kindeev.swipelauncher.presentation.entities.PhoneNumberVisualTransformation
import com.kindeev.swipelauncher.presentation.ui.elements.AppItem
import com.kindeev.swipelauncher.presentation.ui.elements.DialogSearchElement
import com.kindeev.swipelauncher.presentation.ui.elements.MiniCircleMenuItem

@Composable
fun ActionDialog(
    onDismissRequest: () -> Unit,
    onPick: (CircleMenuAction) -> Unit
) {
    val context = LocalContext.current
    val viewModel: ActionDialogVM = viewModel(
        factory = ActionDialogVMFactory(context)
    )
    var actionCategory by rememberSaveable {
        mutableStateOf<ActionCategories?>(null)
    }
    AllActionTypes(
        onPick = { actionCategory = it },
        onDismissRequest = onDismissRequest
    )
    when (actionCategory) {
        ActionCategories.OpenCircleMenu -> {
            OpenCircleMenuActionData(
                getItemImage = viewModel::getItemImage,
                onPick = {
                    onPick(it)
                    onDismissRequest()
                },
                onDismissRequest = { actionCategory = null }
            )
        }

        ActionCategories.OpenApp -> {
            OpenAppActionData(
                viewModel = viewModel,
                onPick = {
                    onPick(it)
                    onDismissRequest()
                },
                onDismissRequest = { actionCategory = null }
            )
        }

        ActionCategories.Flashlight -> {
            FlashlightActionData(
                onPick = {
                    onPick(it)
                    onDismissRequest()
                },
                onDismissRequest = { actionCategory = null }
            )
        }

        ActionCategories.Telephone -> {
            TelephoneActionData(
                onPick = {
                    onPick(it)
                    onDismissRequest()
                },
                onDismissRequest = { actionCategory = null }
            )
        }

        ActionCategories.OpenSettings -> {
            onPick(OpenSettingsAction)
            onDismissRequest()
        }

        ActionCategories.OpenUrl -> {
            OpenUrlActionData(
                onPick = {
                    onPick(it)
                    onDismissRequest()
                },
                onDismissRequest = { actionCategory = null }
            )
        }

        null -> {}
    }
}

@Composable
private fun AllActionTypes(
    onPick: (ActionCategories) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            var searchText by rememberSaveable {
                mutableStateOf("")
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(items = Constants.actionCategories.filter {
                    it.name.lowercase().contains(searchText.lowercase())
                }) { actionType ->
                    ActionTypeElement(
                        name = actionType.name,
                        imageResId = actionType.imageResId,
                        onClick = { onPick(actionType.type) }
                    )
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = { searchText = it })
        }
    }
}

@Composable
private fun ActionTypeElement(
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
fun OpenCircleMenuActionData(
    getItemImage: (CircleMenuImage) -> ImageBitmap?,
    onPick: (CircleMenuAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            var searchText by rememberSaveable {
                mutableStateOf("")
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2)
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(
                    items = LauncherData.allCircleMenus.value?.filter {
                        it.title.lowercase().contains(searchText.lowercase())
                    } ?: emptyList()
                ) { circleMenu ->
                    MiniCircleMenuItem(
                        getItemImage = getItemImage,
                        size = (Integer.min(
                            LocalConfiguration.current.screenWidthDp,
                            LocalConfiguration.current.screenHeightDp
                        ) - 20f) / 3,
                        circleMenu = circleMenu
                    ) {
                        onPick(OpenCircleMenuAction(id = circleMenu.id))
                        onDismissRequest()
                    }
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = { searchText = it })
        }
    }
}

@Composable
fun OpenAppActionData(
    viewModel: ActionDialogVM,
    onPick: (CircleMenuAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            val allApplicationInfo by LauncherData.allApplicationInfo.observeAsState(emptyList())
            var searchText by rememberSaveable {
                mutableStateOf("")
            }
            LazyColumn {
                item { Spacer(modifier = Modifier.height(40.dp)) }
                items(
                    items = viewModel.getAllApplicationsData(allApplicationInfo).filter {
                        it.title.lowercase().contains(searchText.lowercase())
                    },
                    key = { it.packageName }
                ) { applicationData ->
                    viewModel.getItemImage(applicationData.image)?.let { image ->
                        AppItem(
                            title = applicationData.title,
                            image = image
                        ) {
                            onPick(OpenAppAction(packageName = applicationData.packageName))
                            onDismissRequest()
                        }
                    }
                }

            }
            DialogSearchElement(searchText = searchText, onTextChange = { searchText = it })
        }
    }
}

@Composable
fun FlashlightActionData(
    onPick: (CircleMenuAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            var searchText by rememberSaveable {
                mutableStateOf("")
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(items = Constants.flashlightActionCategoryItems.filter {
                    it.name.lowercase().contains(searchText.lowercase())
                }) { flashlightActionType ->
                    ActionTypeElement(
                        name = flashlightActionType.name,
                        imageResId = flashlightActionType.imageResId,
                        onClick = {
                            onPick(flashlightActionType.type.getFlashlightAction())
                            onDismissRequest()
                        }
                    )
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = { searchText = it })
        }
    }
}

@Composable
fun TelephoneActionData(
    onPick: (CircleMenuAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height((screenConfiguration.screenHeightDp / 3 * 2).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            var actionType by rememberSaveable {
                mutableStateOf<AllActionTypes?>(null)
            }
            when (actionType) {
                AllActionTypes.Call -> {
                    var hasCallPermission by rememberSaveable {
                        mutableStateOf<Boolean?>(null)
                    }
                    if (hasCallPermission == null) {
                        CallPermission { hasCallPermission = it }
                    } else {
                        if (hasCallPermission == true) {
                            EnterNumberDialog(
                                onEnter = {
                                    onPick(CallAction(it))
                                    onDismissRequest()
                                },
                                onDismissRequest = {
                                    actionType = null
                                }
                            )
                        } else {
                            actionType = null
                        }
                    }
                }

                AllActionTypes.Dial -> {
                    EnterNumberDialog(
                        onEnter = {
                            onPick(DialAction(it))
                            onDismissRequest()
                        },
                        onDismissRequest = {
                            actionType = null
                        }
                    )
                }

                else -> {}
            }

            var searchText by rememberSaveable {
                mutableStateOf("")
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }
                items(items = Constants.telephoneActionCategoryItems.filter {
                    it.name.lowercase().contains(searchText.lowercase())
                }) { telephoneActionType ->
                    ActionTypeElement(
                        name = telephoneActionType.name,
                        imageResId = telephoneActionType.imageResId,
                        onClick = {
                            actionType = telephoneActionType.type
                        }
                    )
                }
            }
            DialogSearchElement(searchText = searchText, onTextChange = { searchText = it })
        }
    }
}

@Composable
fun EnterNumberDialog(
    defNumber: String = "",
    onEnter: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    val context = LocalContext.current

    var phoneNumber by rememberSaveable {
        mutableStateOf(defNumber)
    }
    var hasReadContactsPermission by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }
    if (hasReadContactsPermission == null) {
        ReadContactsPermission {
            hasReadContactsPermission = it
        }
    }
    val pickContactLauncher = rememberLauncherForActivityResult(
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
                                phoneNumber = contactNumber.replace("+7", "8")
                            }
                        }
                    }
                }
            }
        }
    )

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .height(screenConfiguration.screenWidthDp.dp / 2)
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
                        phoneNumber = value.filter { it.isDigit() }
                    },
                    singleLine = true,
                    visualTransformation = if (phoneNumber.length <= 11) PhoneNumberVisualTransformation else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Phone
                    ),
                    textStyle = TextStyle.Default.copy(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = screenConfiguration.screenWidthDp.sp / 15
                    )
                )
                if (hasReadContactsPermission == true) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                pickContactLauncher.launch(null)
                            },
                        painter = painterResource(id = R.drawable.contact_image),
                        contentDescription = "Contact image"
                    )
                }
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
                        .width((screenConfiguration.screenWidthDp / 3).dp)
                        .height((screenConfiguration.screenWidthDp / 9).dp)
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
                        .width((screenConfiguration.screenWidthDp / 3).dp)
                        .height((screenConfiguration.screenWidthDp / 9).dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            if (phoneNumber.isNotEmpty()) {
                                onEnter(phoneNumber)
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
fun OpenUrlActionData(
    defUrl: String = "",
    onPick: (CircleMenuAction) -> Unit,
    onDismissRequest: () -> Unit
) {
    val screenConfiguration = LocalConfiguration.current
    var url by rememberSaveable {
        mutableStateOf(defUrl)
    }
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .heightIn(max = screenConfiguration.screenWidthDp.dp)
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
                    onValueChange = { url = it },
                    textStyle = TextStyle.Default.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = screenConfiguration.screenWidthDp.sp / 20,
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
                        .width((screenConfiguration.screenWidthDp / 3).dp)
                        .height((screenConfiguration.screenWidthDp / 9).dp)
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
                        .width((screenConfiguration.screenWidthDp / 3).dp)
                        .height((screenConfiguration.screenWidthDp / 9).dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            if (url.isNotEmpty()) {
                                onPick(OpenUrlAction(url))
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

