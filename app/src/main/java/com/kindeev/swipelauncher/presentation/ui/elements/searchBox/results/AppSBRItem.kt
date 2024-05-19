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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.executeSearchResult
import com.kindeev.swipelauncher.presentation.entities.searchBox.AppSBR

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppSBRItem(
    data: AppSBR,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = {
                    context.executeSearchResult(data)
                    onClose()
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            modifier = Modifier.size(50.dp),
            bitmap = data.applicationData.icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = data.applicationData.name,
            fontSize = 14.sp,
            color = LauncherData.textColorOnWallpaper,
            maxLines = 1
        )
    }
}