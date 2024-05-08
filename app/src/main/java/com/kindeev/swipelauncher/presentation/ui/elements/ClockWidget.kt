package com.kindeev.swipelauncher.presentation.ui.elements

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import kotlinx.coroutines.delay
import java.time.LocalDateTime


@Composable
fun ClockWidget(
) {
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
        while (true) {
            val locTime = LocalDateTime.now()
            time = getTime(locTime)
            date = getDate(context, locTime)
            delay(1000L)
        }

    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = time,
            color = LauncherData.textColorOnWallpaper,
            fontSize = Constants.minScreenLength.sp / 5
        )
        Text(
            text = date,
            color = LauncherData.textColorOnWallpaper,
            fontSize = Constants.minScreenLength.sp / 20
        )
    }
}

@Composable
fun ClickableClockWidget(
    onClick: () -> Unit
) {
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
        while (true) {
            val locTime = LocalDateTime.now()
            time = getTime(locTime)
            date = getDate(context, locTime)
            delay(1000L)
        }

    }

    Column(
        modifier = Modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = time,
            color = LauncherData.textColorOnWallpaper,
            fontSize = Constants.minScreenLength.sp / 5
        )
        Text(
            text = date,
            color = LauncherData.textColorOnWallpaper,
            fontSize = Constants.minScreenLength.sp / 20
        )
    }
}

private fun getTime(localDateTime: LocalDateTime): String {
    val hour = if (localDateTime.hour > 9) localDateTime.hour.toString() else "0${localDateTime.hour}"
    val minute = if (localDateTime.minute > 9) localDateTime.minute.toString() else "0${localDateTime.minute}"
    return "$hour:$minute"
}



private fun getDate(context: Context, localDateTime: LocalDateTime): String {
    val weekdays = context.resources.getStringArray(R.array.weekday)
    val months = context.resources.getStringArray(R.array.months)
    return "${weekdays[localDateTime.dayOfWeek.value-1]}, ${localDateTime.dayOfMonth} ${months[localDateTime.month.value-1]}"
}