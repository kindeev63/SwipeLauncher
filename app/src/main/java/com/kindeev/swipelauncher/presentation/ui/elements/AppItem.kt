package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppItem(
    title: String,
    image: ImageBitmap,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                onClick()
            },
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
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
        )
    }
}