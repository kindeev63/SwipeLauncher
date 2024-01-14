package com.kindeev.swipelauncher.presentation.activities

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.data.ApplicationData
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.NoneAction
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.NoneImage
import com.kindeev.swipelauncher.presentation.MainApp
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.screens.LauncherScreen
import com.kindeev.swipelauncher.presentation.uiElements.FirstScreenUI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainAppViewModel = (application as MainApp).mainAppViewModel
        makeStatusBarTransparent(this)
        setContent {
            var allCircleMenu by remember {
                mutableStateOf<List<CircleMenu>?>(null)
            }
            mainAppViewModel.allCircleMenu.observe(this) {
                checkCircleMenus(mainAppViewModel, this)
                allCircleMenu = it
            }
            allCircleMenu?.let { circleMenus ->
                if (circleMenus.isEmpty()) {
                    FirstScreenUI(
                        mainAppViewModel = mainAppViewModel
                    )
                } else {
                    LauncherScreen(mainAppViewModel = mainAppViewModel)
                }
            }

        }
    }

    override fun onResume() {
        setAllApplicationData(this)
        super.onResume()
    }

    private fun setAllApplicationData(context: Context) {
        DataObject.allApplicationData =
            context.packageManager.getInstalledApplications(PackageManager.MATCH_ALL).filter {
                (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0
            }.map {
                ApplicationData(
                    name = it.loadLabel(context.packageManager).toString(),
                    icon = it.loadIcon(context.packageManager).toBitmap().asImageBitmap(),
                    packageName = it.packageName
                )
            }
    }

    private fun makeStatusBarTransparent(activity: Activity) {
        val window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val option =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility = option
        window.statusBarColor = Color.Transparent.value.toInt()
    }

    private fun checkCircleMenus(
        mainAppViewModel: MainAppViewModel,
        context: Context
    ) {
        val allPackageNames =
            context.packageManager.getInstalledApplications(PackageManager.MATCH_ALL).filter {
                (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0
            }.map {
                it.packageName
            }
        mainAppViewModel.allCircleMenu.value?.let { allCircleMenus ->
            val changedCircleMenus = mutableListOf<CircleMenu>()
            allCircleMenus.forEach { circleMenu ->
                val newCircleMenu = checkCircleMenu(circleMenu, allPackageNames)
                if (newCircleMenu != circleMenu) changedCircleMenus.add(newCircleMenu)
            }
            mainAppViewModel.insertCircleMenus(changedCircleMenus)
        }
    }

    private fun checkCircleMenu(
        circleMenu: CircleMenu,
        allPackageNames: List<String>
    ): CircleMenu {
        val newCircleMenu = circleMenu.copy()

        // Check Actions
        val noneAction = CircleMenuAction(
            type = CircleMenuActionTypes.NoneAction,
            data = NoneAction
        )
        if (circleMenu.menuActions.upAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.upAction.data as OpenApp
            if (openApp.packageName !in allPackageNames) newCircleMenu.menuActions.upAction =
                noneAction
        }
        if (circleMenu.menuActions.downAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.downAction.data as OpenApp
            if (openApp.packageName !in allPackageNames) newCircleMenu.menuActions.downAction =
                noneAction
        }
        if (circleMenu.menuActions.rightAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.rightAction.data as OpenApp
            if (openApp.packageName !in allPackageNames) newCircleMenu.menuActions.rightAction =
                noneAction
        }
        if (circleMenu.menuActions.leftAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.leftAction.data as OpenApp
            if (openApp.packageName !in allPackageNames) newCircleMenu.menuActions.leftAction =
                noneAction
        }

        // Check Images
        val noneImage = CircleMenuImage(
            type = CircleMenuImageTypes.NoneImage,
            data = NoneImage
        )
        if (circleMenu.menuImages.upImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.upImage.data as AppImage
            if (appImage.packageName !in allPackageNames) newCircleMenu.menuImages.upImage =
                noneImage
        }
        if (circleMenu.menuImages.downImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.downImage.data as AppImage
            if (appImage.packageName !in allPackageNames) newCircleMenu.menuImages.downImage =
                noneImage
        }
        if (circleMenu.menuImages.rightImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.rightImage.data as AppImage
            if (appImage.packageName !in allPackageNames) newCircleMenu.menuImages.rightImage =
                noneImage
        }
        if (circleMenu.menuImages.leftImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.leftImage.data as AppImage
            if (appImage.packageName !in allPackageNames) newCircleMenu.menuImages.leftImage =
                noneImage
        }
        return newCircleMenu
    }
}

