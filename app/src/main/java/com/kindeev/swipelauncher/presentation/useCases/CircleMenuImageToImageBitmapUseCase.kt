package com.kindeev.swipelauncher.presentation.useCases

import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Process
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.UserImage
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.interfaces.UserImagesRepository
import com.kindeev.swipelauncher.presentation.interfaces.CircleMenuImageToImageBitmap
import com.kindeev.swipelauncher.presentation.interfaces.DrawableGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CircleMenuImageToImageBitmapUseCase(
    private val userImagesRepository: UserImagesRepository,
    private val drawableGetter: DrawableGetter,
    context: Context,
    ioScope: CoroutineScope,
    dataRepository: DataRepository,
    getSystemServiceUseCase: GetSystemServiceUseCase,
) : CircleMenuImageToImageBitmap {

    private val appContext = context.applicationContext

    private val launcherApps =
        getSystemServiceUseCase.get(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    private val _mapper = MutableStateFlow(emptyMap<CircleMenuImage, ImageBitmap>())
    override val mapper: StateFlow<Map<CircleMenuImage, ImageBitmap>> = _mapper.asStateFlow()

    init {
        ioScope.launch {
            dataRepository
                .getAllCircleMenus()
                .flowOn(Dispatchers.IO)
                .map {
                    it
                        .flatMap { menu ->
                            menu.items
                        }
                        .mapNotNull { item ->
                            item.image.getImageBitmapInterface()?.let { imageBitmap ->
                                item.image to imageBitmap
                            }
                        }
                        .associate { pair -> pair }

                }
                .distinctUntilChanged()
                .collect { imageBitmaps ->
                    _mapper.value = imageBitmaps
                }
        }
    }

    suspend fun updateImageForPackageName(packageName: String) = withContext(Dispatchers.IO) {
        val currentKey = AppImage(packageName)
        if (mapper.value.containsKey(currentKey)) {
            _mapper.value =
                mapper.value.mapValues { (key, value) ->
                    if (key == currentKey) {
                        currentKey.getAppImageBitmap()
                    } else value
                }
        }
    }

    fun getImageBitmap(circleMenuImage: CircleMenuImage): ImageBitmap? =
        mapper.value[circleMenuImage] ?: circleMenuImage.getImageBitmapInterface()

    private fun CircleMenuImage.getImageBitmapInterface(): ImageBitmap? {
        return when (this) {
            is AppImage -> getAppImageBitmap()

            is DefaultImage -> getDefaultImageBitmap()

            is UserImage -> getUserImageBitmap()
        }
    }

    private fun AppImage.getAppImageBitmap(): ImageBitmap =
        launcherApps.getApplicationInfo(
            packageName,
            PackageManager.GET_META_DATA,
            Process.myUserHandle()
        )
            .loadIcon(appContext.packageManager).toBitmap().asImageBitmap()

    private fun DefaultImage.getDefaultImageBitmap(): ImageBitmap? {
        return drawableGetter.getDrawable(
            Constants.defaultImages[data] ?: return null,
        )?.toBitmap()?.asImageBitmap()
    }

    private fun UserImage.getUserImageBitmap(): ImageBitmap? {
        val file = userImagesRepository.getFile(id)
        return if (file.exists()) {
            BitmapFactory.decodeFile(
                userImagesRepository.getFile(id).absolutePath ?: return null
            ).asImageBitmap()
        } else null
    }
}