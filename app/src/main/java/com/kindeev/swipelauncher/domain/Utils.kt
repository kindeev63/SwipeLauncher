package com.kindeev.swipelauncher.domain

import android.Manifest
import android.app.role.RoleManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.gson.Gson
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.dataBase.MenuActions
import com.kindeev.swipelauncher.domain.dataBase.MenuImages
import com.kindeev.swipelauncher.domain.entities.ApplicationData
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenApp
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenCircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.entities.settings.ApplicationSetting
import com.kindeev.swipelauncher.domain.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.entities.settings.settingTypes.ClickableClock
import com.kindeev.swipelauncher.domain.entities.settings.settingTypes.OpenLastApp
import com.kindeev.swipelauncher.presentation.entities.ActionType
import com.kindeev.swipelauncher.presentation.entities.ActionTypes
import com.kindeev.swipelauncher.presentation.entities.FlashlightActionType
import com.kindeev.swipelauncher.presentation.entities.TelephoneActionType
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


fun <T> Any?.getAs(classOfT: Class<T>): T {
    val gson = Gson()
    return gson.fromJson(gson.toJson(this), classOfT)
}

fun emptyCircleMenu(id: Int): CircleMenu {
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
        title = "New",
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

fun Context.isMyLauncherDefault(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = this.getSystemService(Context.ROLE_SERVICE) as RoleManager
        roleManager.isRoleAvailable(RoleManager.ROLE_HOME) &&
                roleManager.isRoleHeld(RoleManager.ROLE_HOME)
    } else {
        val packageManager = this.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        resolveInfo?.activityInfo?.packageName == this.packageName
    }
}

fun Context.showLauncherSelection() {
    val intent = Intent(Settings.ACTION_HOME_SETTINGS)
    if (intent.resolveActivity(this.packageManager) != null) {
        this.startActivity(intent)
    }
}

fun CircleMenuImage.getItemImage(context: Context): ImageBitmap? {
    return when (type) {

        CircleMenuImageTypes.DefaultImage -> {
            val resourceId = Constants.defaultImages[data.getAs(
                DefaultImage::class.java
            )] ?: return null
            context.resources.getDrawable(resourceId, context.theme).toBitmap().asImageBitmap()

        }

        CircleMenuImageTypes.AppImage -> {
            val appImage = data.getAs(AppImage::class.java)
            val applicationData =
                LauncherData.allApplicationData.value?.find { it.packageName == appImage.packageName }
            if (applicationData == null) {
                val applicationInfo =
                    context.packageManager.getApplicationInfo(appImage.packageName, 0)
                applicationInfo.loadIcon(context.packageManager).toBitmap().asImageBitmap()
            } else {
                applicationData.icon
            }
        }

        CircleMenuImageTypes.UserImage -> {
            val userImage = data.getAs(UserImage::class.java)
            LauncherData.userImages[userImage.id]
        }
    }
}

fun getRootCircleMenu(title: String): CircleMenu {
    val image = CircleMenuImage(
        type = CircleMenuImageTypes.DefaultImage,
        data = DefaultImage.Error
    )
    val menuImages = MenuImages(
        upImage = image.copy(data = DefaultImage.UpArrow),
        downImage = image.copy(data = DefaultImage.Settings),
        rightImage = image.copy(data = DefaultImage.RightArrow),
        leftImage = image.copy(data = DefaultImage.LeftArrow)
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
        title = title,
        menuImages = menuImages,
        menuActions = menuActions
    )
}

