package com.kindeev.swipelauncher.presentation.ui.elements

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.ReadContactsPermission
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.Call
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.Dial
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenApp
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenCircleMenu
import com.kindeev.swipelauncher.domain.formatPhoneNumber
import com.kindeev.swipelauncher.domain.getActionType
import com.kindeev.swipelauncher.domain.getApplicationData
import com.kindeev.swipelauncher.domain.getAs
import com.kindeev.swipelauncher.domain.getContactName
import com.kindeev.swipelauncher.presentation.ui.dialogs.ActionDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.EnterNumberDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.FlashlightActionData
import com.kindeev.swipelauncher.presentation.ui.dialogs.OpenAppActionData
import com.kindeev.swipelauncher.presentation.ui.dialogs.OpenCircleMenuActionData
import com.kindeev.swipelauncher.presentation.ui.dialogs.TelephoneActionData

@Composable
fun ActionDataItem(
    action: CircleMenuAction,
    onChange: (CircleMenuAction) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current
    val actionType = action.type.getActionType()
    ActionDataBox(
        painter = painterResource(id = actionType?.imageResId ?: R.drawable.ic_error),
        text = actionType?.name ?: stringResource(id = R.string.error),
        onChangeAction = onChange
    ) {
        when (action.type) {
            CircleMenuActionTypes.OpenCircleMenu -> {
                var showOpenCircleMenuDialog by rememberSaveable {
                    mutableStateOf(false)
                }
                if (showOpenCircleMenuDialog) {
                    OpenCircleMenuActionData(
                        onPick = onChange,
                        onDismissRequest = { showOpenCircleMenuDialog = false }
                    )
                }
                val openCircleMenu = action.data.getAs(OpenCircleMenu::class.java)
                val circleMenu =
                    LauncherData.allCircleMenus.value?.find { it.id == openCircleMenu.id }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    circleMenu?.let {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .fillMaxHeight(0.6f)
                                .clip(RoundedCornerShape(7.dp))
                                .background(Color(0xFFBBDEFB))
                                .clickable { showOpenCircleMenuDialog = true }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircleMenuImagesUI(
                                menuSize = screenWidth / 5f,
                                menuImages = circleMenu.menuImages
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = circleMenu.title,
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            CircleMenuActionTypes.OpenSettings -> {}

            CircleMenuActionTypes.OpenApp -> {
                var showOpenAppDialog by rememberSaveable {
                    mutableStateOf(false)
                }
                if (showOpenAppDialog) {
                    OpenAppActionData(
                        onPick = onChange,
                        onDismissRequest = { showOpenAppDialog = false }
                    )
                }
                val openApp = action.data.getAs(OpenApp::class.java)
                val applicationData = context.getApplicationData(openApp.packageName)
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.6f)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0xFFBBDEFB))
                            .clickable { showOpenAppDialog = true }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.size(screenWidth.dp / 5f),
                            bitmap = applicationData.icon,
                            contentDescription = "App image"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = applicationData.name,
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            CircleMenuActionTypes.FlashLightOn -> {
                var showFlashlightDialog by rememberSaveable {
                    mutableStateOf(false)
                }
                if (showFlashlightDialog) {
                    FlashlightActionData(
                        onPick = onChange,
                        onDismissRequest = { showFlashlightDialog = false }
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.6f)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0xFFBBDEFB))
                            .clickable { showFlashlightDialog = true }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Image(
                            modifier = Modifier.size(screenWidth.dp / 5f),
                            painter = painterResource(id = R.drawable.on_flashlight_image),
                            contentDescription = "On flashlight image"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(id = R.string.on_flashlight_action),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            CircleMenuActionTypes.FlashLightOff -> {
                var showFlashlightDialog by rememberSaveable {
                    mutableStateOf(false)
                }
                if (showFlashlightDialog) {
                    FlashlightActionData(
                        onPick = onChange,
                        onDismissRequest = { showFlashlightDialog = false }
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.6f)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0xFFBBDEFB))
                            .clickable { showFlashlightDialog = true }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Image(
                            modifier = Modifier.size(screenWidth.dp / 5f),
                            painter = painterResource(id = R.drawable.off_flashlight_image),
                            contentDescription = "Off flashlight image"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(id = R.string.off_flashlight_action),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            CircleMenuActionTypes.ChangeFlashLightCondition -> {
                var showFlashlightDialog by rememberSaveable {
                    mutableStateOf(false)
                }
                if (showFlashlightDialog) {
                    FlashlightActionData(
                        onPick = onChange,
                        onDismissRequest = { showFlashlightDialog = false }
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.6f)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0xFFBBDEFB))
                            .clickable { showFlashlightDialog = true }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Image(
                            modifier = Modifier.size(screenWidth.dp / 5f),
                            painter = painterResource(id = R.drawable.change_condition_flashlight_image),
                            contentDescription = "Change flashlight condition image"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(id = R.string.change_condition_flashlight_action),
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            CircleMenuActionTypes.Call -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.6f)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0xFFBBDEFB))
                            .padding(10.dp)
                    )
                    {
                        var call by rememberSaveable {
                            mutableStateOf(action.data.getAs(Call::class.java))
                        }
                        var contactName by rememberSaveable {
                            mutableStateOf<String?>(null)
                        }
                        var hasReadContactsPermission by rememberSaveable {
                            mutableStateOf<Boolean?>(null)
                        }
                        if (action.data.getAs(Call::class.java) != call) {
                            call = action.data.getAs(Call::class.java)
                            if (hasReadContactsPermission == true) {
                                contactName = context.getContactName(call.phoneNumber)
                            }
                        }
                        if (hasReadContactsPermission == null) {
                            ReadContactsPermission {
                                if (it) {
                                    contactName = context.getContactName(call.phoneNumber)
                                }
                                hasReadContactsPermission = it
                            }
                        }
                        var showTelephoneDialog by rememberSaveable {
                            mutableStateOf(false)
                        }
                        var showCallDialog by rememberSaveable {
                            mutableStateOf(false)
                        }
                        if (showTelephoneDialog) {
                            TelephoneActionData(
                                onPick = onChange,
                                onDismissRequest = { showTelephoneDialog = false }
                            )
                        }
                        if (showCallDialog) {
                            EnterNumberDialog(
                                onEnter = { onChange(action.copy(data = Call(it))) },
                                onDismissRequest = { showCallDialog = false }
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.5f),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(7.dp))
                                        .clickable { showTelephoneDialog = true }
                                        .padding(2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        modifier = Modifier.size(screenWidth.dp / 15),
                                        painter = painterResource(id = R.drawable.call_telephone_image),
                                        contentDescription = "Call image"
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = stringResource(id = R.string.call_telephone_action),
                                        color = Color.Black,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (contactName != null) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(Color(0xFF2196F3))
                                            .clickable { showCallDialog = true }
                                            .padding(5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            modifier = Modifier
                                                .size(45.dp)
                                                .clip(RoundedCornerShape(16.dp)),
                                            painter = painterResource(id = R.drawable.contact_image),
                                            contentDescription = "Contact image"
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = contactName
                                                ?: stringResource(id = R.string.error),
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    Text(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color(0xFF2196F3))
                                            .clickable { showCallDialog = true }
                                            .padding(horizontal = 15.dp, vertical = 5.dp),
                                        text = call.phoneNumber.formatPhoneNumber(),
                                        color = Color.White,
                                        fontSize = screenWidth.sp / 20
                                    )
                                }
                            }
                        }
                    }
                }
            }

            CircleMenuActionTypes.Dial -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight(0.6f)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0xFFBBDEFB))
                            .padding(10.dp)
                    )
                    {
                        var dial by rememberSaveable {
                            mutableStateOf(action.data.getAs(Dial::class.java))
                        }
                        var contactName by rememberSaveable {
                            mutableStateOf<String?>(null)
                        }
                        var hasReadContactsPermission by rememberSaveable {
                            mutableStateOf<Boolean?>(null)
                        }
                        if (action.data.getAs(Dial::class.java) != dial) {
                            dial = action.data.getAs(Dial::class.java)
                            if (hasReadContactsPermission == true) {
                                contactName = context.getContactName(dial.phoneNumber)
                            }
                        }
                        if (hasReadContactsPermission == null) {
                            ReadContactsPermission {
                                if (it) {
                                    contactName = context.getContactName(dial.phoneNumber)
                                }
                                hasReadContactsPermission = it
                            }
                        }
                        var showTelephoneDialog by rememberSaveable {
                            mutableStateOf(false)
                        }
                        var showDialDialog by rememberSaveable {
                            mutableStateOf(false)
                        }
                        if (showTelephoneDialog) {
                            TelephoneActionData(
                                onPick = onChange,
                                onDismissRequest = { showTelephoneDialog = false }
                            )
                        }
                        if (showDialDialog) {
                            EnterNumberDialog(
                                onEnter = { onChange(action.copy(data = Call(it))) },
                                onDismissRequest = { showDialDialog = false }
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.5f),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(7.dp))
                                        .clickable { showTelephoneDialog = true }
                                        .padding(2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        modifier = Modifier.size(screenWidth.dp / 15),
                                        painter = painterResource(id = R.drawable.dial_telephone_image),
                                        contentDescription = "Dial image"
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = stringResource(id = R.string.dial_telephone_action),
                                        color = Color.Black,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (contactName != null) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(7.dp))
                                            .background(Color(0xFF2196F3))
                                            .clickable { showDialDialog = true }
                                            .padding(5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            modifier = Modifier
                                                .size(45.dp)
                                                .clip(RoundedCornerShape(16.dp)),
                                            painter = painterResource(id = R.drawable.contact_image),
                                            contentDescription = "Contact image"
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = contactName
                                                ?: stringResource(id = R.string.error),
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    Text(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color(0xFF2196F3))
                                            .clickable { showDialDialog = true }
                                            .padding(horizontal = 15.dp, vertical = 5.dp),
                                        text = dial.phoneNumber.formatPhoneNumber(),
                                        color = Color.White,
                                        fontSize = screenWidth.sp / 20
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionDataBox(
    painter: Painter,
    text: String,
    onChangeAction: (CircleMenuAction) -> Unit,
    content: @Composable () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    var showActionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showActionDialog) {
        ActionDialog(
            onDismissRequest = { showActionDialog = false },
            onPick = onChangeAction
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenWidth.dp / 5 * 3)
            .padding(10.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color.White)
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = stringResource(id = R.string.action),
                color = Color.Black,
                fontSize = 18.sp
            )
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .size(screenWidth.dp / 4.5f)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color(0xFFBBDEFB))
                        .clickable { showActionDialog = true },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier.size(screenWidth.dp / 8f),
                        painter = painter,
                        contentDescription = "Action type image"
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = text,
                        color = Color.Black,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        content()
    }
}