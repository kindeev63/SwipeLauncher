package com.kindeev.swipelauncher.presentation.activities

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.data.ui.theme.LauncherScreenTheme
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.presentation.MainApp
import com.kindeev.swipelauncher.presentation.receivers.AppsReceiver
import com.kindeev.swipelauncher.presentation.screens.LauncherScreen
import com.kindeev.swipelauncher.presentation.uiElements.FirstScreenUI

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainAppViewModel = (application as MainApp).mainAppViewModel
        DataObject.setAllApplicationData(this)
        registerReceiver()
        setContent {
            LauncherScreenTheme {
                var allCircleMenu by remember {
                    mutableStateOf<List<CircleMenu>?>(null)
                }
                mainAppViewModel.allCircleMenu.observe(this) {
                    DataObject.checkCircleMenus(mainAppViewModel = mainAppViewModel, context = this)
                    DataObject.setUserImages(mainAppViewModel = mainAppViewModel, context = this)
                    allCircleMenu = it
                }

                allCircleMenu?.let { circleMenus ->
                    if (circleMenus.find { it.id == 0 } == null) {
                        FirstScreenUI(
                            mainAppViewModel = mainAppViewModel
                        )
                    } else {
                        LauncherScreen(mainAppViewModel = mainAppViewModel)
                    }
                }
            }
        }
        if (!isMyLauncherDefault()) {
            showLauncherSelection()
        }
    }

    override fun onResume() {
        super.onResume()
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver()
        } catch (_: Exception) {}

    }
    private fun registerReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }

        val receiver = AppsReceiver()
        registerReceiver(receiver, filter)
    }
    private fun unregisterReceiver() {
        val receiver = AppsReceiver()
        unregisterReceiver(receiver)
    }
    private fun isMyLauncherDefault(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
                    roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        } else {
            val packageManager = packageManager
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
            }
            val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            resolveInfo?.activityInfo?.packageName == packageName
        }
    }
    private fun showLauncherSelection() {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
}