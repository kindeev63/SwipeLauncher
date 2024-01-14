package com.kindeev.swipelauncher.presentation.uiElements.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.data.ApplicationData
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.presentation.uiElements.AppItem

@Composable
fun PickAppDialog(
    pickedPackageName: String?,
    onPick: (ApplicationData) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ){
        Column(
            modifier = androidx.compose.ui.Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .heightIn(max = (screenConfiguration.screenHeightDp / 3 * 2).dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn {
                items(
                    items = DataObject.allApplicationData,
                    key = { it.packageName }
                ) { applicationData ->
                    AppItem(
                        applicationData = applicationData,
                        picked = applicationData.packageName == pickedPackageName
                    ) {
                        onPick(applicationData)
                    }
                }
            }
        }
    }
}