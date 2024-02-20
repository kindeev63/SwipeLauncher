package com.kindeev.swipelauncher.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.dataBaseElements.MenuActions
import com.kindeev.swipelauncher.data.dataBaseElements.MenuImages
import com.kindeev.swipelauncher.data.settings.SettingData
import com.kindeev.swipelauncher.data.settings.SettingTypes
import com.kindeev.swipelauncher.data.settings.SettingValue
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
import com.kindeev.swipelauncher.presentation.viewModels.MainAppViewModel
import java.io.File

object DataObject {
    private val _allApplicationData = MutableLiveData<List<ApplicationData>>(emptyList())
    val allApplicationData: LiveData<List<ApplicationData>> = _allApplicationData
    var userImages = emptyMap<Int, ImageBitmap>()
    val imageDialogTabs = listOf(
        ImageDialogTabs.AppImageTab,
        ImageDialogTabs.DefaultImageTab,
        ImageDialogTabs.UserImageTab
    )
    val actionDialogTabs = listOf(
        ActionDialogTabs.OpenAppTab,
        ActionDialogTabs.OpenCircleMenuTab,
        ActionDialogTabs.OtherTab
    )
    val defaultImages = mapOf(
        Pair(DefaultImage.Settings, R.drawable.ic_settings),
        Pair(DefaultImage.UpArrow, R.drawable.ic_up_arrow),
        Pair(DefaultImage.DownArrow, R.drawable.ic_down_arrow),
        Pair(DefaultImage.RightArrow, R.drawable.ic_right_arrow),
        Pair(DefaultImage.LeftArrow, R.drawable.ic_left_arrow),
        Pair(DefaultImage.Alarm, R.drawable.ic_alarm),
        Pair(DefaultImage.Time, R.drawable.ic_time),
        Pair(DefaultImage.Wallet, R.drawable.ic_wallet),
        Pair(DefaultImage.AccountBox, R.drawable.ic_account_box),
        Pair(DefaultImage.AccountCircle, R.drawable.ic_account_circle),
        Pair(DefaultImage.ObjectsTree, R.drawable.ic_objects_tree),
        Pair(DefaultImage.PopupNotification, R.drawable.ic_popup_notification),
        Pair(DefaultImage.MailA, R.drawable.ic_mail_a),
        Pair(DefaultImage.Apps, R.drawable.ic_apps),
        Pair(DefaultImage.Article, R.drawable.ic_article),
        Pair(DefaultImage.UncheckedTasks, R.drawable.ic_unchecked_tasks),
        Pair(DefaultImage.Brush, R.drawable.ic_brush),
        Pair(DefaultImage.Build, R.drawable.ic_build),
        Pair(DefaultImage.CheckedTasks, R.drawable.ic_checked_tasks),
        Pair(DefaultImage.Mail, R.drawable.ic_mail),
        Pair(DefaultImage.Extension, R.drawable.ic_extension),
        Pair(DefaultImage.Favourite, R.drawable.ic_favorite),
        Pair(DefaultImage.FlashLightOn, R.drawable.ic_flashlight_on),
        Pair(DefaultImage.FlashLightOff, R.drawable.ic_flashlight_off),
        Pair(DefaultImage.Error, R.drawable.ic_error),
    )
    private val settingsNames = mapOf(
        Pair(ApplicationSetting.OpenAllCircleMenus, R.string.all_circle_menus),
        Pair(ApplicationSetting.OpenLastApp, R.string.open_last_app),
    )
    val otherActionsList = listOf(
        OtherAction(
            type = CircleMenuActionTypes.OpenSettings,
            nameResourceId = R.string.open_settings,
            image = DefaultImage.Settings
        ),
        OtherAction(
            type = CircleMenuActionTypes.FlashLightOn,
            nameResourceId = R.string.flashlight_on,
            image = DefaultImage.FlashLightOn
        ),
        OtherAction(
            type = CircleMenuActionTypes.FlashLightOff,
            nameResourceId = R.string.flashlight_off,
            image = DefaultImage.FlashLightOff
        ),
        OtherAction(
            type = CircleMenuActionTypes.ChangeFlashLightCondition,
            nameResourceId = R.string.change_flashlight_condition,
            image = DefaultImage.FlashLightOn
        ),
    )

    fun openLastAppSettingValue(allSettings: List<SettingData>): Boolean {
        val setting = allSettings.find { it.setting == ApplicationSetting.OpenLastApp }
                ?: return true
        return setting.value.data as Boolean
    }

    fun getSettingNameId(applicationSetting: ApplicationSetting) =
        settingsNames[applicationSetting] ?: 0

