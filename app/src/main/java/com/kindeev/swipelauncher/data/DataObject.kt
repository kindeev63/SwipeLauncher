package com.kindeev.swipelauncher.data

import android.app.role.RoleManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import com.google.gson.Gson
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.dataBaseElements.MenuActions
import com.kindeev.swipelauncher.data.dataBaseElements.MenuImages
import com.kindeev.swipelauncher.data.settings.ApplicationSetting
import com.kindeev.swipelauncher.data.settings.settingTypes.ClickableClock
import com.kindeev.swipelauncher.data.settings.SettingData
import com.kindeev.swipelauncher.data.settings.settingTypes.OpenLastApp
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
import com.kindeev.swipelauncher.presentation.receivers.AppsReceiver
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

    object SettingDataObject {
        private val settingsNames = mapOf(
            Pair(ApplicationSetting.OpenAllCircleMenus, R.string.all_circle_menus),
            Pair(ApplicationSetting.OpenLastApp, R.string.open_last_app),
            Pair(ApplicationSetting.ClickableClock, R.string.clickable_clock),
        )
        val defaultSettings = listOf(
            SettingData(ApplicationSetting.OpenLastApp, OpenLastApp(true)),
            SettingData(ApplicationSetting.OpenAllCircleMenus),
            SettingData(ApplicationSetting.ClickableClock, ClickableClock(false))
        )

        fun getSettingNameId(applicationSetting: ApplicationSetting) =
            settingsNames[applicationSetting] ?: 0

        fun openLastAppSettingValue(allSettings: List<SettingData>): Boolean {
            val setting = allSettings.find { it.setting == ApplicationSetting.OpenLastApp }
                ?: return true
            return (setting.getObjectData().getAs(OpenLastApp::class.java)).value
        }

        fun clickableClockSettingValue(allSettings: List<SettingData>): ClickableClock {
            val setting = allSettings.find { it.setting == ApplicationSetting.ClickableClock }
                ?: return ClickableClock(enabled = false)
            return setting.getObjectData().getAs(ClickableClock::class.java)
        }

        fun setDefaultSettings(mainAppViewModel: MainAppViewModel) {
            mainAppViewModel.insertSettings(defaultSettings)
        }
    }
    fun isAppInstalled(packageName: String, context: Context): Boolean {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
            packageInfo.packageName == packageName
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
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
                val userImage = menuImages.upImage.data.getAs(UserImage::class.java)
                if ("${userImage.id}.png" in allFileNames) {
                    userImages[userImage.id] =
                        BitmapFactory.decodeFile(File(context.filesDir, "${userImage.id}.png").path)
                            .asImageBitmap()
                    fileNames.add("${userImage.id}.png")
                }
            }
            if (menuImages.downImage.type == CircleMenuImageTypes.UserImage) {
                val userImage = menuImages.downImage.data.getAs(UserImage::class.java)
                if ("${userImage.id}.png" in allFileNames) {
                    userImages[userImage.id] =
                        BitmapFactory.decodeFile(File(context.filesDir, "${userImage.id}.png").path)
                            .asImageBitmap()
                    fileNames.add("${userImage.id}.png")
                }
            }
            if (menuImages.rightImage.type == CircleMenuImageTypes.UserImage) {
                val userImage = menuImages.rightImage.data.getAs(UserImage::class.java)
                if ("${userImage.id}.png" in allFileNames) {
                    userImages[userImage.id] =
                        BitmapFactory.decodeFile(File(context.filesDir, "${userImage.id}.png").path)
                            .asImageBitmap()
                    fileNames.add("${userImage.id}.png")
                }
            }
            if (menuImages.leftImage.type == CircleMenuImageTypes.UserImage) {
                val userImage = menuImages.leftImage.data.getAs(UserImage::class.java)
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
    fun checkCircleMenuReturn(
        allCircleMenus: List<CircleMenu>,
        context: Context
    ): List<CircleMenu> {
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
        return changedCircleMenus
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
            val openApp = circleMenu.menuActions.upAction.data.getAs(OpenApp::class.java)
            if (openApp.packageName !in allPackageNames) {
                menuActions = menuActions.copy(upAction = defaultAction)
                changed = true
            }
        }
        if (circleMenu.menuActions.downAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.downAction.data.getAs(OpenApp::class.java)
            if (openApp.packageName !in allPackageNames) {
                menuActions = menuActions.copy(downAction = defaultAction)
                changed = true
            }
        }
        if (circleMenu.menuActions.rightAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.rightAction.data.getAs(OpenApp::class.java)
            if (openApp.packageName !in allPackageNames) {
                menuActions = menuActions.copy(rightAction = defaultAction)
                changed = true
            }
        }
        if (circleMenu.menuActions.leftAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.leftAction.data.getAs(OpenApp::class.java)
            if (openApp.packageName !in allPackageNames) {
                menuActions = menuActions.copy(leftAction = defaultAction)
                changed = true
            }
        }

        // Check OpenCircleMenu
        if (circleMenu.menuActions.upAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu =
                circleMenu.menuActions.upAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id !in allCircleMenuId) {
                menuActions = menuActions.copy(upAction = defaultAction)
                changed = true
            }
        }
        if (circleMenu.menuActions.downAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu =
                circleMenu.menuActions.downAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id !in allCircleMenuId) {
                menuActions = menuActions.copy(downAction = defaultAction)
                changed = true
            }
        }
        if (circleMenu.menuActions.rightAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu =
                circleMenu.menuActions.rightAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id !in allCircleMenuId) {
                menuActions = menuActions.copy(rightAction = defaultAction)
                changed = true
            }
        }
        if (circleMenu.menuActions.leftAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu =
                circleMenu.menuActions.leftAction.data.getAs(OpenCircleMenu::class.java)
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
            val appImage = circleMenu.menuImages.upImage.data.getAs(AppImage::class.java)
            if (appImage.packageName !in allPackageNames) {
                menuImages = menuImages.copy(upImage = defaultImage)
                changed = true
            }
        }
        if (circleMenu.menuImages.downImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.downImage.data.getAs(AppImage::class.java)
            if (appImage.packageName !in allPackageNames) {
                menuImages = menuImages.copy(downImage = defaultImage)
                changed = true
            }
        }
        if (circleMenu.menuImages.rightImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.rightImage.data.getAs(AppImage::class.java)
            if (appImage.packageName !in allPackageNames) {
                menuImages = menuImages.copy(rightImage = defaultImage)
                changed = true
            }
        }
        if (circleMenu.menuImages.leftImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.leftImage.data.getAs(AppImage::class.java)
            if (appImage.packageName !in allPackageNames) {
                menuImages = menuImages.copy(leftImage = defaultImage)
                changed = true
            }
        }

        // Check UserImages
        val fileNames = context.filesDir.listFiles()?.map { it.name } ?: emptyList()
        if (circleMenu.menuImages.upImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.upImage.data.getAs(UserImage::class.java)
            if ("${userImage.id}.png" !in fileNames) {
                menuImages = menuImages.copy(upImage = defaultImage)
                changed = true
            }
        }
        if (circleMenu.menuImages.downImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.downImage.data.getAs(UserImage::class.java)
            if ("${userImage.id}.png" !in fileNames) {
                menuImages = menuImages.copy(downImage = defaultImage)
                changed = true
            }
        }
        if (circleMenu.menuImages.rightImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.rightImage.data.getAs(UserImage::class.java)
            if ("${userImage.id}.png" !in fileNames) {
                menuImages = menuImages.copy(rightImage = defaultImage)
                changed = true
            }
        }
        if (circleMenu.menuImages.leftImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.leftImage.data.getAs(UserImage::class.java)
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
            val openApp = circleMenu.menuActions.upAction.data.getAs(OpenApp::class.java)
            if (!isAppInstalled(openApp.packageName, context)) return false
        }
        if (circleMenu.menuActions.downAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.downAction.data.getAs(OpenApp::class.java)
            if (!isAppInstalled(openApp.packageName, context)) return false
        }
        if (circleMenu.menuActions.rightAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.rightAction.data.getAs(OpenApp::class.java)
            if (!isAppInstalled(openApp.packageName, context)) return false
        }
        if (circleMenu.menuActions.leftAction.type == CircleMenuActionTypes.OpenApp) {
            val openApp = circleMenu.menuActions.leftAction.data.getAs(OpenApp::class.java)
            if (!isAppInstalled(openApp.packageName, context)) return false
        }

        // Check OpenCircleMenu
        val allCircleMenuIds = mainAppViewModel.allCircleMenu.value?.map { it.id } ?: emptyList()
        if (circleMenu.menuActions.upAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu =
                circleMenu.menuActions.upAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id !in allCircleMenuIds) return false
        }
        if (circleMenu.menuActions.downAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu =
                circleMenu.menuActions.downAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id !in allCircleMenuIds) return false
        }
        if (circleMenu.menuActions.rightAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu =
                circleMenu.menuActions.rightAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id !in allCircleMenuIds) return false
        }
        if (circleMenu.menuActions.leftAction.type == CircleMenuActionTypes.OpenCircleMenu) {
            val openCircleMenu =
                circleMenu.menuActions.leftAction.data.getAs(OpenCircleMenu::class.java)
            if (openCircleMenu.id !in allCircleMenuIds) return false
        }


        // Check Images

        // Check AppImages
        if (circleMenu.menuImages.upImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.upImage.data.getAs(AppImage::class.java)
            if (!isAppInstalled(appImage.packageName, context)) return false
        }
        if (circleMenu.menuImages.downImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.downImage.data.getAs(AppImage::class.java)
            if (!isAppInstalled(appImage.packageName, context)) return false
        }
        if (circleMenu.menuImages.rightImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.rightImage.data.getAs(AppImage::class.java)
            if (!isAppInstalled(appImage.packageName, context)) return false
        }
        if (circleMenu.menuImages.leftImage.type == CircleMenuImageTypes.AppImage) {
            val appImage = circleMenu.menuImages.leftImage.data.getAs(AppImage::class.java)
            if (!isAppInstalled(appImage.packageName, context)) return false
        }

        // Check UserImages
        val fileNames = context.filesDir.listFiles()?.map { it.name } ?: emptyList()
        if (circleMenu.menuImages.upImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.upImage.data.getAs(UserImage::class.java)
            if ("${userImage.id}.png" !in fileNames) return false
        }
        if (circleMenu.menuImages.downImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.downImage.data.getAs(UserImage::class.java)
            if ("${userImage.id}.png" !in fileNames) return false
        }
        if (circleMenu.menuImages.rightImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.rightImage.data.getAs(UserImage::class.java)
            if ("${userImage.id}.png" !in fileNames) return false
        }
        if (circleMenu.menuImages.leftImage.type == CircleMenuImageTypes.UserImage) {
            val userImage = circleMenu.menuImages.leftImage.data.getAs(UserImage::class.java)
            if ("${userImage.id}.png" !in fileNames) return false
        }
        return true
    }
    fun Context.setAllApplicationData() {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val allAppData = packageManager.queryIntentActivities(intent, 0)
            .map { it.activityInfo.applicationInfo }
            .map {
                ApplicationData(
                    name = it.loadLabel(packageManager).toString(),
                    icon = it.loadIcon(packageManager).toBitmap().asImageBitmap(),
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
        val image = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage.UpArrow
        )
        val menuImages = MenuImages(
            upImage = image,
            downImage = image,
            rightImage = image,
            leftImage = image
        )
        val action = CircleMenuAction(
            type = CircleMenuActionTypes.OpenCircleMenu,
            data = OpenCircleMenu(id = 0)
        )
        val menuActions = MenuActions(
            upAction = action,
            downAction = CircleMenuAction(type = CircleMenuActionTypes.OpenSettings),
            rightAction = action,
            leftAction = action
        )
        return CircleMenu(
            title = stringResource(id = R.string.root),
            menuImages = menuImages,
            menuActions = menuActions
        )
    }
    @Composable
    fun CircleMenuImage.getItemImage(): Painter? {
        return when (type) {

            CircleMenuImageTypes.DefaultImage -> {
                val defaultImage = data.getAs(DefaultImage::class.java)
                painterResource(id = defaultImages[defaultImage] ?: return null)
            }

            CircleMenuImageTypes.AppImage -> {
                val appImage = data.getAs(AppImage::class.java)
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
                val userImage = data.getAs(UserImage::class.java)
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
    fun createEmptyCircleMenu(id: Int, title: String = ""): CircleMenu {
        val image = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage.Error
        )
        val action = CircleMenuAction(
            type = CircleMenuActionTypes.OpenCircleMenu,
            data = OpenCircleMenu(id = 0)
        )
        return CircleMenu(
            id = id,
            title = title,
            menuImages = MenuImages(
                upImage = image.copy(data = DefaultImage.UpArrow),
                downImage = image.copy(data = DefaultImage.DownArrow),
                rightImage = image.copy(data = DefaultImage.RightArrow),
                leftImage = image.copy(data = DefaultImage.LeftArrow)
            ),
            menuActions = MenuActions(
                upAction = action,
                downAction = action,
                rightAction = action,
                leftAction = action
            )
        )
    }
    fun Any?.serializableSettingData(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
    fun String.deserializableSettingData(setting: ApplicationSetting): Any? {
        val gson = Gson()
        val classOfData = getClassOfSettingData(setting)
        classOfData?.let {
            return gson.fromJson(this, it)
        }
        return null
    }
    private fun getClassOfSettingData(setting: ApplicationSetting): Class<*>? {
        return when (setting) {
            ApplicationSetting.OpenAllCircleMenus -> null
            ApplicationSetting.OpenLastApp -> OpenLastApp::class.java
            ApplicationSetting.ClickableClock -> ClickableClock::class.java
        }
    }
    fun <T> Any?.getAs(classOfT: Class<T>): T {
        val gson = Gson()
        return gson.fromJson(gson.toJson(this), classOfT)
    }
    fun isMyLauncherDefault(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
            roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
                    roleManager.isRoleHeld(RoleManager.ROLE_HOME)
        } else {
            val packageManager = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
            }
            val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            resolveInfo?.activityInfo?.packageName == context.packageName
        }
    }
    fun showLauncherSelection(context: Context) {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
    fun Context.registerAppsReceiver(receiver: BroadcastReceiver) {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        this.registerReceiver(receiver, filter)
    }
    fun Context.unregisterAppsReceiver(receiver: BroadcastReceiver) {
        try {
            this.unregisterReceiver(receiver)
        } catch (_: Exception) {}
    }
}