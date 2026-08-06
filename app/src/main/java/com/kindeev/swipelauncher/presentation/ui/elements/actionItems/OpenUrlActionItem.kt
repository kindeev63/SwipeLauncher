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
fun OpenUrlActionItem(
    url: String,
    clickOnImage: () -> Unit,
    clickOnUrl: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.open_url_image),
            modifier = Modifier
                .size(50.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable(onClick = clickOnImage)
                .padding(8.dp),
            contentDescription = "Url image"
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .clickable(onClick = clickOnUrl),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = url,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun OpenUrlActionLightPreview() {
    LauncherTheme {
        OpenUrlActionItem(
            url = "https://google.com/",
            clickOnImage = {  },
            clickOnUrl = { }
        )
    }
}

@Preview
@Composable
fun OpenUrlActionDarkPreview() {
    LauncherTheme(darkTheme = true) {
        OpenUrlActionItem(
            url = "https://google.com/",
            clickOnImage = {  },
            clickOnUrl = { }
        )
    }
}
