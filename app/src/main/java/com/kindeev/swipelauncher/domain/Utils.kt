package com.kindeev.swipelauncher.domain

import android.Manifest
import android.annotation.SuppressLint
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
import android.provider.Telephony
import android.telecom.TelecomManager
import androidx.activity.SystemBarStyle
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import com.kindeev.swipelauncher.domain.entities.ApplicationData
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.entities.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenApp
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionData.OpenCircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.entities.settings.Setting
import com.kindeev.swipelauncher.domain.entities.settings.SettingData
import com.kindeev.swipelauncher.domain.entities.settings.settingTypes.ClickOnClock
import com.kindeev.swipelauncher.presentation.activities.SettingsActivity
import com.kindeev.swipelauncher.presentation.entities.ActionType
import com.kindeev.swipelauncher.presentation.entities.ActionTypes
import com.kindeev.swipelauncher.presentation.entities.FlashlightActionType
import com.kindeev.swipelauncher.presentation.entities.ImageType
import com.kindeev.swipelauncher.presentation.entities.TelephoneActionType
import com.kindeev.swipelauncher.presentation.entities.searchBox.AppSBR
import com.kindeev.swipelauncher.presentation.entities.searchBox.SearchBoxResult
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


fun <T> Any?.getAs(classOfT: Class<T>): T {
    val gson = Gson()
    return gson.fromJson(gson.toJson(this), classOfT)
}

fun emptyCircleMenu(id: Int): CircleMenu {
    return CircleMenu(
        id = id,
        title = "New",
        items = emptyList()
    )
}

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
            LauncherData.allApplicationInfo.value?.find { it.packageName == appImage.packageName }?.icon
                ?: context.packageManager.getApplicationInfo(appImage.packageName, 0)
                    .loadIcon(context.packageManager).toBitmap().asImageBitmap()
        }

        CircleMenuImageTypes.UserImage -> {
            val userImage = data.getAs(UserImage::class.java)
            LauncherData.userImages[userImage.id]
        }
    }
}

fun CircleMenuImage.getItemImageForApplicationInfoDialog(
    context: Context,
    packageName: String
): ImageBitmap? {
    return when (type) {

        CircleMenuImageTypes.DefaultImage -> {
            val resourceId = Constants.defaultImages[data.getAs(
                DefaultImage::class.java
            )] ?: return null
            context.resources.getDrawable(resourceId, context.theme).toBitmap().asImageBitmap()
        }

        CircleMenuImageTypes.AppImage -> {
            val appImage = data.getAs(AppImage::class.java)
            if (appImage.packageName == packageName) {
                val applicationInfo =
                    context.packageManager.getApplicationInfo(appImage.packageName, 0)
                applicationInfo.loadIcon(context.packageManager).toBitmap().asImageBitmap()
            } else {
                val applicationData = context.getApplicationData(appImage.packageName)
                if (applicationData.image.type == CircleMenuImageTypes.AppImage && applicationData.image.data.getAs(
                        AppImage::class.java
                    ).packageName == applicationData.packageName
                ) {
                    val applicationInfo =
                        context.packageManager.getApplicationInfo(appImage.packageName, 0)
                    applicationInfo.loadIcon(context.packageManager).toBitmap().asImageBitmap()
                } else {
                    applicationData.image.getItemImage(context)
                }
            }
        }

        CircleMenuImageTypes.UserImage -> {
            val userImage = data.getAs(UserImage::class.java)
            LauncherData.userImages[userImage.id]
        }
    }
}

