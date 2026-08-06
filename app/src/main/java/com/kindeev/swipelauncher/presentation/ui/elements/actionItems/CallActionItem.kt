package com.kindeev.swipelauncher.presentation.ui.elements.actionItems

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.presentation.ui.theme.LauncherTheme

@Composable
fun CallActionItem(
    contactOrNumber: String,
    clickOnImage: () -> Unit,
    clickOnNumber: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.call_telephone_image),
            modifier = Modifier
                .size(50.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = clickOnImage)
                .padding(8.dp),
            contentDescription = "call image"
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = clickOnNumber)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = contactOrNumber,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun CallActionLightPreview() {
    LauncherTheme {
        CallActionItem(
            contactOrNumber = "8 (800) 555-35-35",
            clickOnImage = {  },
            clickOnNumber = {  }
        )
    }
}

@Preview
@Composable
fun CallActionDarkPreview() {
    LauncherTheme(darkTheme = true) {
        CallActionItem(
            contactOrNumber = "Александр",
            clickOnImage = {  },
            clickOnNumber = {  }
        )
    }
}
