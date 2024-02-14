package com.kindeev.swipelauncher.presentation.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.data.ApplicationData
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.domain.CircleMenu
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.domain.circleMenuActions.actionTypes.OpenCircleMenu
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.circleMenuImages.imageTypes.UserImage
import com.kindeev.swipelauncher.presentation.MainApp
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import com.kindeev.swipelauncher.presentation.screens.LauncherScreen
import com.kindeev.swipelauncher.presentation.uiElements.FirstScreenUI
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainAppViewModel = (application as MainApp).mainAppViewModel
        makeStatusBarTransparent(this)
        setAllApplicationData()
        setContent {
            var allCircleMenu by remember {
                mutableStateOf<List<CircleMenu>?>(null)
            }
            mainAppViewModel.allCircleMenu.observe(this) {
                checkCircleMenus(mainAppViewModel)
                setUserImages(mainAppViewModel)
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

    private fun setAllApplicationData() {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        DataObject.allApplicationData =
            packageManager.queryIntentActivities(intent, 0).map { it.activityInfo.applicationInfo }
                .map {
                    ApplicationData(
                        name = it.loadLabel(packageManager).toString(),
                        icon = it.loadIcon(packageManager).toBitmap().asImageBitmap(),
                        packageName = it.packageName
                    )
                }.toMutableList()
        val mutableAllApplicationData = DataObject.allApplicationData.toMutableList()
        DataObject.allApplicationData.forEach { applicationData ->
            if (mutableAllApplicationData.count { it.packageName == applicationData.packageName } > 1) {
                mutableAllApplicationData.remove(applicationData)
            }
        }
        DataObject.allApplicationData = mutableAllApplicationData.sortedBy { it.name }
    }

    private fun setUserImages(
        mainAppViewModel: MainAppViewModel
    ) {
        val userImages = mutableMapOf<Int, ImageBitmap>()
        val fileNames = mutableListOf<String>()
        val allFileNames = filesDir.listFiles()?.map { it.name } ?: emptyList()
        mainAppViewModel.allCircleMenu.value?.map { it.menuImages }?.forEach { menuImages ->
            if (menuImages.upImage.type == CircleMenuImageTypes.UserImage) {
                val userImage = menuImages.upImage.data as UserImage
                if ("${userImage.id}.png" in allFileNames) {
                    userImages[userImage.id] =
                        BitmapFactory.decodeFile(File(filesDir, "${userImage.id}.png").path)
                            .asImageBitmap()
                    fileNames.add("${userImage.id}.png")
                }
            }
            if (menuImages.downImage.type == CircleMenuImageTypes.UserImage) {
                val userImage = menuImages.downImage.data as UserImage
                if ("${userImage.id}.png" in allFileNames) {
                    userImages[userImage.id] =
                        BitmapFactory.decodeFile(File(filesDir, "${userImage.id}.png").path)
                            .asImageBitmap()
                    fileNames.add("${userImage.id}.png")
                }
            }
            if (menuImages.rightImage.type == CircleMenuImageTypes.UserImage) {
                val userImage = menuImages.rightImage.data as UserImage
                if ("${userImage.id}.png" in allFileNames) {
                    userImages[userImage.id] =
                        BitmapFactory.decodeFile(File(filesDir, "${userImage.id}.png").path)
                            .asImageBitmap()
                    fileNames.add("${userImage.id}.png")
                }
            }
            if (menuImages.leftImage.type == CircleMenuImageTypes.UserImage) {
                val userImage = menuImages.leftImage.data as UserImage
                if ("${userImage.id}.png" in allFileNames) {
                    userImages[userImage.id] =
                        BitmapFactory.decodeFile(File(filesDir, "${userImage.id}.png").path)
                            .asImageBitmap()
                    fileNames.add("${userImage.id}.png")
                }
            }
        }
        filesDir.listFiles()?.forEach { file ->
            if (file.name.contains(".png") && file.name !in fileNames) {
                file.delete()
            }
        }
        DataObject.userImages = userImages
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
    ) {
        val allPackageNames =
            packageManager.getInstalledApplications(PackageManager.MATCH_ALL).filter {
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
        val noneAction = CircleMenuAction(type = CircleMenuActionTypes.OpenCircleMenu, data = OpenCircleMenu(id = 0))
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

        // Check AppImages
        val noneImage = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage.Error
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

        // Check UserImages

        val fileNames = filesDir.listFiles()?.map { it.name } ?: emptyList()
        if (circleMenu.menuImages.upImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.upImage.data as UserImage
            if ("${userImage.id}.png" !in fileNames) newCircleMenu.menuImages.upImage = noneImage
        }
        if (circleMenu.menuImages.downImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.downImage.data as UserImage
            if ("${userImage.id}.png" !in fileNames) newCircleMenu.menuImages.downImage = noneImage
        }
        if (circleMenu.menuImages.rightImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.rightImage.data as UserImage
            if ("${userImage.id}.png" !in fileNames) newCircleMenu.menuImages.rightImage = noneImage
        }
        if (circleMenu.menuImages.leftImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.leftImage.data as UserImage
            if ("${userImage.id}.png" !in fileNames) newCircleMenu.menuImages.leftImage = noneImage
        }

        return newCircleMenu
    }
}

