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
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.utils.openApp
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.LauncherScreenVM
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
    val applications by context.container.applicationsManager.applications.collectAsState()
    val settings by context.container.settings.collectAsState()
    val searchResults = viewModel.getSearchResults(applications)
    var applicationInfoDialog by rememberSaveable {
        mutableStateOf<ApplicationInfo?>(null)
    }

    applicationInfoDialog?.let { applicationInfo ->
        ApplicationInfoDialog(
            viewModel = viewModel,
            applicationInfo = applicationInfo,
            onDismissRequest = { applicationInfoDialog = null }
        )
    }

    if (searchResults.size == 1 && settings.openLastApp && searchText.firstOrNull() != ' '
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
            ) { applicationInfo ->
                SearchAppItem(
                    title = applicationInfo.title,
                    packageName = applicationInfo.packageName,
                    onClick = {
                        if (applicationInfo.packageName == context.packageName) {
                            val intent = Intent(context, SettingsActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            context.openApp(applicationInfo.packageName)
                        }
                        onClose()
                    },
                    onLongClick = {
                        applicationInfoDialog = applicationInfo
                    }
                )
            }
        }
    }
}