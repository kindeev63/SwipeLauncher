package com.kindeev.swipelauncher.presentation.ui.elements

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.kindeev.swipelauncher.domain.utils.ReadContactsPermission
import com.kindeev.swipelauncher.domain.utils.formatPhoneNumber
import com.kindeev.swipelauncher.domain.utils.getContactName

@Composable
fun ContactNameOrPhoneNumber(phoneNumber: String, content: @Composable (String) -> Unit) {
    val context = LocalContext.current
    var hasReadContactsPermission by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }
    Log.e("test", hasReadContactsPermission.toString())
    when (hasReadContactsPermission) {
        true -> content(
            context.getContactName(phoneNumber) ?: phoneNumber.formatPhoneNumber()
        )
        false -> content(phoneNumber.formatPhoneNumber())
        null -> {
            ReadContactsPermission {
                Log.e("test", it.toString())
                hasReadContactsPermission = it
            }
        }
    }
}