package com.kindeev.swipelauncher.presentation.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.kindeev.swipelauncher.presentation.GlobalExceptionHandler
import com.kindeev.swipelauncher.presentation.ui.theme.SettingsScreenTheme
import java.io.PrintWriter
import java.io.StringWriter


class ErrorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsScreenTheme {
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
                        modifier = Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("ERROR:")
                        error?.let {
                            Text(text = printStackTraceToString(it))
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
                        error?.let {
                            Button(onClick = {
                                copyTextToClipboard(printStackTraceToString(it))
                            }) {
                                Text(text = "Copy error")
                            }
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

    private fun printStackTraceToString(throwable: Throwable): String {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }

    private fun copyTextToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }
}