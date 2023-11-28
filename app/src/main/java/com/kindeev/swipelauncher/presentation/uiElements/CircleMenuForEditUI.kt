package com.kindeev.swipelauncher.presentation.uiElements

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.presentation.MainAppViewModel

@Composable
fun EditCircleMenuScreen(mainAppViewModel: MainAppViewModel) {
    val context = LocalContext.current
    val menuSize = context.resources.configuration.screenWidthDp / 3f * 2f
    var circleMenu by remember {
        mutableStateOf<CircleMenu?>(null)
    }
    LaunchedEffect(Unit) {
        mainAppViewModel.allCircleMenu.value?.find { it.id == 0 }?.let {
            circleMenu = it
        }
    }
    Column {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            circleMenu?.let { menu ->
                CircleMenuForEditUI(
                    menuSize = menuSize,
                    menuImages = menu.menuImages,
                    upImageClick = {
                        Toast.makeText(context, "Up", Toast.LENGTH_SHORT).show()
                    },
                    downImageClick = {
                        Toast.makeText(context, "Down", Toast.LENGTH_SHORT).show()
                    },
                    rightImageClick = {
                        Toast.makeText(context, "Right", Toast.LENGTH_SHORT).show()
                    },
                    leftImageClick = {
                        Toast.makeText(context, "Left", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}