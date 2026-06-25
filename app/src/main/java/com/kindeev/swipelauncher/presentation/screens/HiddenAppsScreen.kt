package com.kindeev.swipelauncher.presentation.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.viewModels.screens.hiddenAppsScreen.HiddenAppsScreenVM
import com.kindeev.swipelauncher.domain.viewModels.screens.hiddenAppsScreen.HiddenAppsScreenVMFactory
import com.kindeev.swipelauncher.presentation.ui.dialogs.QuestionDialog
import com.kindeev.swipelauncher.presentation.ui.elements.AppItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HiddenAppsScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: HiddenAppsScreenVM = viewModel(
        factory = HiddenAppsScreenVMFactory(context)
    )
    val window = (LocalContext.current as Activity).window
    val view = LocalView.current
    val controller = WindowInsetsControllerCompat(window, view)
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        controller.isAppearanceLightStatusBars = false
    }
    BackHandler {
        scope.launch { controller.isAppearanceLightStatusBars = true }
        onBackPressed()
    }
    val allApplicationInfo by LauncherData.allApplicationInfo.collectAsState()
    val allApplicationData by LauncherData.allApplicationData.collectAsState()

    var questionDialog by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    questionDialog?.let { packageName ->
        QuestionDialog(
            text = stringResource(R.string.show_app_question),
            onClickYes = {
                viewModel.showApp(packageName)
                questionDialog = null
            },
            onDismissRequest = { questionDialog = null }
        )
    }
    val data = viewModel.getHiddenApps(allApplicationInfo, allApplicationData)
    Scaffold(
        topBar = {
            HiddenAppsToolbar(
                onBackPressed = {
                    scope.launch { controller.isAppearanceLightStatusBars = true }
                    onBackPressed()
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(
                items = data
            ) { applicationData ->
                AppItem(
                    title = applicationData.title,
                    image = applicationData.image,
                    onClick = { questionDialog = applicationData.packageName }
                )
            }
        }
    }
}

@Composable
private fun HiddenAppsToolbar(
    onBackPressed: () -> Unit
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
            text = stringResource(id = R.string.setting_hidden_apps),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 20.sp
        )
    }
}