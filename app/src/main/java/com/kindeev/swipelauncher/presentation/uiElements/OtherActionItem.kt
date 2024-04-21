package com.kindeev.swipelauncher.presentation.uiElements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.dialogTabs.OtherAction

@Composable
fun OtherActionItem(
    otherAction: OtherAction,
    picked: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(if (picked) Color.Gray.copy(alpha = 0.5f) else Color.Transparent)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ){
        Constants.defaultImages[otherAction.image]?.let { resourceId ->
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(id = resourceId),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(id = otherAction.nameResourceId),
            fontSize = 14.sp,
            color = Color.Black,
            maxLines = 1
        )
    }
}