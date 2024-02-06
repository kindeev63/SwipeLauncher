package com.kindeev.swipelauncher.presentation.uiElements

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClockWidget() {
    var time by remember {
        val locTime = LocalDateTime.now()
        mutableStateOf("${locTime.hour}:${locTime.minute}")
    }
    var date by remember {
        val locTime = LocalDateTime.now()
        mutableStateOf("${locTime.dayOfWeek}, ${locTime.dayOfMonth} ${locTime.month.name}")
    }
    LaunchedEffect(Unit) {
        delay((60 - LocalDateTime.now().second) * 1000L)
        while (true) {
            val locTime = LocalDateTime.now()
            time = "${locTime.hour}:${locTime.minute}"
            date = "${locTime.dayOfWeek}, ${locTime.dayOfMonth} ${locTime.month.name}"
            delay(60000L)
        }

    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = time,
            color = Color.White,
            fontSize = (LocalConfiguration.current.screenWidthDp/5).sp
        )
        Text(
            text = date,
            color = Color.White,
            fontSize = (LocalConfiguration.current.screenWidthDp/20).sp
        )
    }

}