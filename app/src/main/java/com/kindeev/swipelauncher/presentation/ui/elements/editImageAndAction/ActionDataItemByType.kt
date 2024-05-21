package com.kindeev.swipelauncher.presentation.ui.elements.editImageAndAction

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.ReadContactsPermission
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.Call
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.Dial
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenApp
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenCircleMenu
import com.kindeev.swipelauncher.domain.formatPhoneNumber
import com.kindeev.swipelauncher.domain.getApplicationInfo
import com.kindeev.swipelauncher.domain.getAs
import com.kindeev.swipelauncher.domain.getContactName
import com.kindeev.swipelauncher.presentation.ui.dialogs.EnterNumberDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.FlashlightActionData
import com.kindeev.swipelauncher.presentation.ui.dialogs.OpenAppActionData
import com.kindeev.swipelauncher.presentation.ui.dialogs.OpenCircleMenuActionData
import com.kindeev.swipelauncher.presentation.ui.dialogs.TelephoneActionData
import com.kindeev.swipelauncher.presentation.ui.elements.CircleMenuItems

@Composable
fun ActionDataByType(
    action: CircleMenuAction,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    when (action.type) {
        CircleMenuActionTypes.OpenCircleMenu -> {
            OpenCircleMenuDataItem(
                action = action,
                textColor = textColor,
                onChangeAction = onChangeAction
            )
        }

        CircleMenuActionTypes.OpenSettings -> {}
        CircleMenuActionTypes.OpenApp -> {
            OpenAppDataItem(
                action = action,
                textColor = textColor,
                onChangeAction = onChangeAction
            )
        }

        CircleMenuActionTypes.FlashLightOn -> {
            FlashlightOnDataItem(onChangeAction = onChangeAction, textColor = textColor)
        }

        CircleMenuActionTypes.FlashLightOff -> {
            FlashlightOffDataItem(onChangeAction = onChangeAction, textColor = textColor)
        }

        CircleMenuActionTypes.ChangeFlashLightCondition -> {
            ChangeFlashlightConditionDataItem(onChangeAction = onChangeAction, textColor = textColor)
        }

        CircleMenuActionTypes.Call -> {
            CallDataItem(action = action, onChangeAction = onChangeAction)
        }

        CircleMenuActionTypes.Dial -> {
            DialDataItem(action = action, onChangeAction = onChangeAction)
        }
    }
}

@Composable
private fun OpenCircleMenuDataItem(
    action: CircleMenuAction,
    textColor: Color,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    var showOpenCircleMenuDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showOpenCircleMenuDialog) {
        OpenCircleMenuActionData(
            onPick = onChangeAction,
            onDismissRequest = { showOpenCircleMenuDialog = false }
        )
    }

    val openCircleMenu = action.data.getAs(OpenCircleMenu::class.java)
    val circleMenu =
        LauncherData.allCircleMenus.value?.find { it.id == openCircleMenu.id }
    Column(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(7.dp))
            .clickable { showOpenCircleMenuDialog = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        circleMenu?.let {
            CircleMenuItems(
                menuSize = Constants.minScreenLength / 3f,
                items = circleMenu.items
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = circleMenu.title,
                color = textColor,
                fontSize = Constants.minScreenLength.sp / 20
            )
        }
    }
}

@Composable
private fun OpenAppDataItem(
    action: CircleMenuAction,
    textColor: Color,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    var showOpenAppDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showOpenAppDialog) {
        OpenAppActionData(
            onPick = onChangeAction,
            onDismissRequest = { showOpenAppDialog = false }
        )
    }
    val openApp = action.data.getAs(OpenApp::class.java)
    val applicationData = LocalContext.current.getApplicationInfo(openApp.packageName)
    Column(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(7.dp))
            .clickable { showOpenAppDialog = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(Constants.minScreenLength.dp / 3),
            bitmap = applicationData.icon,
            contentDescription = "App image"
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = applicationData.title,
            color = textColor,
            fontSize = Constants.minScreenLength.sp / 20
        )
    }
}

@Composable
private fun FlashlightOnDataItem(
    textColor: Color,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    var showFlashlightDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showFlashlightDialog) {
        FlashlightActionData(
            onPick = onChangeAction,
            onDismissRequest = { showFlashlightDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(7.dp))
            .clickable { showFlashlightDialog = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(Constants.minScreenLength.dp / 3),
            painter = painterResource(id = R.drawable.on_flashlight_image),
            contentDescription = "On flashlight image"
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = stringResource(id = R.string.on_flashlight_action),
            color = textColor,
            fontSize = Constants.minScreenLength.sp / 20
        )
    }
}

@Composable
private fun FlashlightOffDataItem(
    textColor: Color,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    var showFlashlightDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showFlashlightDialog) {
        FlashlightActionData(
            onPick = onChangeAction,
            onDismissRequest = { showFlashlightDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(7.dp))
            .clickable { showFlashlightDialog = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(Constants.minScreenLength.dp / 3),
            painter = painterResource(id = R.drawable.off_flashlight_image),
            contentDescription = "Off flashlight image"
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = stringResource(id = R.string.off_flashlight_action),
            color = textColor,
            fontSize = Constants.minScreenLength.sp / 20
        )
    }
}

