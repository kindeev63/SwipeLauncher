package com.kindeev.swipelauncher.presentation.uiElements

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.domain.entities.ApplicationData

@Composable
fun AppItem(
    applicationData: ApplicationData,
    picked: Boolean = false,
    textColor: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(if (picked) Color.Gray.copy(alpha = 0.5f) else Color.Transparent)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            modifier = Modifier.size(50.dp),
            bitmap = applicationData.icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text=applicationData.name,
            fontSize = 14.sp,
            color = textColor,
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItem(
    applicationData: ApplicationData,
    picked: Boolean = false,
    textColor: Color = Color.Black,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(if (picked) Color.Gray.copy(alpha = 0.5f) else Color.Transparent)
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable (
                onClick = onClick,
                onLongClick = onLongClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            modifier = Modifier.size(50.dp),
            bitmap = applicationData.icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text=applicationData.name,
            fontSize = 14.sp,
            color = textColor,
            maxLines = 1
        )
    }
}