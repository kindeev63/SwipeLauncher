package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeAppItem(
    applicationData: ApplicationData,
    textColor: Color = Color.Black,
    onDelete: () -> Unit,
    onGetAppInfo: () -> Unit,
    onClick: () -> Unit
) {
    val swipeState = rememberDismissState(
        confirmStateChange = { value ->
            when (value) {
                DismissValue.DismissedToStart -> {
                    onDelete()
                    false
                }
                DismissValue.DismissedToEnd -> {
                    onGetAppInfo()
                    false
                }
                DismissValue.Default -> true
            }
        }
    )
    SwipeToDismiss(
        state = swipeState,
        dismissThresholds = { FractionalThreshold(0.2f) },
        background = {
            val direction = swipeState.dismissDirection ?: return@SwipeToDismiss
            if (swipeState.targetValue == DismissValue.Default) return@SwipeToDismiss
            val color by animateColorAsState(
                targetValue = when(swipeState.targetValue) {
                    DismissValue.Default -> Color.Transparent
                    DismissValue.DismissedToEnd -> Color.LightGray.copy(alpha = 0.4f)
                    DismissValue.DismissedToStart -> Color.Red.copy(alpha = 0.4f)
                },
                label = "Swipe background color"
            )
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Info
                DismissDirection.EndToStart -> Icons.Default.Delete
            }
            val scale by animateFloatAsState(targetValue = if (swipeState.targetValue == DismissValue.Default) 0.8f else 1.2f,
                label = ""
            )

            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color)
                    .padding(horizontal = 12.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    modifier = Modifier.scale(scale),
                    imageVector = icon,
                    contentDescription = "Icon"
                )
            }
        },
        dismissContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onClick),
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
    )
}