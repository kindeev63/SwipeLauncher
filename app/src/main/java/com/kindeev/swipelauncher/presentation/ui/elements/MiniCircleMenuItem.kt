package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.domain.entities.CircleMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniCircleMenuItem(
    size: Float,
    circleMenu: CircleMenu,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .padding(3.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ),
            onClick = onClick
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(5 / 6f),
                contentAlignment = Alignment.Center
            ) {
                CircleMenuImagesUI(
                    menuSize = (size - 6) * 4 / 6f,
                    menuImages = circleMenu.menuImages
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = circleMenu.title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = (size / 12).sp
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MiniCircleMenuItem(
    size: Float,
    root: Boolean = false,
    circleMenu: CircleMenu,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .padding(3.dp)
    ) {
        Card(
            modifier = Modifier
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (root) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(5 / 6f),
                contentAlignment = Alignment.Center
            ) {
                CircleMenuImagesUI(
                    menuSize = (size - 6) * 4 / 6f,
                    menuImages = circleMenu.menuImages
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = circleMenu.title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = (size / 12).sp
                )
            }
        }
    }
}