@Composable
private fun ChangeFlashlightConditionDataItem(
    textColor: Color,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    var showFlashlightDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showFlashlightDialog) {
        FlashlightActionData(
            onPick = onChangeAction,
            onDismissRequest = { showFlashlightDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(7.dp))
            .clickable { showFlashlightDialog = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(Constants.minScreenLength.dp / 3),
            painter = painterResource(id = R.drawable.change_condition_flashlight_image),
            contentDescription = "Change flashlight condition image"
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = stringResource(id = R.string.change_condition_flashlight_action),
            color = textColor,
            fontSize = Constants.minScreenLength.sp / 20
        )
    }
}

@Composable
private fun CallDataItem(
    action: CircleMenuAction,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    val context = LocalContext.current
    var data by rememberSaveable {
        mutableStateOf(action.data.getAs(Call::class.java))
    }
    var contactName by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var hasReadContactsPermission by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }
    if (action.data.getAs(Call::class.java) != data) {
        data = action.data.getAs(Call::class.java)
        if (hasReadContactsPermission == true) {
            contactName = context.getContactName(data.phoneNumber)
        }
    }
    if (hasReadContactsPermission == null) {
        ReadContactsPermission {
            if (it) {
                contactName = context.getContactName(data.phoneNumber)
            }
            hasReadContactsPermission = it
        }
    }
    var showTelephoneDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showEnterNumberDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showTelephoneDialog) {
        TelephoneActionData(
            onPick = onChangeAction,
            onDismissRequest = { showTelephoneDialog = false }
        )
    }
    if (showEnterNumberDialog) {
        EnterNumberDialog(
            defNumber = data.phoneNumber,
            onEnter = { onChangeAction(action.copy(data = Call(it))) },
            onDismissRequest = { showEnterNumberDialog = false }
        )
    }

    Row(
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(Constants.minScreenLength.dp / 8)
                .clip(CircleShape)
                .clickable { showTelephoneDialog = true },
            painter = painterResource(id = R.drawable.call_telephone_image),
            contentDescription = "Call image"
        )
        Spacer(modifier = Modifier.width(5.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.secondary)
                .clickable { showEnterNumberDialog = true }
                .padding(horizontal = 15.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (contactName == null) {
                Text(
                    text = data.phoneNumber.formatPhoneNumber(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = Constants.minScreenLength.sp / 20
                )
            } else {
                Image(
                    modifier = Modifier
                        .size(Constants.minScreenLength.dp / 10)
                        .clip(RoundedCornerShape(16.dp)),
                    painter = painterResource(id = R.drawable.contact_image),
                    contentDescription = "Contact image"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = contactName
                        ?: stringResource(id = R.string.error),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = Constants.minScreenLength.sp / 20,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DialDataItem(
    action: CircleMenuAction,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    val context = LocalContext.current
    var data by rememberSaveable {
        mutableStateOf(action.data.getAs(Dial::class.java))
    }
    var contactName by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var hasReadContactsPermission by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }
    if (action.data.getAs(Dial::class.java) != data) {
        data = action.data.getAs(Dial::class.java)
        if (hasReadContactsPermission == true) {
            contactName = context.getContactName(data.phoneNumber)
        }
    }
    if (hasReadContactsPermission == null) {
        ReadContactsPermission {
            if (it) {
                contactName = context.getContactName(data.phoneNumber)
            }
            hasReadContactsPermission = it
        }
    }
    var showTelephoneDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showEnterNumberDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showTelephoneDialog) {
        TelephoneActionData(
            onPick = onChangeAction,
            onDismissRequest = { showTelephoneDialog = false }
        )
    }
    if (showEnterNumberDialog) {
        EnterNumberDialog(
            defNumber = data.phoneNumber,
            onEnter = { onChangeAction(action.copy(data = Call(it))) },
            onDismissRequest = { showEnterNumberDialog = false }
        )
    }

    Row(
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(Constants.minScreenLength.dp / 8)
                .clip(CircleShape)
                .clickable { showTelephoneDialog = true }
                .padding(2.dp),
            painter = painterResource(id = R.drawable.dial_telephone_image),
            contentDescription = "Dial image"
        )
        Spacer(modifier = Modifier.width(5.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.secondary)
                .clickable { showEnterNumberDialog = true }
                .padding(horizontal = 15.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (contactName == null) {
                Text(
                    text = data.phoneNumber.formatPhoneNumber(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = Constants.minScreenLength.sp / 20
                )
            } else {
                Image(
                    modifier = Modifier
                        .size(Constants.minScreenLength.dp / 10)
                        .clip(RoundedCornerShape(16.dp)),
                    painter = painterResource(id = R.drawable.contact_image),
                    contentDescription = "Contact image"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = contactName
                        ?: stringResource(id = R.string.error),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = Constants.minScreenLength.sp / 20,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}