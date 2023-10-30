package com.kindeev.swipelauncher.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainAppViewModel = (application as MainApp).mainAppViewModel
        setContent {
            val allCircleMenu = mainAppViewModel.allCircleMenu.observeAsState()
            if (allCircleMenu.value.isNullOrEmpty()) {
                FirstScreen(
                    mainAppViewModel = mainAppViewModel
                )
            } else {
                SwipeBox()
            }
        }
    }
}

@Composable
fun Setting(context: Context) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)

            }
        ) {
            Text(text = "Setting")
        }
    }
}

