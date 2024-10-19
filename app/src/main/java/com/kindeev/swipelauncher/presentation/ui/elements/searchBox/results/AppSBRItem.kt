package com.kindeev.swipelauncher.presentation.ui.elements.searchBox.results

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.utils.executeSearchResult
import com.kindeev.swipelauncher.presentation.entities.searchBox.AppSBR
import com.kindeev.swipelauncher.presentation.ui.dialogs.ApplicationInfoDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppSBRItem(
    data: AppSBR,
    onClose: () -> Unit
) {
    val textColorOnWallpaper by LauncherData.textColorOnWallpaper.observeAsState(Color.White)
    val context = LocalContext.current
    var showApplicationDataDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showApplicationDataDialog) {
        ApplicationInfoDialog(
            applicationInfo = data.applicationInfo,
            onDismissRequest = { showApplicationDataDialog = false }
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = {
                    context.executeSearchResult(data)
                    onClose()
                },
                onLongClick = { showApplicationDataDialog = true }
            ),
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            modifier = Modifier.size(50.dp),
            bitmap = data.applicationInfo.icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = data.applicationInfo.title,
            fontSize = 14.sp,
            color = textColorOnWallpaper,
            maxLines = 1
        )
    }
}