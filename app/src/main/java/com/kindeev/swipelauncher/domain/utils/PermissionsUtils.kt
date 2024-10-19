package com.kindeev.swipelauncher.domain.utils

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReadContactsPermission(
    result: (Boolean) -> Unit
) {
    val permissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)
    if (permissionState.status.isGranted) {
        result(true)
    } else {
        if (permissionState.status.shouldShowRationale) {
            result(false)
        } else {
            LaunchedEffect(permissionState) {
                permissionState.launchPermissionRequest()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallPermission(
    result: (Boolean) -> Unit
) {
    val permissionState = rememberPermissionState(Manifest.permission.CALL_PHONE)
    if (permissionState.status.isGranted) {
        result(true)
    } else {
        if (permissionState.status.shouldShowRationale) {
            result(false)
        } else {
            LaunchedEffect(permissionState) {
                permissionState.launchPermissionRequest()
            }
        }
    }
}