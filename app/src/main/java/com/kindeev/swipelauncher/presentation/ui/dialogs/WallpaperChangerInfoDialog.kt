package com.kindeev.swipelauncher.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.getAppDetails

@Composable
fun WallpaperChangerInfoDialog(
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val screenConfiguration = LocalConfiguration.current
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .width(screenConfiguration.screenWidthDp.dp - 20.dp)
                .heightIn(max = (screenConfiguration.screenHeightDp / 3 * 2).dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.wallpaper_changer_info),
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(
                onClick = {
                    context.getAppDetails(context.packageName)
                    onDismissRequest()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.go_to_settings),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}