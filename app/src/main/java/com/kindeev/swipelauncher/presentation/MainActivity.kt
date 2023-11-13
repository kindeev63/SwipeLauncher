package com.kindeev.swipelauncher.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kindeev.swipelauncher.domain.CircleMenu

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainAppViewModel = (application as MainApp).mainAppViewModel
        setContent {
            var allCircleMenu by remember {
                mutableStateOf<List<CircleMenu>?>(null)
            }
            mainAppViewModel.allCircleMenu.observe(this) {
                allCircleMenu = it
            }
            allCircleMenu?.let { circleMenus ->
                Log.e("test", circleMenus.isEmpty().toString())
                if (circleMenus.isEmpty()) {
                    FirstScreenUI(
                        mainAppViewModel = mainAppViewModel
                    )
                } else {
                    SwipeBoxUI(
                        mainAppViewModel = mainAppViewModel
                    )
                }
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