    private fun isAppInstalled(packageName: String, context: Context): Boolean {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            packageInfo.packageName == packageName
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun executeClickableSetting(
        applicationSetting: ApplicationSetting,
        openAllCircleMenuScreen: () -> Unit
    ) {
        when (applicationSetting) {
            ApplicationSetting.OpenAllCircleMenus -> {
                openAllCircleMenuScreen()
            }

            else -> {}
        }
    }

    fun executeSwitchSetting(
        mainAppViewModel: MainAppViewModel,
        settingData: SettingData,
        data: Boolean
    ) {
        mainAppViewModel.insertSetting(settingData.copy(value = settingData.value.copy(data = data)))
    }

    fun setDefaultSettings(mainAppViewModel: MainAppViewModel) {
        val defaultSettings = listOf(
            SettingData(ApplicationSetting.OpenLastApp, SettingValue(SettingTypes.Switch, true)),
            SettingData(ApplicationSetting.OpenAllCircleMenus, SettingValue(SettingTypes.Clickable))
        )
        mainAppViewModel.insertSettings(defaultSettings)
    }

    fun openApp(packageName: String, context: Context) {
        if (isAppInstalled(packageName, context)) {
            val intent =
                context.packageManager.getLaunchIntentForPackage(packageName)
            intent?.let { context.startActivity(it) }
        }
    }

    fun deleteApp(packageName: String, context: Context) {
        val packageUri = Uri.parse("package:$packageName")
        val uninstallIntent = Intent(Intent.ACTION_DELETE, packageUri)
        context.startActivity(uninstallIntent)
    }

    fun setUserImages(
        mainAppViewModel: MainAppViewModel,
        context: Context
    ) {
        val userImages = mutableMapOf<Int, ImageBitmap>()
        val fileNames = mutableListOf<String>()
        val allFileNames = context.filesDir.listFiles()?.map { it.name } ?: emptyList()
        mainAppViewModel.allCircleMenu.value?.map { it.menuImages }?.forEach { menuImages ->
            if (menuImages.upImage.type == CircleMenuImageTypes.UserImage) {
                val userImage = menuImages.upImage.data as UserImage
                if ("${userImage.id}.png" in allFileNames) {
                    userImages[userImage.id] =
                        BitmapFactory.decodeFile(File(context.filesDir, "${userImage.id}.png").path)
                            .asImageBitmap()
                    fileNames.add("${userImage.id}.png")
                }
            }
            if (menuImages.downImage.type == CircleMenuImageTypes.UserImage) {
                val userImage = menuImages.downImage.data as UserImage
                if ("${userImage.id}.png" in allFileNames) {
                    userImages[userImage.id] =
                        BitmapFactory.decodeFile(File(context.filesDir, "${userImage.id}.png").path)
                            .asImageBitmap()
                    fileNames.add("${userImage.id}.png")
                }
            }
            if (menuImages.rightImage.type == CircleMenuImageTypes.UserImage) {
                val userImage = menuImages.rightImage.data as UserImage
                if ("${userImage.id}.png" in allFileNames) {
                    userImages[userImage.id] =
                        BitmapFactory.decodeFile(File(context.filesDir, "${userImage.id}.png").path)
                            .asImageBitmap()
                    fileNames.add("${userImage.id}.png")
                }
            }
            if (menuImages.leftImage.type == CircleMenuImageTypes.UserImage) {
                val userImage = menuImages.leftImage.data as UserImage
                if ("${userImage.id}.png" in allFileNames) {
                    userImages[userImage.id] =
                        BitmapFactory.decodeFile(File(context.filesDir, "${userImage.id}.png").path)
                            .asImageBitmap()
                    fileNames.add("${userImage.id}.png")
                }
            }
        }
        context.filesDir.listFiles()?.forEach { file ->
            if (file.name.contains(".png") && file.name !in fileNames) {
                file.delete()
            }
        }
        DataObject.userImages = userImages
    }

    fun checkCircleMenus(
        mainAppViewModel: MainAppViewModel,
        context: Context
    ) {
        mainAppViewModel.allCircleMenu.value?.let { allCircleMenus ->
            val changedCircleMenus = mutableListOf<CircleMenu>()
            val allPackageNames = allApplicationData.value?.map { it.packageName } ?: emptyList()
            allCircleMenus.forEach { circleMenu ->
                val changedCircleMenu = checkCircleMenu(
                    context = context,
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
        context: Context,
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
        val fileNames = context.filesDir.listFiles()?.map { it.name } ?: emptyList()
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

    fun checkCircleMenu(
        circleMenu: CircleMenu,
        context: Context,
        mainAppViewModel: MainAppViewModel
    ): Boolean {

        // Check Actions

        // Check OpenApp
        if (circleMenu.menuActions.upAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.upAction.data as OpenApp
            if (!isAppInstalled(openApp.packageName, context)) return false
        }
        if (circleMenu.menuActions.downAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.downAction.data as OpenApp
            if (!isAppInstalled(openApp.packageName, context)) return false
        }
        if (circleMenu.menuActions.rightAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.rightAction.data as OpenApp
            if (!isAppInstalled(openApp.packageName, context)) return false
        }
        if (circleMenu.menuActions.leftAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.leftAction.data as OpenApp
            if (!isAppInstalled(openApp.packageName, context)) return false
        }

        // Check OpenCircleMenu
        val allCircleMenuIds = mainAppViewModel.allCircleMenu.value?.map { it.id } ?: emptyList()
        if (circleMenu.menuActions.upAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = circleMenu.menuActions.upAction.data as OpenCircleMenu
            if (openCircleMenu.id !in allCircleMenuIds) return false
        }
        if (circleMenu.menuActions.downAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = circleMenu.menuActions.downAction.data as OpenCircleMenu
            if (openCircleMenu.id !in allCircleMenuIds) return false
        }
        if (circleMenu.menuActions.rightAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = circleMenu.menuActions.rightAction.data as OpenCircleMenu
            if (openCircleMenu.id !in allCircleMenuIds) return false
        }
        if (circleMenu.menuActions.leftAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu = circleMenu.menuActions.leftAction.data as OpenCircleMenu
            if (openCircleMenu.id !in allCircleMenuIds) return false
        }


        // Check Images

        // Check AppImages
        if (circleMenu.menuImages.upImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.upImage.data as AppImage
            if (!isAppInstalled(appImage.packageName, context)) return false
        }
        if (circleMenu.menuImages.downImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.downImage.data as AppImage
            if (!isAppInstalled(appImage.packageName, context)) return false
        }
        if (circleMenu.menuImages.rightImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.rightImage.data as AppImage
            if (!isAppInstalled(appImage.packageName, context)) return false
        }
        if (circleMenu.menuImages.leftImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.leftImage.data as AppImage
            if (!isAppInstalled(appImage.packageName, context)) return false
        }

        // Check UserImages
        val fileNames = context.filesDir.listFiles()?.map { it.name } ?: emptyList()
        if (circleMenu.menuImages.upImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.upImage.data as UserImage
            if ("${userImage.id}.png" !in fileNames) return false
        }
        if (circleMenu.menuImages.downImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.downImage.data as UserImage
            if ("${userImage.id}.png" !in fileNames) return false
        }
        if (circleMenu.menuImages.rightImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.rightImage.data as UserImage
            if ("${userImage.id}.png" !in fileNames) return false
        }
        if (circleMenu.menuImages.leftImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.leftImage.data as UserImage
            if ("${userImage.id}.png" !in fileNames) return false
        }
        return true
    }

    fun setAllApplicationData(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val allAppData = context.packageManager.queryIntentActivities(intent, 0)
            .map { it.activityInfo.applicationInfo }
            .map {
                ApplicationData(
                    name = it.loadLabel(context.packageManager).toString(),
                    icon = it.loadIcon(context.packageManager).toBitmap().asImageBitmap(),
                    packageName = it.packageName
                )
            }
        val mutableAllApplicationData = allAppData.toMutableList()
        allAppData.forEach { applicationData ->
            if (mutableAllApplicationData.count { it.packageName == applicationData.packageName } > 1) {
                mutableAllApplicationData.remove(applicationData)
            }
        }
        _allApplicationData.value = mutableAllApplicationData.sortedBy { it.name }
    }

    fun receiverGetNewApplicationData(context: Context): List<ApplicationData> {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val allAppData = context.packageManager.queryIntentActivities(intent, 0)
            .map { it.activityInfo.applicationInfo }
            .map {
                ApplicationData(
                    name = it.loadLabel(context.packageManager).toString(),
                    icon = it.loadIcon(context.packageManager).toBitmap().asImageBitmap(),
                    packageName = it.packageName
                )
            }
        val mutableAllApplicationData = allAppData.toMutableList()
        allAppData.forEach { applicationData ->
            if (mutableAllApplicationData.count { it.packageName == applicationData.packageName } > 1) {
                mutableAllApplicationData.remove(applicationData)
            }
        }
        return mutableAllApplicationData.sortedBy { it.name }
    }

    fun receiverSetAllApplicationData(newApplicationData: List<ApplicationData>) {
        _allApplicationData.value = newApplicationData
    }

    @Composable
    fun getRootCircleMenu(): CircleMenu {
        val menuImages = MenuImages(
            upImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.UpArrow
            ),
            downImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.Settings
            ),
            rightImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.RightArrow
            ),
            leftImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.LeftArrow
            )
        )
        val menuActions = MenuActions(
            upAction = CircleMenuAction(
                type = CircleMenuActionTypes.OpenCircleMenu,
                data = OpenCircleMenu(id = 0)
            ),
            downAction = CircleMenuAction(type = CircleMenuActionTypes.OpenSettings),
            rightAction = CircleMenuAction(
                type = CircleMenuActionTypes.OpenCircleMenu,
                data = OpenCircleMenu(id = 0)
            ),
            leftAction = CircleMenuAction(
                type = CircleMenuActionTypes.OpenCircleMenu,
                data = OpenCircleMenu(id = 0)
            )
        )
        return CircleMenu(
            title = stringResource(id = R.string.root),
            menuImages = menuImages,
            menuActions = menuActions
        )
    }

    @Composable
    fun getItemImage(
        circleMenuImage: CircleMenuImage
    ): Painter? {
        return when (circleMenuImage.type) {

            CircleMenuImageTypes.DefaultImage -> {
                val defaultImage = circleMenuImage.data as DefaultImage
                painterResource(id = defaultImages[defaultImage] ?: return null)
            }

            CircleMenuImageTypes.AppImage -> {
                val appImage = circleMenuImage.data as AppImage
                allApplicationData.value?.find { it.packageName == appImage.packageName }
                    ?.let { applicationData ->
                        val imageBitmap = applicationData.icon
                        remember(imageBitmap) {
                            BitmapPainter(
                                imageBitmap,
                                filterQuality = DrawScope.DefaultFilterQuality
                            )
                        }
                    }
                val context = LocalContext.current
                val applicationInfo =
                    context.packageManager.getApplicationInfo(appImage.packageName, 0)
                val imageBitmap =
                    applicationInfo.loadIcon(context.packageManager).toBitmap().asImageBitmap()
                return remember(imageBitmap) {
                    BitmapPainter(
                        imageBitmap,
                        filterQuality = DrawScope.DefaultFilterQuality
                    )
                }
            }

            CircleMenuImageTypes.UserImage -> {
                val userImage = circleMenuImage.data as UserImage
                val imageBitmap = userImages[userImage.id]
                imageBitmap?.let {
                    return remember(imageBitmap) {
                        BitmapPainter(
                            imageBitmap,
                            filterQuality = DrawScope.DefaultFilterQuality
                        )
                    }
                }
            }
        }
    }

    fun getItemsOffset(menuSize: Float, itemSize: Float) =
        listOf(
            // up
            Offset(
                x = menuSize / 2 - itemSize / 2,
                y = menuSize / 6 - itemSize / 2
            ),
            // down
            Offset(
                x = menuSize / 2 - itemSize / 2,
                y = menuSize / 6 * 5 - itemSize / 2
            ),
            // right
            Offset(
                x = menuSize / 6 * 5 - itemSize / 2,
                y = menuSize / 2 - itemSize / 2
            ),
            // left
            Offset(
                x = menuSize / 6 - itemSize / 2,
                y = menuSize / 2 - itemSize / 2
            )
        )

    fun createEmptyCircleMenu(id: Int, title: String = "") = CircleMenu(
        id = id,
        title = title,
        menuImages = MenuImages(
            upImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.UpArrow
            ),
            downImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.DownArrow
            ),
            rightImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.RightArrow
            ),
            leftImage = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.LeftArrow
            )
        ),
        menuActions = MenuActions(
            upAction = CircleMenuAction(
                type = CircleMenuActionTypes.OpenCircleMenu,
                data = OpenCircleMenu(id = 0)
            ),
            downAction = CircleMenuAction(
                type = CircleMenuActionTypes.OpenCircleMenu,
                data = OpenCircleMenu(id = 0)
            ),
            rightAction = CircleMenuAction(
                type = CircleMenuActionTypes.OpenCircleMenu,
                data = OpenCircleMenu(id = 0)
            ),
            leftAction = CircleMenuAction(
                type = CircleMenuActionTypes.OpenCircleMenu,
                data = OpenCircleMenu(id = 0)
            )
        )
    )
}