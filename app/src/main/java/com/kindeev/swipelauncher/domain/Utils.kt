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
import com.kindeev.swipelauncher.presentation.entities.ActionType
import com.kindeev.swipelauncher.presentation.entities.ActionTypes
import com.kindeev.swipelauncher.presentation.entities.FlashlightActionType
import com.kindeev.swipelauncher.presentation.entities.ImageType
import com.kindeev.swipelauncher.presentation.entities.TelephoneActionType
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
        data = DefaultImage.Settings
    )
    val action = CircleMenuAction(
        type = CircleMenuActionTypes.OpenSettings
    )
    return CircleMenu(
        title = title,
        items = listOf(
            CircleMenuItem( // 1
                offset = Offset(0f, -4f),
                image = image,
                action = action
            ),
            CircleMenuItem( // 2
                offset = Offset(3f, -3f),
                image = image,
                action = action
            ),
            CircleMenuItem( // 3
                offset = Offset(4f, 0f),
                image = image,
                action = action
            ),
            CircleMenuItem( // 4
                offset = Offset(3f, 3f),
                image = image,
                action = action
            ),
            CircleMenuItem( // 5
                offset = Offset(0f, 4f),
                image = image,
                action = action
            ),
            CircleMenuItem( // 6
                offset = Offset(-3f, 3f),
                image = image,
                action = action
            ),
            CircleMenuItem( // 7
                offset = Offset(-4f, 0f),
                image = image,
                action = action
            ),
            CircleMenuItem( // 8
                offset = Offset(-3f, -3f),
                image = image,
                action = action
            )
        )
    )
}

fun CircleMenu.check(
    allPackageNames: List<String>,
    allCircleMenuIds: List<Int>,
    userImageIds: List<Int>
): CircleMenu? {
    val newItems = mutableListOf<CircleMenuItem>()

    // Check OpenApp
    this.items.forEach { item ->
        var image = item.image
        var action = item.action

        // Check Image
        val defaultImage = CircleMenuImage(
            type = CircleMenuImageTypes.DefaultImage,
            data = DefaultImage.Error
        )
        when (item.image.type) {
            CircleMenuImageTypes.AppImage -> {
                if (item.image.data.getAs(AppImage::class.java).packageName !in allPackageNames) {
                    image = defaultImage
                }
            }

            CircleMenuImageTypes.UserImage -> {
                if (item.image.data.getAs(UserImage::class.java).id !in userImageIds) {
                    image = defaultImage
                }
            }

            else -> {}
        }

        // Check Action
        val defaultAction = CircleMenuAction(
            type = CircleMenuActionTypes.OpenCircleMenu,
            data = OpenCircleMenu(id = 0)
        )
        when (item.action.type) {
            CircleMenuActionTypes.OpenApp -> {
                if (item.action.data.getAs(OpenApp::class.java).packageName !in allPackageNames) {
                    action = defaultAction
                }
            }

            CircleMenuActionTypes.OpenCircleMenu -> {
                if (item.action.data.getAs(OpenCircleMenu::class.java).id !in allCircleMenuIds) {
                    action = defaultAction
                }
            }

            else -> {}
        }
        newItems.add(item.copy(image = image, action = action))
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
    return this
        .asSequence()
        .map { it.items } // get lists of items
        .flatten() // get one list with all items
        .map { it.image } // list with CircleMenuImage
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
    }
}

fun CircleMenuImageTypes.getImageType(): ImageType? {
    return Constants.imageTypes.find { it.type == this }
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

fun Offset.getCircleMenuOffset(menuSize: Float): Offset {
    val x = this.x * menuSize / 10
    val y = this.y * menuSize / 10
    return Offset(x, y)
}

private data class ItemCords(val xStart: Float, val xEnd: Float, val yStart: Float, val yEnd: Float)

fun CircleMenu.getCircleMenuItem(offset: Offset, menuSize: Float): CircleMenuItem? {
    for (item in items) {
        val itemCords = item.offset.getItemCords(menuSize)
        if (offset.x < itemCords.xStart || offset.x > itemCords.xEnd) continue
        if (offset.y < itemCords.yStart || offset.y > itemCords.yEnd) continue
        return item
    }
    return null
}

private fun Offset.getItemCords(menuSize: Float): ItemCords {
    val itemSize = menuSize / 6
    val x = this.x * menuSize / 10
    val y = this.y * menuSize / 10
    return ItemCords(
        xStart = x - itemSize / 2,
        xEnd = x + itemSize / 2,
        yStart = y - itemSize / 2,
        yEnd = y + itemSize / 2
    )
}

fun Offset.getSelectedBoxOffset(menuSize: Float): Offset {
    val itemSize = menuSize / 4
    val x = (menuSize / 2 - itemSize / 2) + this.x * menuSize / 10
    val y = (menuSize / 2 - itemSize / 2) + this.y * menuSize / 10
    return Offset(x, y)
}