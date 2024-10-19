package com.kindeev.swipelauncher.presentation.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.utils.getItemOffset
import com.kindeev.swipelauncher.domain.viewModels.elements.cirlceMenuItems.CircleMenuItemsVM
import com.kindeev.swipelauncher.domain.viewModels.elements.cirlceMenuItems.CircleMenuItemsVMFactory

@Composable
fun CircleMenuItems(
    items: List<CircleMenuItem>,
    menuSize: Float
) {
    Box(
        modifier = Modifier
            .size(menuSize.dp)
    ) {
        val context = LocalContext.current
        val viewModel: CircleMenuItemsVM = viewModel(
            factory = CircleMenuItemsVMFactory(context)
        )
        items.forEach { item ->
            viewModel.getItemImage(item.image)?.let { imageBitmap ->
                val offset = item.offset.getItemOffset(menuSize)
                Image(
                    modifier = Modifier
                        .offset(
                            x = offset.x.dp,
                            y = offset.y.dp
                        )
                        .size((menuSize / 5).dp),
                    bitmap = imageBitmap,
                    contentDescription = null
                )
            }
        }
    }
}