fun CircleMenu.check(
    allPackageNames: List<String>,
    allCircleMenuIds: List<Int>,
    userImageIds: List<Int>
): CircleMenu? {
    var changed = false

    // Check Actions
    var menuActions = this.menuActions
    val defaultAction = CircleMenuAction(
        type = CircleMenuActionTypes.OpenCircleMenu,
        data = OpenCircleMenu(id = 0)
    )

    // Check OpenApp
    if (this.menuActions.upAction.type == CircleMenuActionTypes.OpenApp) {
        if (this.menuActions.upAction.data.getAs(OpenApp::class.java).packageName !in allPackageNames) {
            menuActions = menuActions.copy(upAction = defaultAction)
            changed = true
        }
    }
    if (this.menuActions.downAction.type == CircleMenuActionTypes.OpenApp) {
        if (this.menuActions.downAction.data.getAs(OpenApp::class.java).packageName !in allPackageNames) {
            menuActions = menuActions.copy(downAction = defaultAction)
            changed = true
        }
    }
    if (this.menuActions.rightAction.type == CircleMenuActionTypes.OpenApp) {
        if (this.menuActions.rightAction.data.getAs(OpenApp::class.java).packageName !in allPackageNames) {
            menuActions = menuActions.copy(rightAction = defaultAction)
            changed = true
        }
    }
    if (this.menuActions.leftAction.type == CircleMenuActionTypes.OpenApp) {
        if (this.menuActions.leftAction.data.getAs(OpenApp::class.java).packageName !in allPackageNames) {
            menuActions = menuActions.copy(leftAction = defaultAction)
            changed = true
        }
    }

    // Check OpenCircleMenu
    if (this.menuActions.upAction.type == CircleMenuActionTypes.OpenCircleMenu) {
        if (this.menuActions.upAction.data.getAs(OpenCircleMenu::class.java).id !in allCircleMenuIds) {
            menuActions = menuActions.copy(upAction = defaultAction)
            changed = true
        }
    }
    if (this.menuActions.downAction.type == CircleMenuActionTypes.OpenCircleMenu) {
        if (this.menuActions.downAction.data.getAs(OpenCircleMenu::class.java).id !in allCircleMenuIds) {
            menuActions = menuActions.copy(downAction = defaultAction)
            changed = true
        }
    }
    if (this.menuActions.rightAction.type == CircleMenuActionTypes.OpenCircleMenu) {
        if (this.menuActions.rightAction.data.getAs(OpenCircleMenu::class.java).id !in allCircleMenuIds) {
            menuActions = menuActions.copy(rightAction = defaultAction)
            changed = true
        }
    }
    if (this.menuActions.leftAction.type == CircleMenuActionTypes.OpenCircleMenu) {
        if (this.menuActions.leftAction.data.getAs(OpenCircleMenu::class.java).id !in allCircleMenuIds) {
            menuActions = menuActions.copy(leftAction = defaultAction)
            changed = true
        }
    }


    // Check Images
    var menuImages = this.menuImages
    val defaultImage = CircleMenuImage(
        type = CircleMenuImageTypes.DefaultImage,
        data = DefaultImage.Error
    )

    // Check AppImages
    if (this.menuImages.upImage.type == CircleMenuImageTypes.AppImage) {
        if (this.menuImages.upImage.data.getAs(AppImage::class.java).packageName !in allPackageNames) {
            menuImages = menuImages.copy(upImage = defaultImage)
            changed = true
        }
    }
    if (this.menuImages.downImage.type == CircleMenuImageTypes.AppImage) {
        if (this.menuImages.downImage.data.getAs(AppImage::class.java).packageName !in allPackageNames) {
            menuImages = menuImages.copy(downImage = defaultImage)
            changed = true
        }
    }
    if (this.menuImages.rightImage.type == CircleMenuImageTypes.AppImage) {
        if (this.menuImages.rightImage.data.getAs(AppImage::class.java).packageName !in allPackageNames) {
            menuImages = menuImages.copy(rightImage = defaultImage)
            changed = true
        }
    }
    if (this.menuImages.leftImage.type == CircleMenuImageTypes.AppImage) {
        if (this.menuImages.leftImage.data.getAs(AppImage::class.java).packageName !in allPackageNames) {
            menuImages = menuImages.copy(leftImage = defaultImage)
            changed = true
        }
    }

    // Check UserImages
    if (this.menuImages.upImage.type == CircleMenuImageTypes.UserImage) {
        if (this.menuImages.upImage.data.getAs(UserImage::class.java).id !in userImageIds) {
            menuImages = menuImages.copy(upImage = defaultImage)
            changed = true
        }
    }
    if (this.menuImages.downImage.type == CircleMenuImageTypes.UserImage) {
        if (this.menuImages.downImage.data.getAs(UserImage::class.java).id !in userImageIds) {
            menuImages = menuImages.copy(downImage = defaultImage)
            changed = true
        }
    }
    if (this.menuImages.rightImage.type == CircleMenuImageTypes.UserImage) {
        if (this.menuImages.rightImage.data.getAs(UserImage::class.java).id !in userImageIds) {
            menuImages = menuImages.copy(rightImage = defaultImage)
            changed = true
        }
    }
    if (this.menuImages.leftImage.type == CircleMenuImageTypes.UserImage) {
        if (this.menuImages.leftImage.data.getAs(UserImage::class.java).id !in userImageIds) {
            menuImages = menuImages.copy(leftImage = defaultImage)
            changed = true
        }
    }

    return if (changed) {
        this.copy(
            menuActions = menuActions,
            menuImages = menuImages
        )
    } else null
}

