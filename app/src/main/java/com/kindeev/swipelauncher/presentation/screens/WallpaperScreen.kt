package com.kindeev.swipelauncher.presentation.screens

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.cancelChangeHomeScreenWallpaperAlarm
import com.kindeev.swipelauncher.domain.cancelChangeLockScreenWallpaperAlarm
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.HomeScreenWallpaperChange
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.LockScreenWallpaperChange
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.wallpaperChange.WallpaperChangeType
import com.kindeev.swipelauncher.domain.getHomeScreenWallpapersCount
import com.kindeev.swipelauncher.domain.getLockScreenWallpapersCount
import com.kindeev.swipelauncher.domain.getTimeText
import com.kindeev.swipelauncher.domain.getValueOf
import com.kindeev.swipelauncher.domain.setChangeHomeScreenWallpaperAlarm
import com.kindeev.swipelauncher.domain.setChangeLockScreenWallpaperAlarm
import com.kindeev.swipelauncher.domain.spacer
import com.kindeev.swipelauncher.domain.wallpapersHomeScreenDir
import com.kindeev.swipelauncher.domain.wallpapersLockScreenDir
import com.kindeev.swipelauncher.presentation.ui.dialogs.WallpaperChangeTimeDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.WallpaperChangerInfoDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.WallpapersDialog
import com.kindeev.swipelauncher.presentation.ui.elements.settings.SwitchSettingItem
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun WallpaperScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val window = (LocalContext.current as Activity).window
    val view = LocalView.current
    val controller = WindowInsetsControllerCompat(window, view)
    val scope = rememberCoroutineScope()
    var showWallpaperInfoDialog by rememberSaveable {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        controller.isAppearanceLightStatusBars = false
        if (context.isFirstRun()) showWallpaperInfoDialog = true
    }
    BackHandler {
        scope.launch { controller.isAppearanceLightStatusBars = true }
        onBackPressed()
    }
    var wallpapersDirForDialog by remember {
        mutableStateOf<File?>(null)
    }
    val settings by LauncherData.settings.observeAsState(emptyList())
    wallpapersDirForDialog?.let { dir ->
        WallpapersDialog(
            dir = dir,
            onDismissRequest = { wallpapersDirForDialog = null }
        )
    }
    if (showWallpaperInfoDialog) {
        WallpaperChangerInfoDialog(
            onDismissRequest = { showWallpaperInfoDialog = false }
        )
    }
    Scaffold(
        topBar = {
            WallpaperToolbar(
                onBackPressed = {
                    scope.launch { controller.isAppearanceLightStatusBars = true }
                    onBackPressed()
                },
                onClickInfo = {
                    showWallpaperInfoDialog = true
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(10.dp)
                .fillMaxSize()
        ) {

            // Home screen
            item {
                settings.getValueOf(
                    SettingNames.HomeScreenWallpaperChange,
                    HomeScreenWallpaperChange::class.java
                )?.let { value ->
                    SwitchSettingItem(
                        text = stringResource(id = R.string.setting_wallpaper_home_screen),
                        value = value.enabled,
                        onChangeValue = {
                            scope.launch {
                                if (value.changeType == WallpaperChangeType.Time) {
                                    if (it) {
                                        context.setChangeHomeScreenWallpaperAlarm(value.minutes)
                                    } else {
                                        context.cancelChangeHomeScreenWallpaperAlarm()
                                    }
                                }
                                LauncherData.insertSetting(
                                    SettingData(
                                        name = SettingNames.HomeScreenWallpaperChange,
                                        value = value.copy(enabled = it)
                                    )
                                )
                            }
                        },
                        last = !value.enabled
                    )
                    AnimatedVisibility(
                        visible = value.enabled,
                        enter = fadeIn() + expandVertically(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(bottomStart = 7.dp, bottomEnd = 7.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.wallpaper_change_type),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = Constants.minScreenLength.sp / 25
                                )
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    var expanded by remember {
                                        mutableStateOf(false)
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        WallpaperChangeType.values().forEach { changeType ->
                                            if (value.changeType != changeType) {
                                                DropdownMenuItem(
                                                    onClick = {
                                                        scope.launch {
                                                            if (value.changeType == WallpaperChangeType.Time) {
                                                                context.cancelChangeHomeScreenWallpaperAlarm()
                                                            }
                                                            if (changeType == WallpaperChangeType.Time) {
                                                                context.setChangeHomeScreenWallpaperAlarm(value.minutes)
                                                            }
                                                            LauncherData.insertSetting(
                                                                SettingData(
                                                                    name = SettingNames.HomeScreenWallpaperChange,
                                                                    value = value.copy(changeType = changeType)
                                                                )
                                                            )
                                                        }
                                                        expanded = false
                                                    }
                                                ) {
                                                    Text(
                                                        text = stringResource(
                                                            id = Constants.wallpaperChangeTypeText[changeType]
                                                                ?: throw IllegalArgumentException("Illegal wallpaper change type")
                                                        ),
                                                        color = MaterialTheme.colorScheme.onBackground
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    WallpaperChangeTypeElement(
                                        onClick = { expanded = true },
                                        text = stringResource(
                                            id = Constants.wallpaperChangeTypeText[value.changeType]
                                                ?: throw IllegalArgumentException("Illegal wallpaper change type")
                                        )
                                    )
                                }
                            }
                            AnimatedVisibility(
                                visible = value.changeType == WallpaperChangeType.Time,
                                enter = fadeIn() + expandVertically(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    ChangeTime(
                                        minutes = value.minutes,
                                        onChange = {
                                            scope.launch {
                                                LauncherData.insertSetting(
                                                    SettingData(
                                                        name = SettingNames.HomeScreenWallpaperChange,
                                                        value = value.copy(minutes = it)
                                                    )
                                                )
                                                context.setChangeHomeScreenWallpaperAlarm(it)
                                            }
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .clickable {
                                            wallpapersDirForDialog =
                                                context.wallpapersHomeScreenDir()
                                        }
                                        .padding(horizontal = 25.dp, vertical = 10.dp),
                                    text = stringResource(id = R.string.wallpapers_conut) + " ${context.getHomeScreenWallpapersCount()}",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }

            spacer()

            // Lock screen
            item {
                settings.getValueOf(
                    SettingNames.LockScreenWallpaperChange,
                    LockScreenWallpaperChange::class.java
                )?.let { value ->
                    SwitchSettingItem(
                        text = stringResource(id = R.string.setting_wallpaper_lock_screen),
                        value = value.enabled,
                        onChangeValue = {
                            scope.launch {
                                if (value.changeType == WallpaperChangeType.Time) {
                                    if (it) {
                                        context.setChangeLockScreenWallpaperAlarm(value.minutes)
                                    } else {
                                        context.cancelChangeLockScreenWallpaperAlarm()
                                    }
                                }
                                LauncherData.insertSetting(
                                    SettingData(
                                        name = SettingNames.LockScreenWallpaperChange,
                                        value = value.copy(enabled = it)
                                    )
                                )
                            }
                        },
                        last = !value.enabled
                    )
                    AnimatedVisibility(
                        visible = value.enabled,
                        enter = fadeIn() + expandVertically(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(bottomStart = 7.dp, bottomEnd = 7.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(id = R.string.wallpaper_change_type),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = Constants.minScreenLength.sp / 25
                                )
                                Box(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    var expanded by remember {
                                        mutableStateOf(false)
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                    ) {
                                        WallpaperChangeType.values().forEach { changeType ->
                                            if (value.changeType != changeType) {
                                                DropdownMenuItem(
                                                    onClick = {
                                                        scope.launch {
                                                            if (value.changeType == WallpaperChangeType.Time) {
                                                                context.cancelChangeLockScreenWallpaperAlarm()
                                                            }
                                                            if (changeType == WallpaperChangeType.Time) {
                                                                context.setChangeLockScreenWallpaperAlarm(value.minutes)
                                                            }
                                                            LauncherData.insertSetting(
                                                                SettingData(
                                                                    name = SettingNames.LockScreenWallpaperChange,
                                                                    value = value.copy(changeType = changeType)
                                                                )
                                                            )
                                                        }
                                                        expanded = false
                                                    }
                                                ) {
                                                    Text(
                                                        text = stringResource(
                                                            id = Constants.wallpaperChangeTypeText[changeType]
                                                                ?: throw IllegalArgumentException("Illegal wallpaper change type")
                                                        ),
                                                        color = MaterialTheme.colorScheme.onBackground
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    WallpaperChangeTypeElement(
                                        onClick = { expanded = true },
                                        text = stringResource(
                                            id = Constants.wallpaperChangeTypeText[value.changeType]
                                                ?: throw IllegalArgumentException("Illegal wallpaper change type")
                                        )
                                    )
                                }
                            }
                            AnimatedVisibility(
                                visible = value.changeType == WallpaperChangeType.Time,
                                enter = fadeIn() + expandVertically(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    ChangeTime(
                                        minutes = value.minutes,
                                        onChange = {
                                            scope.launch {
                                                LauncherData.insertSetting(
                                                    SettingData(
                                                        name = SettingNames.LockScreenWallpaperChange,
                                                        value = value.copy(minutes = it)
                                                    )
                                                )
                                                context.setChangeLockScreenWallpaperAlarm(it)
                                            }
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .clickable {
                                            wallpapersDirForDialog =
                                                context.wallpapersLockScreenDir()
                                        }
                                        .padding(horizontal = 25.dp, vertical = 10.dp),
                                    text = stringResource(id = R.string.wallpapers_conut) + " ${context.getLockScreenWallpapersCount()}",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WallpaperToolbar(
    onBackPressed: () -> Unit,
    onClickInfo: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(MaterialTheme.colorScheme.primary)
            .shadow(elevation = 1.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onBackPressed()
            }
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(id = R.string.setting_wallpaper),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp
        )
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onClickInfo()
                }
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun WallpaperChangeTypeElement(
    text: String,
    onClick: () -> Unit
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        text = text,
        fontSize = LocalConfiguration.current.screenWidthDp.sp / 25,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
    )
}

private fun Context.isFirstRun(): Boolean {
    val prefs = getSharedPreferences("wallpaper", Context.MODE_PRIVATE)
    return if (prefs.contains("first_run")) false
    else {
        val editor = prefs.edit()
        editor.putString("first_run", "false")
        editor.apply()
        true
    }
}

@Composable
private fun ChangeTime(
    minutes: Int,
    onChange: (Int) -> Unit
) {
    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showDialog) {
        WallpaperChangeTimeDialog(
            minutes = minutes,
            onSave = onChange,
            onDismissRequest = {
                showDialog = false
            }
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.every),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = Constants.minScreenLength.sp / 25
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable {
                    showDialog = true
                }
                .padding(vertical = 10.dp),
            text = LocalContext.current.getTimeText(minutes),
            fontSize = LocalConfiguration.current.screenWidthDp.sp / 25,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}