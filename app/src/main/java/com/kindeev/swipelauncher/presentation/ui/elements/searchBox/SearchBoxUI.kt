package com.kindeev.swipelauncher.presentation.ui.elements.searchBox

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.di.container
import com.kindeev.swipelauncher.domain.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.entities.settings.settingValues.OpenLastApp
import com.kindeev.swipelauncher.domain.utils.getValueOf
import com.kindeev.swipelauncher.domain.utils.openApp
import com.kindeev.swipelauncher.domain.viewModels.screens.launcherScreen.LauncherScreenVM
import com.kindeev.swipelauncher.presentation.activities.SettingsActivity
import com.kindeev.swipelauncher.presentation.ui.dialogs.ApplicationInfoDialog

@Composable
fun SearchBoxUI(
    viewModel: LauncherScreenVM,
    onClose: () -> Unit
) {
    BackHandler(onBack = onClose)
    val context = LocalContext.current
    val searchText by viewModel.searchText.collectAsState()
    val allApplicationInfo by context.container.applicationsInfo.collectAsState()
    val settings by context.container.settings.collectAsState()
    val searchResults = viewModel.getSearchResults(allApplicationInfo)
    var applicationInfoDialog by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    applicationInfoDialog?.let { packageName ->
        ApplicationInfoDialog(
            viewModel = viewModel,
            packageName = packageName,
            onDismissRequest = { applicationInfoDialog = null }
        )
    }

    if (searchResults.size == 1 && settings.getValueOf(
            SettingNames.OpenLastApp,
            OpenLastApp::class.java
        )?.enabled == true && searchText.firstOrNull() != ' '
    ) {
        searchResults.firstOrNull()?.let {
            if (it.packageName == context.packageName) {
                val intent = Intent(context, SettingsActivity::class.java)
                context.startActivity(intent)
            } else {
                context.openApp(it.packageName)
            }
        }
        onClose()
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
        )
        SearchBoxSearchElement(searchText = searchText, onChangeText = { viewModel.search(it) })
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = searchResults
            ) { applicationData ->
                SearchAppItem(
                    title = applicationData.title,
                    image = applicationData.image,
                    onClick = {
                        if (applicationData.packageName == context.packageName) {
                            val intent = Intent(context, SettingsActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            context.openApp(applicationData.packageName)
                        }
                        onClose()
                    },
                    onLongClick = {
                        applicationInfoDialog = applicationData.packageName
                    }
                )
            }
        }
    }
}