fun List<CircleMenu>.getOnlyChanged(
    allPackageNames: List<String>,
    allCircleMenuIds: List<Int>,
    userImageIds: List<Int>
): List<CircleMenu> {
    val changedCircleMenus = mutableListOf<CircleMenu>()
    this.forEach { circleMenu ->
        circleMenu.check(
            allPackageNames = allPackageNames,
            allCircleMenuIds = allCircleMenuIds,
            userImageIds = userImageIds
        )?.let { changedCircleMenus.add(it) }
    }
    return changedCircleMenus
}

fun Context.getAllApplicationData(): List<ApplicationData> {
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
    return mutableAllApplicationData.sortedBy { it.name }
}

fun List<CircleMenu>.getOnlyChanged(
    context: Context
) = this.getOnlyChanged(
    allCircleMenuIds = LauncherData.allCircleMenus.value?.map { it.id } ?: emptyList(),
    allPackageNames = context.getAllApplicationData().map { it.packageName },
    userImageIds = context.getUserImageIds()
)

fun Context.getUserImageIds(): List<Int> {
    val ids = mutableListOf<Int>()
    this.filesDir.listFiles()?.forEach { file ->
        file.name.split(".")[0].toIntOrNull()?.let {
            ids.add(it)
        }
    }
    return ids
}

fun Context.getUserImages(): Map<Int, ImageBitmap> {
    val newUserImages = mutableMapOf<Int, ImageBitmap>()
    this.filesDir.listFiles()?.map { file ->
        file.name.split(".")[0].toIntOrNull()?.let { key ->
            newUserImages[key] = BitmapFactory.decodeFile(file.path).asImageBitmap()
        }
    } ?: return emptyMap()
    return newUserImages
}

fun Context.removeUnusedUserImages(
    allCircleMenus: List<CircleMenu>
) {
    val allUserImageNamesInCircleMenus = allCircleMenus.getUserImageNames()
    this.filesDir.listFiles()?.forEach { file ->
        if (file.name.contains(".png") && file.name !in allUserImageNamesInCircleMenus) {
            file.delete()
        }
    }
}

private fun List<CircleMenu>.getUserImageNames(): List<String> {
    val names = mutableListOf<String>()
    this.map { it.menuImages }.forEach { menuImages ->
        if (menuImages.upImage.type == CircleMenuImageTypes.UserImage) {
            names.add("${menuImages.upImage.data.getAs(UserImage::class.java).id}.png")
        }
        if (menuImages.downImage.type == CircleMenuImageTypes.UserImage) {
            names.add("${menuImages.downImage.data.getAs(UserImage::class.java).id}.png")
        }
        if (menuImages.rightImage.type == CircleMenuImageTypes.UserImage) {
            names.add("${menuImages.rightImage.data.getAs(UserImage::class.java).id}.png")
        }
        if (menuImages.leftImage.type == CircleMenuImageTypes.UserImage) {
            names.add("${menuImages.leftImage.data.getAs(UserImage::class.java).id}.png")
        }
    }
    return names
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
    } catch (_: Exception) {
    }
}

