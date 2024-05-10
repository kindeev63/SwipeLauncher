package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.CircleMenuItem
import com.kindeev.swipelauncher.domain.getItemOffset

@Composable
fun CircleMenuForEditUI(
    menuSize: Float,
    items: List<CircleMenuItem>,
    selectedBoxOffset: Offset,
    onSelectItem: (CircleMenuItem) -> Unit,
    onAdd: (Offset) -> Unit
) {
    Box(
        modifier = Modifier
            .size(menuSize.dp)
    ) {
        SelectedBox(cords = selectedBoxOffset, size = menuSize / 4)
        CircleMenuItems(items = items, menuSize = menuSize)
        items.forEach { item ->
            val offset = item.offset.getItemOffset(menuSize)
            Box(
                modifier = Modifier
                    .offset(
                        x = offset.x.dp,
                        y = offset.y.dp,
                    )
                    .size((menuSize / 5).dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        onClick = {
                            onSelectItem(item)
                        }
                    )
            )
        }
        val itemCords = items.map { it.offset }
        Constants.menuCords.forEach { cords ->
            if (cords !in itemCords) {
                val offset = cords.getItemOffset(menuSize)
                Icon(
                    modifier = Modifier
                        .offset(
                            x = offset.x.dp,
                            y = offset.y.dp,
                        )
                        .size((menuSize / 5).dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            indication = LocalIndication.current,
                            interactionSource = MutableInteractionSource(),
                            onClick = {
                                onAdd(cords)
                            }
                        ),
                    painter = painterResource(id = R.drawable.ic_add),
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun SelectedBox(
    cords: Offset,
    size: Float
) {
    val offset by animateOffsetAsState(targetValue = cords, label = "")
    Box(
        modifier = Modifier
            .offset(
                x = offset.x.dp,
                y = offset.y.dp
            )
            .size(size.dp)
            .background(
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(16.dp),
            )
    )
}