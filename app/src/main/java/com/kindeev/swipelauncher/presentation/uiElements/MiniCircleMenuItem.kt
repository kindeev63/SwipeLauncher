package com.kindeev.swipelauncher.presentation.uiElements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.data.RootCircleMenu
import com.kindeev.swipelauncher.domain.CircleMenu

@Composable
fun MiniCircleMenuItem(
    size: Float,
    picked: Boolean = false,
    circleMenu: CircleMenu,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(size.dp)
            .padding(3.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (picked) Color.Gray.copy(alpha = 0.5f) else Color.White)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
            .fillMaxHeight(5/6f),
            contentAlignment = Alignment.Center
        ) {
            CircleMenuImagesUI(
                menuSize = (size - 6) * 4/6f ,
                menuImages = circleMenu.menuImages
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = circleMenu.title,
                fontSize = (size / 12).sp
            )
        }
    }

}