package com.kindeev.swipelauncher.presentation.ui.elements.searchBox

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.domain.LauncherData

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchAppItem(
    title: String,
    image: ImageBitmap,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val textColorOnWallpaper by LauncherData.textColorOnWallpaper.observeAsState(Color.White)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            modifier = Modifier.size(50.dp),
            bitmap = image,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            color = textColorOnWallpaper,
            maxLines = 1
        )
    }
}