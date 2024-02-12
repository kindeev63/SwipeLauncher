package com.kindeev.swipelauncher.presentation.uiElements

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import kotlinx.coroutines.delay
import java.time.LocalDateTime


@Composable
fun ClockWidget() {
    val context = LocalContext.current
    var time by remember {
        val locTime = LocalDateTime.now()
        mutableStateOf(getTime(locTime))
    }
    var date by remember {
        val locTime = LocalDateTime.now()
        mutableStateOf(getDate(context, locTime))
    }
    LaunchedEffect(Unit) {
        delay((60 - LocalDateTime.now().second) * 1000L)
        while (true) {
            val locTime = LocalDateTime.now()
            time = getTime(locTime)
            date = getDate(context, locTime)
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

private fun getTime(localDateTime: LocalDateTime) =
    if (localDateTime.minute > 9) {
        "${localDateTime.hour}:${localDateTime.minute}"
    } else {
        "${localDateTime.hour}:0${localDateTime.minute}"
    }


private fun getDate(context: Context, localDateTime: LocalDateTime): String {
    val weekdays = context.resources.getStringArray(R.array.weekday)
    val months = context.resources.getStringArray(R.array.months)
    return "${weekdays[localDateTime.dayOfWeek.value]}, ${localDateTime.dayOfMonth} ${months[localDateTime.month.value]}"
}