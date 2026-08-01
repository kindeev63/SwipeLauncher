package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R

@Composable
fun MaterialIcon(
    unicode: String,
    modifier: Modifier = Modifier,
    size: Int = 24,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        text = unicode,
        fontFamily = FontFamily(Font(R.font.material_icons_outlined)),
        fontSize = size.sp,
        color = color,
        modifier = modifier,
        style = TextStyle(
            lineHeight = size.sp
        )
    )
}