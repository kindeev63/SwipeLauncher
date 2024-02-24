package com.kindeev.swipelauncher.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.gson.Gson
import com.kindeev.swipelauncher.data.GlobalExceptionHandler
import com.kindeev.swipelauncher.data.ui.theme.SettingsScreenTheme


class ErrorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreenTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val error = try {
                        Gson().fromJson(
                            intent.getStringExtra(GlobalExceptionHandler.THROWABLE_KEY),
                            Throwable::class.java
                        )
                    } catch (e: Exception) {
                        null
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("ERROR:")
                        error?.message?.let {
                            Text(text = it)
                        }
                        Button(onClick = {
                            restart()
                        }) {
                            Text("Restart")
                        }
                        Button(onClick = {
                            changeDefaultLauncher()
                        }) {
                            Text("Change default launcher")
                        }
                    }
                }
            }
        }
    }
    private fun restart() {
        val packageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        mainIntent.setPackage(packageName)
        startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    private fun changeDefaultLauncher() {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}