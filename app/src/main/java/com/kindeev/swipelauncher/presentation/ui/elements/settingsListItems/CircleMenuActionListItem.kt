package com.kindeev.swipelauncher.presentation.ui.elements.settingsListItems

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CallAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.DialAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenUrlAction
import com.kindeev.swipelauncher.domain.utils.ReadContactsPermission
import com.kindeev.swipelauncher.presentation.entities.ActionItemData
import com.kindeev.swipelauncher.presentation.ui.dialogs.EnterNumberDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.OpenUrlCategory
import com.kindeev.swipelauncher.presentation.ui.dialogs.pickContactLauncher
import com.kindeev.swipelauncher.presentation.ui.elements.CircleMenuActionItem
import java.io.Serializable

private sealed class PhoneActionDialog: Serializable {
    object Hide : PhoneActionDialog()
    data class Call(val phoneNumber: String) : PhoneActionDialog()
    data class Dial(val phoneNumber: String) : PhoneActionDialog()
}

private sealed class UrlActionDialog: Serializable {
    object Hide : UrlActionDialog()
    data class Show(val url: String) : UrlActionDialog()
}

@Composable
fun CircleMenuActionListItem(
    actionItemData: ActionItemData,
    changeAction: (CircleMenuAction) -> Unit,
    openActionDialog: () -> Unit
) {
    var editPhoneNumberDialog by rememberSaveable {
        mutableStateOf<PhoneActionDialog>(PhoneActionDialog.Hide)
    }
    var editUrlDialog by rememberSaveable {
        mutableStateOf<UrlActionDialog>(UrlActionDialog.Hide)
    }
    when (val data = editPhoneNumberDialog) {
        is PhoneActionDialog.Call -> {
            var number by rememberSaveable {
                mutableStateOf(data.phoneNumber)
            }
            val pickContact = pickContactLauncher(
                onPick = { number = it }
            )
            var hasReadContactsPermission by rememberSaveable {
                mutableStateOf<Boolean?>(null)
            }
            if (hasReadContactsPermission == null) {
                ReadContactsPermission {
                    hasReadContactsPermission = it
                }
            }
            EnterNumberDialog(
                phoneNumber = number,
                onChangeNumber = { number = it },
                pickContact = {
                    if (hasReadContactsPermission == true) {
                        pickContact.launch(null)
                    }
                },
                onEnter = {
                    changeAction(CallAction(number))
                },
                onDismissRequest = {
                    editPhoneNumberDialog = PhoneActionDialog.Hide
                }
            )
        }

        is PhoneActionDialog.Dial -> {
            var number by rememberSaveable {
                mutableStateOf(data.phoneNumber)
            }
            val pickContact = pickContactLauncher(
                onPick = { number = it }
            )
            var hasReadContactsPermission by rememberSaveable {
                mutableStateOf<Boolean?>(null)
            }
            if (hasReadContactsPermission == null) {
                ReadContactsPermission {
                    hasReadContactsPermission = it
                }
            }
            EnterNumberDialog(
                phoneNumber = number,
                onChangeNumber = { number = it },
                pickContact = {
                    if (hasReadContactsPermission == true) {
                        pickContact.launch(null)
                    }
                },
                onEnter = {
                    changeAction(DialAction(number))
                },
                onDismissRequest = {
                    editPhoneNumberDialog = PhoneActionDialog.Hide
                }
            )
        }
        PhoneActionDialog.Hide -> {}
    }
    when (val data = editUrlDialog) {
        is UrlActionDialog.Show -> {
            var url by remember {
                mutableStateOf(TextFieldValue(data.url))
            }
            OpenUrlCategory(
                url = url,
                onChangeUrl = { url = it },
                onPick = {
                    changeAction(OpenUrlAction(url.text))
                },
                onDismissRequest = {
                    editUrlDialog = UrlActionDialog.Hide
                }
            )
        }
        UrlActionDialog.Hide -> {}
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        CircleMenuActionItem(
            actionItemData = actionItemData,
            textColor = MaterialTheme.colorScheme.onSurface,
            changeAction = openActionDialog,
            changePhoneNumber = {
                when (actionItemData) {
                    is ActionItemData.Call -> {
                        editPhoneNumberDialog = PhoneActionDialog.Call(actionItemData.phoneNumber)
                    }

                    is ActionItemData.Dial -> {
                        editPhoneNumberDialog = PhoneActionDialog.Dial(actionItemData.phoneNumber)
                    }

                    else -> {}
                }
            },
            changeUrl = {
                if (actionItemData is ActionItemData.OpenUrl) {
                    editUrlDialog = UrlActionDialog.Show(actionItemData.url)
                }
            },
        )
    }
}