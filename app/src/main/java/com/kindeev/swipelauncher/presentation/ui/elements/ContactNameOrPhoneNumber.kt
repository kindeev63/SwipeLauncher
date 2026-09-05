package com.kindeev.swipelauncher.presentation.ui.elements

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.kindeev.swipelauncher.domain.utils.ReadContactsPermission

@Composable
fun ContactNameOrPhoneNumber(phoneNumber: String, content: @Composable (String) -> Unit) {
    val context = LocalContext.current
    var hasReadContactsPermission by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }
    when (hasReadContactsPermission) {
        true -> content(
            context.getContactName(phoneNumber) ?: phoneNumber.formatPhoneNumber()
        )
        false -> content(phoneNumber.formatPhoneNumber())
        null -> {
            ReadContactsPermission {
                hasReadContactsPermission = it
            }
        }
    }
}

private fun String.formatPhoneNumber(): String {
    return if (this.length == 11) {
        "${this[0]} (${this.substring(1, 4)}) ${
            this.substring(4, 7)
        }-${this.substring(7, 9)}-${this.substring(9)}"
    } else this
}
private fun Context.getContactName(phoneNumber: String): String? {
    val uri = Uri.withAppendedPath(
        ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
        Uri.encode(phoneNumber)
    )
    val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
    val cursor = contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            return cursor.getString(0)
        }
    }
    return null
}