fun CircleMenu.check(
    allPackageNames: List<String>,
    allCircleMenuIds: List<Int>,
    userImageIds: List<Int>
): CircleMenu? {
    val newItems = mutableListOf<CircleMenuItem>()

    for (item in this.items) {
        when (item.image.type) {
            CircleMenuImageTypes.AppImage -> {
                if (item.image.data.getAs(AppImage::class.java).packageName !in allPackageNames) {
                    continue
                }
            }

            CircleMenuImageTypes.UserImage -> {
                if (item.image.data.getAs(UserImage::class.java).id !in userImageIds) {
                    continue
                }
            }

            else -> {}
        }
        when (item.action.type) {
            CircleMenuActionTypes.OpenApp -> {
                if (item.action.data.getAs(OpenApp::class.java).packageName !in allPackageNames) {
                    continue
                }
            }

            CircleMenuActionTypes.OpenCircleMenu -> {
                if (item.action.data.getAs(OpenCircleMenu::class.java).id !in allCircleMenuIds) {
                    continue
                }
            }

            else -> {}
        }
        newItems.add(item)
    }

    return if (this.items == newItems) {
        null
    } else this.copy(items = newItems)
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

fun Context.getAllApplicationInfo(): List<ApplicationInfo> {
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    val allAppInfo = packageManager.queryIntentActivities(intent, 0)
        .map { it.activityInfo.applicationInfo }
        .map {
            ApplicationInfo(
                title = it.loadLabel(packageManager).toString(),
                icon = it.loadIcon(packageManager).toBitmap().asImageBitmap(),
                packageName = it.packageName
            )
        }
    val mutableAllApplicationInfo = allAppInfo.toMutableList()
    allAppInfo.forEach { applicationData ->
        if (mutableAllApplicationInfo.count { it.packageName == applicationData.packageName } > 1) {
            mutableAllApplicationInfo.remove(applicationData)
        }
    }

    val allApplicationData = LauncherData.allApplicationData.value ?: emptyList()
    mutableAllApplicationInfo.replaceAll { applicationInfo ->
        allApplicationData.find { it.packageName == applicationInfo.packageName }
            ?.let { applicationData ->
                ApplicationInfo(
                    title = applicationData.title,
                    icon = applicationData.image.getItemImage(this)
                        ?: throw IllegalArgumentException("Illegal image"),
                    packageName = applicationData.packageName
                )
            } ?: applicationInfo
    }
    return mutableAllApplicationInfo.sortedBy { it.title }
}

fun Context.getApplications(): List<ApplicationInfo> {
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    val allAppInfo = packageManager.queryIntentActivities(intent, 0)
        .map { it.activityInfo.applicationInfo }
        .map {
            ApplicationInfo(
                title = it.loadLabel(packageManager).toString(),
                icon = it.loadIcon(packageManager).toBitmap().asImageBitmap(),
                packageName = it.packageName
            )
        }
    val mutableAllApplicationInfo = allAppInfo.toMutableList()
    allAppInfo.forEach { applicationData ->
        if (mutableAllApplicationInfo.count { it.packageName == applicationData.packageName } > 1) {
            mutableAllApplicationInfo.remove(applicationData)
        }
    }
    return mutableAllApplicationInfo.sortedBy { it.title }
}

fun List<CircleMenu>.getOnlyChanged(
    context: Context
) = this.getOnlyChanged(
    allCircleMenuIds = LauncherData.allCircleMenus.value?.map { it.id } ?: emptyList(),
    allPackageNames = context.getApplications().map { it.packageName },
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
    allCircleMenus: List<CircleMenu>,
    allApplicationData: List<ApplicationData>
) {
    val allUserImageNamesInCircleMenus = allCircleMenus.getUserImageNamesFromCircleMenus()
    val allUserImageNamesInApplicationData =
        allApplicationData.getUserImageNamesFromApplicationsData()
    this.filesDir.listFiles()?.forEach { file ->
        if (file.name.contains(".png") && file.name !in allUserImageNamesInCircleMenus && file.name !in allUserImageNamesInApplicationData) {
            file.delete()
        }
    }
}

private fun List<CircleMenu>.getUserImageNamesFromCircleMenus(): List<String> {
    return this
        .asSequence()
        .map { it.items } // get lists of items
        .flatten() // get one list with all items
        .map { it.image } // list with CircleMenuImage
        .filter { it.type == CircleMenuImageTypes.UserImage } // list with CircleMenuImage when type = UserImage
        .map { "${it.data.getAs(UserImage::class.java).id}.png" }
        .toList() // list of filenames
}

private fun List<ApplicationData>.getUserImageNamesFromApplicationsData(): List<String> {
    return this
        .asSequence()
        .map { it.image } // get lists of items
        .filter { it.type == CircleMenuImageTypes.UserImage } // list with CircleMenuImage when type = UserImage
        .map { "${it.data.getAs(UserImage::class.java).id}.png" }
        .toList() // list of filenames
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

private fun Setting.getClassOfSettingData(): Class<*> {
    return when (this) {
        Setting.OpenLastApp -> Boolean::class.java
        Setting.BlackTextColorOnWallpaper -> Boolean::class.java
        Setting.ClickOnClock -> ClickOnClock::class.java
        Setting.PickAppActionWithImage -> Boolean::class.java
    }
}

fun String.deserializableSettingValue(setting: Setting): Any? {
    val classOfData = setting.getClassOfSettingData()
    return Gson().fromJson(this, classOfData)
}

fun Any?.serializableSettingValue(): String {
    return Gson().toJson(this)
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
    @Suppress("DEPRECATION")
    MediaStore.Images
        .Media.getBitmap(contentResolver, uri)

} else {
    ImageDecoder.decodeBitmap(
        ImageDecoder
            .createSource(contentResolver, uri)
    )
}

fun Context.setActionAndImageTypes() {
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
        ActionType(
            name = this.resources.getString(R.string.open_url_action),
            imageResId = R.drawable.open_url_image,
            type = ActionTypes.OpenUrl
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
    Constants.imageTypes = listOf(
        ImageType(
            name = this.resources.getString(R.string.app_image),
            imageResId = R.drawable.app_image,
            type = CircleMenuImageTypes.AppImage
        ),
        ImageType(
            name = this.resources.getString(R.string.default_image),
            imageResId = R.drawable.default_image,
            type = CircleMenuImageTypes.DefaultImage
        ),
        ImageType(
            name = this.resources.getString(R.string.user_image),
            imageResId = R.drawable.user_image,
            type = CircleMenuImageTypes.UserImage
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
        CircleMenuActionTypes.OpenUrl -> Constants.actionTypes.find { it.type == ActionTypes.OpenUrl }
    }
}

fun CircleMenuImageTypes.getImageType(): ImageType? {
    return Constants.imageTypes.find { it.type == this }
}

fun Context.getApplicationInfo(packageName: String): ApplicationInfo {
    val applicationInfo =
        LauncherData.allApplicationInfo.value?.find { it.packageName == packageName }
    return if (applicationInfo == null) {
        val appInfo =
            packageManager.getApplicationInfo(packageName, 0)
        ApplicationInfo(
            title = appInfo.loadLabel(packageManager).toString(),
            icon = appInfo.loadIcon(packageManager).toBitmap().asImageBitmap(),
            packageName = appInfo.packageName
        )
    } else {
        applicationInfo
    }
}

fun Context.getThisAppIcon() =
    packageManager.getApplicationInfo(packageName, 0).loadIcon(packageManager).toBitmap()
        .asImageBitmap()

fun Context.getApplicationData(packageName: String): ApplicationData {
    val applicationData =
        LauncherData.allApplicationData.value?.find { it.packageName == packageName }
    return if (applicationData == null) {
        val applicationInfo =
            packageManager.getApplicationInfo(packageName, 0)
        ApplicationData(
            title = applicationInfo.loadLabel(packageManager).toString(),
            image = CircleMenuImage(
                type = CircleMenuImageTypes.AppImage,
                data = AppImage(packageName)
            ),
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
    return minOf(
        resources.configuration.screenWidthDp,
        resources.configuration.screenHeightDp
    ).toFloat()
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

@Composable
fun pickUserImageLauncher(
    onPick: (CircleMenuImage) -> Unit
): ManagedActivityResultLauncher<String, Uri?> {
    val context = LocalContext.current
    return rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val ids = LauncherData.userImages.map { it.key }
            var newId = 0
            while (newId in ids) {
                newId++
            }
            val bitmap = context.createBitmap(uri)
            LauncherData.userImages = LauncherData.userImages.toMutableMap().apply {
                this[newId] = bitmap.asImageBitmap()
            }.toMap()
            context.addUserImage(newId, bitmap)
            onPick(
                CircleMenuImage(
                    type = CircleMenuImageTypes.UserImage,
                    data = UserImage(id = newId)
                )
            )
        }
    }
}

fun <T> List<SettingData>.getValueOf(setting: Setting, classOfT: Class<T>): T? {
    val gson = Gson()
    return gson.fromJson(
        gson.toJson(this.find { it.setting == setting }?.value ?: return null),
        classOfT
    )
}

fun getLauncherStatusBarStyle(): SystemBarStyle {
    return if (LauncherData.settings.value?.getValueOf(
            Setting.BlackTextColorOnWallpaper,
            Boolean::class.java
        ) == true
    ) SystemBarStyle.light(
        android.graphics.Color.TRANSPARENT,
        android.graphics.Color.TRANSPARENT
    ) else SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
}

fun Offset.getItemOffset(menuSize: Float): Offset {
    val itemSize = menuSize / 5
    val x = (menuSize / 2 - itemSize / 2) + this.x * menuSize / 10
    val y = (menuSize / 2 - itemSize / 2) + this.y * menuSize / 10
    return Offset(x, y)
}

private data class ItemCords(
    val xStart: Float?,
    val xEnd: Float?,
    val yStart: Float?,
    val yEnd: Float?
)

fun CircleMenu.getCircleMenuItem(offset: Offset, menuSize: Float): CircleMenuItem? {
    items.forEach {  item ->
        val itemCords = item.offset.getItemCords(menuSize)
        for (cords in itemCords) {
            if (
                (cords.xStart == null || offset.x >= cords.xStart) && (cords.xEnd == null || offset.x <= cords.xEnd) // x
                &&
                (cords.yStart == null || offset.y >= cords.yStart) && (cords.yEnd == null || offset.y <= cords.yEnd) // y
            ) {
                return item
            }
        }
    }
    return null
}

private fun Offset.getItemCords(menuSize: Float): List<ItemCords> {
    if (x == 0f && y == -4f) { // 1
        return listOf(
            ItemCords(
                xStart = -menuSize / 6,
                xEnd = menuSize / 6,
                yStart = null,
                yEnd = -menuSize / 2 + menuSize / 5
            )
        )
    }
    if (x == 3f && y == -3f) { // 2
        return listOf(
            ItemCords(
                xStart = menuSize / 2 - menuSize / 3,
                xEnd = menuSize / 2 - menuSize / 5,
                yStart = null,
                yEnd = -menuSize / 2 + menuSize / 5
            ),
            ItemCords(
                xStart = menuSize / 2 - menuSize / 5,
                xEnd = null,
                yStart = null,
                yEnd = -menuSize / 2 + menuSize / 3
            ),
            ItemCords(
                xStart = menuSize / 10 * 3 - menuSize / 10,
                xEnd = menuSize / 10 * 3 + menuSize / 10,
                yStart = -menuSize / 10 * 3 - menuSize / 10,
                yEnd = -menuSize / 10 * 3 + menuSize / 10,
            )
        )
    }
    if (x == 4f && y == 0f) { // 3
        return listOf(
            ItemCords(
                xStart = menuSize / 2 - menuSize / 5,
                xEnd = null,
                yStart = -menuSize / 6,
                yEnd = menuSize / 6
            )
        )
    }
    if (x == 3f && y == 3f) { // 4
        return listOf(
            ItemCords(
                xStart = menuSize / 2 - menuSize / 3,
                xEnd = menuSize / 2 - menuSize / 5,
                yStart = menuSize / 2 - menuSize / 5,
                yEnd = null
            ),
            ItemCords(
                xStart = menuSize / 2 - menuSize / 5,
                xEnd = null,
                yStart = menuSize / 2 - menuSize / 3,
                yEnd = null
            ),
            ItemCords(
                xStart = menuSize / 10 * 3 - menuSize / 10,
                xEnd = menuSize / 10 * 3 + menuSize / 10,
                yStart = menuSize / 10 * 3 - menuSize / 10,
                yEnd = menuSize / 10 * 3 + menuSize / 10,
            )
        )
    }
    if (x == 0f && y == 4f) { // 5
        return listOf(
            ItemCords(
                xStart = -menuSize / 6,
                xEnd = menuSize / 6,
                yStart = menuSize / 2 - menuSize / 5,
                yEnd = null
            )
        )
    }
    if (x == -3f && y == 3f) { // 6
        return listOf(
            ItemCords(
                xStart = -menuSize / 2 + menuSize / 5,
                xEnd = -menuSize / 2 + menuSize / 3,
                yStart = menuSize / 2 - menuSize / 5,
                yEnd = null
            ),
            ItemCords(
                xStart = null,
                xEnd = -menuSize / 2 + menuSize / 5,
                yStart = menuSize / 2 - menuSize / 3,
                yEnd = null
            ),
            ItemCords(
                xStart = -menuSize / 10 * 3 - menuSize / 10,
                xEnd = -menuSize / 10 * 3 + menuSize / 10,
                yStart = menuSize / 10 * 3 - menuSize / 10,
                yEnd = menuSize / 10 * 3 + menuSize / 10,
            )
        )
    }
    if (x == -4f && y == 0f) { // 7
        return listOf(
            ItemCords(
                xStart = null,
                xEnd = -menuSize / 2 + menuSize / 5,
                yStart = -menuSize / 6,
                yEnd = menuSize / 6
            )
        )
    }
    if (x == -3f && y == -3f) { // 8
        return listOf(
            ItemCords(
                xStart = -menuSize / 2 + menuSize / 5,
                xEnd = -menuSize / 2 + menuSize / 3,
                yStart = null,
                yEnd = -menuSize / 2 + menuSize / 5
            ),
            ItemCords(
                xStart = null,
                xEnd = -menuSize / 2 + menuSize / 5,
                yStart = null,
                yEnd = -menuSize / 2 + menuSize / 3
            ),
            ItemCords(
                xStart = -menuSize / 10 * 3 - menuSize / 10,
                xEnd = -menuSize / 10 * 3 + menuSize / 10,
                yStart = -menuSize / 10 * 3 - menuSize / 10,
                yEnd = -menuSize / 10 * 3 + menuSize / 10,
            )
        )
    }
    return emptyList()
}

fun Offset.getSelectedBoxOffset(menuSize: Float): Offset {
    val itemSize = menuSize / 4
    val x = (menuSize / 2 - itemSize / 2) + this.x * menuSize / 10
    val y = (menuSize / 2 - itemSize / 2) + this.y * menuSize / 10
    return Offset(x, y)
}

fun Context.openApp(packageName: String) {
    val intent =
        this.packageManager.getLaunchIntentForPackage(packageName)
    intent?.let { this.startActivity(it) }
}

fun Context.executeSearchResult(result: SearchBoxResult) {
    when (result) {
        is AppSBR -> {
            if (result.applicationInfo.packageName == packageName) {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            } else {
                openApp(result.applicationInfo.packageName)
            }
        }
    }
}

fun Context.getNotMaskApplicationData(packageName: String): ApplicationData {
    val applicationInfo =
        packageManager.getApplicationInfo(packageName, 0)
    return ApplicationData(
        title = applicationInfo.loadLabel(packageManager).toString(),
        image = CircleMenuImage(type = CircleMenuImageTypes.AppImage, data = AppImage(packageName)),
        packageName = applicationInfo.packageName
    )
}

fun Context.deleteApp(packageName: String) {
    val packageUri = Uri.parse("package:$packageName")
    val uninstallIntent = Intent(Intent.ACTION_DELETE, packageUri)
    startActivity(uninstallIntent)
}

suspend fun List<ApplicationData>.check(
    allApplicationInfo: List<ApplicationInfo>,
    context: Context
) {
    val allPackageNames = allApplicationInfo.map { it.packageName }
    val toDelete = mutableListOf<ApplicationData>()
    val toChange = mutableListOf<ApplicationData>()
    this.forEach { applicationData ->
        if (applicationData.packageName !in allApplicationInfo.map { it.packageName }) {
            toDelete.add(applicationData)
        } else {
            if (applicationData.image.type == CircleMenuImageTypes.AppImage) {
                if (applicationData.image.data.getAs(AppImage::class.java).packageName !in allPackageNames) {
                    val appInfo = context.getNotMaskApplicationData(applicationData.packageName)
                    if (appInfo.title == applicationData.title && !applicationData.hidden) {
                        toDelete.add(applicationData)
                    } else {
                        toChange.add(
                            applicationData.copy(
                                image = CircleMenuImage(
                                    type = CircleMenuImageTypes.AppImage,
                                    data = AppImage(applicationData.packageName)
                                )
                            )
                        )
                    }
                }
            }
        }
    }
    LauncherData.insertApplicationsData(toChange)
    LauncherData.deleteApplicationsData(toDelete)
}

suspend fun Context.hideApp(packageName: String) {
    LauncherData.insertApplicationData(
        LauncherData.allApplicationData.value?.find { it.packageName == packageName }
            ?.copy(hidden = true)
            ?: this.getApplicationData(packageName).copy(hidden = true)
    )
}

suspend fun Context.showApp(packageName: String) {
    LauncherData.allApplicationData.value?.find { it.packageName == packageName }
        ?.let { applicationData ->
            val appInfo = getNotMaskApplicationData(applicationData.packageName)
            if (applicationData.title == appInfo.title && applicationData.image.type == CircleMenuImageTypes.AppImage && applicationData.image.data.getAs(
                    AppImage::class.java
                ).packageName == appInfo.packageName
            ) {
                LauncherData.deleteApplicationData(applicationData)
            } else {
                LauncherData.insertApplicationData(applicationData.copy(hidden = false))
            }
        }
}

suspend fun Context.changeApp(applicationData: ApplicationData) {
    val appInfo = getNotMaskApplicationData(applicationData.packageName)
    if (applicationData.title == appInfo.title && applicationData.image.type == CircleMenuImageTypes.AppImage && applicationData.image.data.getAs(
            AppImage::class.java
        ).packageName == appInfo.packageName
    ) {
        LauncherData.deleteApplicationDataByPackageName(applicationData.packageName)
    } else {
        LauncherData.insertApplicationData(
            LauncherData.allApplicationData.value?.find { it.packageName == applicationData.packageName }
                ?.copy(title = applicationData.title, image = applicationData.image)
                ?: applicationData
        )
    }
}

fun List<ApplicationInfo>.getNotHidden(): List<ApplicationInfo> {
    val result = this.toMutableList()
    val hidden = LauncherData.allApplicationData.value?.filter { it.hidden }?.map { it.packageName }
        ?: emptyList()
    this.forEach {
        if (it.packageName in hidden) {
            result.remove(it)
        }
    }
    return result
}

fun List<ApplicationInfo>.getHidden(allApplicationData: List<ApplicationData>): List<ApplicationInfo> {
    val result = mutableListOf<ApplicationInfo>()
    val hidden = allApplicationData.filter { it.hidden }.map { it.packageName }
    this.forEach {
        if (it.packageName in hidden) {
            result.add(it)
        }
    }
    return result
}

private fun getDefaultCameraApp(context: Context): String? {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val resolveInfo =
        context.packageManager.resolveActivity(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY)
    if (resolveInfo != null) {
        return resolveInfo.activityInfo?.packageName
    }
    return null
}

@SuppressLint("IntentReset")
fun getDefaultGalleryApp(context: Context): String? {
    val mainIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    mainIntent.type = "image/*"
    val pkgAppsList =
        context.packageManager.queryIntentActivities(mainIntent, PackageManager.GET_RESOLVED_FILTER)
    if (pkgAppsList.isNotEmpty()) {
        for (resolveInfo in pkgAppsList) {
            return resolveInfo.activityInfo.packageName
        }
    }
    return null
}

private fun getDefaultBrowserApp(context: Context): String? {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
    val resolveInfo =
        context.packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
    return resolveInfo?.activityInfo?.packageName
}

private fun getDefaultPhoneApp(context: Context): String? {
    val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    return telecomManager.defaultDialerPackage
}

private fun getDefaultEmailApp(context: Context): String? {
    val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
    val resolveInfoList =
        context.packageManager.queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY)
    if (resolveInfoList.isNotEmpty()) {
        for (resolveInfo in resolveInfoList) {
            return resolveInfo.activityInfo.packageName
        }
    }
    return null
}

private fun getDefaultSmsApp(context: Context): String? {
    return Telephony.Sms.getDefaultSmsPackage(context)
}

private fun getDefaultSettingsApp(context: Context): String? {
    val intent = Intent(Settings.ACTION_SETTINGS)
    val resolveInfo =
        context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    if (resolveInfo.isEmpty()) {
        return null
    }

    return resolveInfo.firstOrNull()?.activityInfo?.packageName
}

private fun Offset.getCircleMenuItemByPackageName(packageName: String?): CircleMenuItem? {
    if (packageName == null) return null
    return CircleMenuItem(
        offset = this,
        image = CircleMenuImage(
            type = CircleMenuImageTypes.AppImage,
            data = AppImage(packageName = packageName)
        ),
        action = CircleMenuAction(
            type = CircleMenuActionTypes.OpenApp,
            data = OpenApp(packageName = packageName)
        )
    )
}

fun Context.getRootCircleMenu(title: String): CircleMenu {
    val items = mutableListOf<CircleMenuItem>()
    Constants.menuCords[0].getCircleMenuItemByPackageName(getDefaultCameraApp(this))
        ?.let { items.add(it) }
    Constants.menuCords[1].getCircleMenuItemByPackageName(getDefaultGalleryApp(this))
        ?.let { items.add(it) }
    Constants.menuCords[2].getCircleMenuItemByPackageName(getDefaultBrowserApp(this))
        ?.let { items.add(it) }
    Constants.menuCords[3].getCircleMenuItemByPackageName(getDefaultPhoneApp(this))
        ?.let { items.add(it) }
    items.add(
        CircleMenuItem(
            offset = Constants.menuCords[4],
            image = CircleMenuImage(
                type = CircleMenuImageTypes.DefaultImage,
                data = DefaultImage.Settings
            ),
            action = CircleMenuAction(type = CircleMenuActionTypes.OpenSettings)
        )
    )
    Constants.menuCords[5].getCircleMenuItemByPackageName(getDefaultEmailApp(this))
        ?.let { items.add(it) }
    Constants.menuCords[6].getCircleMenuItemByPackageName(getDefaultSmsApp(this))
        ?.let { items.add(it) }
    Constants.menuCords[7].getCircleMenuItemByPackageName(getDefaultSettingsApp(this))
        ?.let { items.add(it) }

    return CircleMenu(
        title = title,
        items = items
    )
}