fun Context.isAppInstalled(packageName: String): Boolean {
    return try {
        val packageInfo = this.packageManager.getPackageInfo(packageName, 0)
        packageInfo.packageName == packageName
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

private fun ApplicationSetting.getClassOfSettingData(): Class<*>? {
    return when (this) {
        ApplicationSetting.OpenAllCircleMenus -> null
        ApplicationSetting.OpenLastApp -> OpenLastApp::class.java
        ApplicationSetting.ClickableClock -> ClickableClock::class.java
        ApplicationSetting.ChangeDefaultLauncher -> null
    }
}

fun String.deserializableSettingData(setting: ApplicationSetting): Any? {
    val gson = Gson()
    val classOfData = setting.getClassOfSettingData()
    classOfData?.let {
        return gson.fromJson(this, it)
    }
    return null
}

fun Any?.serializableSettingData(): String {
    val gson = Gson()
    return gson.toJson(this)
}

fun List<SettingData>.clickableClockSettingValue(): ClickableClock {
    val setting =
        this.find { it.setting == ApplicationSetting.ClickableClock } ?: return ClickableClock(
            enabled = false
        )
    return setting.getObjectData().getAs(ClickableClock::class.java)
}

fun List<SettingData>.openLastAppSettingValue(): Boolean {
    val setting = this.find { it.setting == ApplicationSetting.OpenLastApp } ?: return true
    return (setting.getObjectData().getAs(OpenLastApp::class.java)).value
}

fun Context.getAppDetails(packageName: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

fun Context.addUserImage(id: Int, bitmap: Bitmap) {
    val file = File(filesDir, "$id.png")
    file.createNewFile()
    val fos = FileOutputStream(file)
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
    fos.write(bos.toByteArray())
    fos.flush()
    fos.close()
}

fun Context.createBitmap(uri: Uri): Bitmap = if (Build.VERSION.SDK_INT < 28) {
    MediaStore.Images
        .Media.getBitmap(contentResolver, uri)

} else {
    ImageDecoder.decodeBitmap(
        ImageDecoder
            .createSource(contentResolver, uri)
    )
}

fun Context.setActionTypes() {
    Constants.actionTypes = listOf(
        ActionType(
            name = this.resources.getString(R.string.open_app_action),
            imageResId = R.drawable.open_app_image,
            type = ActionTypes.OpenApp
        ),
        ActionType(
            name = this.resources.getString(R.string.open_circle_menu_action),
            imageResId = R.drawable.open_circle_menu_image,
            type = ActionTypes.OpenCircleMenu
        ),
        ActionType(
            name = this.resources.getString(R.string.telephone_action),
            imageResId = R.drawable.telephone_image,
            type = ActionTypes.Telephone
        ),
        ActionType(
            name = this.resources.getString(R.string.flashlight_action),
            imageResId = R.drawable.flashlight_action,
            type = ActionTypes.Flashlight
        ),
        ActionType(
            name = this.resources.getString(R.string.open_settings_action),
            imageResId = R.drawable.open_settings_image,
            type = ActionTypes.OpenSettings
        ),
    )
    Constants.flashlightActionTypes = listOf(
        FlashlightActionType(
            name = this.resources.getString(R.string.on_flashlight_action),
            imageResId = R.drawable.on_flashlight_image,
            type = CircleMenuActionTypes.FlashLightOn
        ),
        FlashlightActionType(
            name = this.resources.getString(R.string.off_flashlight_action),
            imageResId = R.drawable.off_flashlight_image,
            type = CircleMenuActionTypes.FlashLightOff
        ),
        FlashlightActionType(
            name = this.resources.getString(R.string.change_condition_flashlight_action),
            imageResId = R.drawable.change_condition_flashlight_image,
            type = CircleMenuActionTypes.ChangeFlashLightCondition
        ),
    )
    Constants.telephoneActionTypes = listOf(
        TelephoneActionType(
            name = this.resources.getString(R.string.call_telephone_action),
            imageResId = R.drawable.call_telephone_image,
            type = CircleMenuActionTypes.Call
        ),
        TelephoneActionType(
            name = this.resources.getString(R.string.dial_telephone_action),
            imageResId = R.drawable.dial_telephone_image,
            type = CircleMenuActionTypes.Dial
        ),
    )
}

fun CircleMenuActionTypes.getActionType(): ActionType? {
    return when (this) {
        CircleMenuActionTypes.OpenCircleMenu -> Constants.actionTypes.find { it.type == ActionTypes.OpenCircleMenu }
        CircleMenuActionTypes.OpenSettings -> Constants.actionTypes.find { it.type == ActionTypes.OpenSettings }
        CircleMenuActionTypes.OpenApp -> Constants.actionTypes.find { it.type == ActionTypes.OpenApp }
        CircleMenuActionTypes.FlashLightOn -> Constants.actionTypes.find { it.type == ActionTypes.Flashlight }
        CircleMenuActionTypes.FlashLightOff -> Constants.actionTypes.find { it.type == ActionTypes.Flashlight }
        CircleMenuActionTypes.ChangeFlashLightCondition -> Constants.actionTypes.find { it.type == ActionTypes.Flashlight }
        CircleMenuActionTypes.Call -> Constants.actionTypes.find { it.type == ActionTypes.Telephone }
        CircleMenuActionTypes.Dial -> Constants.actionTypes.find { it.type == ActionTypes.Telephone }
    }
}

fun Context.getApplicationData(packageName: String): ApplicationData {
    val applicationData =
        LauncherData.allApplicationData.value?.find { it.packageName == packageName }
    return if (applicationData == null) {
        val applicationInfo =
            packageManager.getApplicationInfo(packageName, 0)
        ApplicationData(
            name = applicationInfo.loadLabel(packageManager).toString(),
            icon = applicationInfo.loadIcon(packageManager).toBitmap().asImageBitmap(),
            packageName = applicationInfo.packageName
        )
    } else {
        applicationData
    }
}

fun String.formatPhoneNumber(): String {
    return if (this.length == 11) {
        "${this[0]} (${this.substring(1, 4)}) ${
            this.substring(4, 7)
        }-${this.substring(7, 9)}-${this.substring(9)}"
    } else this
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReadContactsPermission(
    result: (Boolean) -> Unit
) {
    val permissionState = rememberPermissionState(Manifest.permission.READ_CONTACTS)
    if (permissionState.status.isGranted) {
        result(true)
    } else {
        if (permissionState.status.shouldShowRationale) {
            result(false)
        } else {
            LaunchedEffect(permissionState) {
                permissionState.launchPermissionRequest()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallPermission(
    result: (Boolean) -> Unit
) {
    val permissionState = rememberPermissionState(Manifest.permission.CALL_PHONE)
    if (permissionState.status.isGranted) {
        result(true)
    } else {
        if (permissionState.status.shouldShowRationale) {
            result(false)
        } else {
            LaunchedEffect(permissionState) {
                permissionState.launchPermissionRequest()
            }
        }
    }
}

fun Context.getContactName(phoneNumber: String): String? {
    val uri = Uri.withAppendedPath(
        ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
        Uri.encode(phoneNumber)
    )
    val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
    val cursor = contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            return cursor.getString(0)
        }
    }
    return null
}

fun Context.getMinScreenLength(): Float {
    return minOf(resources.configuration.screenWidthDp, resources.configuration.screenHeightDp).toFloat()
}

@Composable
fun getMinScreenLengthDp(): Dp {
    val configuration = LocalConfiguration.current
    return minOf(configuration.screenWidthDp, configuration.screenHeightDp).dp
}

@Composable
fun getMinScreenLengthSp(): TextUnit {
    val configuration = LocalConfiguration.current
    return minOf(configuration.screenWidthDp, configuration.screenHeightDp).sp
}