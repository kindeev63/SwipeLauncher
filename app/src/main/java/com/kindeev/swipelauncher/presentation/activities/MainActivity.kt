package com.kindeev.swipelauncher.presentation.activities

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.data.ApplicationData
import com.kindeev.swipelauncher.data.ChangedCircleMenu
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.data.ui.theme.SwipeLauncherTheme
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
        setAllApplicationData()
        setContent {
            SwipeLauncherTheme {
                enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
                var allCircleMenu by remember {
                    mutableStateOf<List<CircleMenu>?>(null)
                }
                mainAppViewModel.allCircleMenu.observe(this) {
                    checkCircleMenus(mainAppViewModel)
                    setUserImages(mainAppViewModel)
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
                val changedCircleMenu = checkCircleMenu(
                    circleMenu = circleMenu,
                    allPackageNames = allPackageNames,
                    allCircleMenuId = allCircleMenus.map { it.id }
                )
                if (changedCircleMenu.changed) changedCircleMenus.add(changedCircleMenu.circleMenu)
            }
            mainAppViewModel.insertCircleMenus(changedCircleMenus)
        }
    }

    private fun checkCircleMenu(
        circleMenu: CircleMenu,
        allPackageNames: List<String>,
        allCircleMenuId: List<Int>,
    ): ChangedCircleMenu {
        var changed = false

        // Check Actions
        var menuActions = circleMenu.menuActions
        val defaultAction = CircleMenuAction(
            type = CircleMenuActionTypes.OpenCircleMenu,
            data = OpenCircleMenu(id = 0)
        )

        // Check OpenApp
        if (circleMenu.menuActions.upAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.upAction.data as OpenApp
            if (openApp.packageName !in allPackageNames) {
                menuActions = menuActions.copy(upAction = defaultAction)
                changed = true
            }
        }
        if (circleMenu.menuActions.downAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.downAction.data as OpenApp
            if (openApp.packageName !in allPackageNames) {
                menuActions = menuActions.copy(downAction = defaultAction)
                changed = true
            }
        }
        if (circleMenu.menuActions.rightAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.rightAction.data as OpenApp
            if (openApp.packageName !in allPackageNames) {
                menuActions = menuActions.copy(rightAction = defaultAction)
                changed = true
            }
        }
        if (circleMenu.menuActions.leftAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.leftAction.data as OpenApp
            if (openApp.packageName !in allPackageNames) {
                menuActions = menuActions.copy(leftAction = defaultAction)
                changed = true
            }
        }

        // Check OpenCircleMenu
        if (circleMenu.menuActions.upAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = circleMenu.menuActions.upAction.data as OpenCircleMenu
            if (openCircleMenu.id !in allCircleMenuId) {
                menuActions = menuActions.copy(upAction = defaultAction)
                changed = true
            }
        }
        if (circleMenu.menuActions.downAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = circleMenu.menuActions.downAction.data as OpenCircleMenu
            if (openCircleMenu.id !in allCircleMenuId) {
                menuActions = menuActions.copy(downAction = defaultAction)
                changed = true
            }
        }
        if (circleMenu.menuActions.rightAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = circleMenu.menuActions.rightAction.data as OpenCircleMenu
            if (openCircleMenu.id !in allCircleMenuId) {
                menuActions = menuActions.copy(rightAction = defaultAction)
                changed = true
            }
        }
        if (circleMenu.menuActions.leftAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = circleMenu.menuActions.leftAction.data as OpenCircleMenu
            if (openCircleMenu.id !in allCircleMenuId) {
                menuActions = menuActions.copy(leftAction = defaultAction)
                changed = true
            }
        }


        // Check Images
        var menuImages = circleMenu.menuImages
        val defaultImage = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage.Error
        )

        // Check AppImages
        if (circleMenu.menuImages.upImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.upImage.data as AppImage
            if (appImage.packageName !in allPackageNames) {
                menuImages = menuImages.copy(upImage = defaultImage)
                changed = true
            }
        }
        if (circleMenu.menuImages.downImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.downImage.data as AppImage
            if (appImage.packageName !in allPackageNames) {
                menuImages = menuImages.copy(downImage = defaultImage)
                changed = true
            }
        }
        if (circleMenu.menuImages.rightImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.rightImage.data as AppImage
            if (appImage.packageName !in allPackageNames) {
                menuImages = menuImages.copy(rightImage = defaultImage)
                changed = true
            }
        }
        if (circleMenu.menuImages.leftImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.leftImage.data as AppImage
            if (appImage.packageName !in allPackageNames) {
                menuImages = menuImages.copy(leftImage = defaultImage)
                changed = true
            }
        }

        // Check UserImages
        val fileNames = filesDir.listFiles()?.map { it.name } ?: emptyList()
        if (circleMenu.menuImages.upImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.upImage.data as UserImage
            if ("${userImage.id}.png" !in fileNames) {
                menuImages = menuImages.copy(upImage = defaultImage)
                changed = true
            }
        }
        if (circleMenu.menuImages.downImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.downImage.data as UserImage
            if ("${userImage.id}.png" !in fileNames) {
                menuImages = menuImages.copy(downImage = defaultImage)
                changed = true
            }
        }
        if (circleMenu.menuImages.rightImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.rightImage.data as UserImage
            if ("${userImage.id}.png" !in fileNames) {
                menuImages = menuImages.copy(rightImage = defaultImage)
                changed = true
            }
        }
        if (circleMenu.menuImages.leftImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.leftImage.data as UserImage
            if ("${userImage.id}.png" !in fileNames) {
                menuImages = menuImages.copy(leftImage = defaultImage)
                changed = true
            }
        }

        return ChangedCircleMenu(
            circleMenu = circleMenu.copy(
                menuActions = menuActions,
                menuImages = menuImages
            ),
            changed = changed
        )
    }
}

