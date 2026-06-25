package com.kindeev.swipelauncher.domain.viewModels.screens.launcherScreen

import android.content.Context
import android.net.Uri
import android.os.Vibrator
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.entities.ApplicationData
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.CallAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.ChangeFlashLightConditionAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.DialAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOffAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.FlashLightOnAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenCircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenSettingsAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenUrlAction
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.CircleMenuWithOffset
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.screenStates.LauncherScreenState
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.CheckCircleMenuUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.FlashLightUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenSettingsUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.OpenUrlUseCase
import com.kindeev.swipelauncher.domain.useCases.circleMenuActions.TelephoneUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class LauncherScreenVM(context: Context) : ViewModel() {

    private val applicationsUseCase = ApplicationsUseCase(context)
    private val checkCircleMenuUseCase =
        CheckCircleMenuUseCase(LauncherData.userImagesRepository, applicationsUseCase)
    private val telephoneUseCase = TelephoneUseCase(context)
    private val openSettingsUseCase = OpenSettingsUseCase(context)
    private val flashLightUseCase = FlashLightUseCase(context)
    private val openUrlUseCase = OpenUrlUseCase(context)

    private val _currentMenu = MutableStateFlow(
        LauncherData.allCircleMenus.value.find { it.id == 0 }?.let {
            CircleMenuWithOffset(it, null)
        }
    )
    val currentMenu: StateFlow<CircleMenuWithOffset?> = _currentMenu
    private val vibrator =
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val density = context.resources.displayMetrics.density

    private val size = Constants.minScreenLength / 3f * 2
    private val radius = (size / 2 - size / 5)
    private val radiusSq = radius.pow(2)
    private var offsets = getOffsets(LauncherData.allCircleMenus.value, size)
    private var sizes = getSizes(LauncherData.allCircleMenus.value)

    private var clickTime = 0L
    private var actionInProgress = false

    private val _screenState = MutableStateFlow(LauncherScreenState.SwipeBox)
    val screenState: StateFlow<LauncherScreenState> = _screenState

    fun setCircleMenu(circleMenu: CircleMenu) {
        _currentMenu.value = _currentMenu.value?.copy(circleMenu = circleMenu)
    }

    fun setOffsets(circleMenus: List<CircleMenu>) {
        offsets = getOffsets(circleMenus, size)
    }

    fun setSizes(circleMenus: List<CircleMenu>) {
        sizes = getSizes(circleMenus)
    }

    fun closeSearchBox() {
        _screenState.value = LauncherScreenState.SwipeBox
        clearSearch()
    }

    fun getOffset(): List<DpOffset> {
        if (offsets.isEmpty()) {
            return emptyList()
        }
        currentMenu.value?.circleMenu?.id?.let {
            return offsets[it] ?: emptyList()
        }
        return emptyList()
    }

    val itemSize: Dp
        get() = sizes[currentMenu.value?.circleMenu?.id ?: 0] ?: 0.dp

    fun onSwipe(): (MotionEvent) -> Boolean = { event ->
        val offset = Offset(
            x = event.x / density,
            y = event.y / density
        )
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.eventTime - clickTime < 300L) {
                    // Double click
                    _screenState.value = LauncherScreenState.SearchBox
                } else {
                    _currentMenu.value = _currentMenu.value?.copy(offset = offset)
                }
                clickTime = event.eventTime
                actionInProgress = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (!actionInProgress) {
                    actionInProgress = true
                    currentMenu.value?.offset?.let {
                        val index = getElementIndexOnCords(
                            offset = Offset(
                                x = offset.x - it.x,
                                y = offset.y - it.y,
                            )
                        )
                        index?.let {
                            currentMenu.value?.circleMenu?.items?.getOrNull(index)?.let { item ->
                                viewModelScope.launch {
                                    executeAction(item.action, offset)
                                }
                            }
                        }
                    }
                    actionInProgress = false
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                LauncherData.allCircleMenus.value.find { it.id == 0 }?.let {
                    _currentMenu.value =
                        CircleMenuWithOffset(
                            circleMenu = it,
                            offset = null
                        )
                }
            }
        }
        true
    }

    suspend fun executeAction(
        action: CircleMenuAction,
        offset: Offset? = null
    ) {
        when (action) {

            is OpenCircleMenuAction -> {
                offset?.let { newOffset ->
                    var circleMenuForCheck =
                        LauncherData.allCircleMenus.value.find { it.id == action.id }
                            ?: LauncherData.allCircleMenus.value.find { it.id == 0 }
                    circleMenuForCheck?.let { menu ->
                        circleMenuForCheck =
                            if (checkCircleMenuUseCase.check(
                                    menu
                                )
                            ) {
                                menu
                            } else {
                                LauncherData.allCircleMenus.value.find { it.id == 0 }
                            }
                    }
                    circleMenuForCheck?.let {
                        _currentMenu.value =
                            CircleMenuWithOffset(
                                circleMenu = it,
                                offset = newOffset
                            )
                    }
                    vibrator.vibrate(20)
                }
            }

            is OpenSettingsAction -> {
                openSettingsUseCase.invoke()
            }

            is OpenAppAction -> {
                applicationsUseCase.openApp(action.packageName)
            }

            is FlashLightOnAction -> {
                flashLightUseCase.on()
                LauncherData.flashLightCondition = true
            }

            is FlashLightOffAction -> {
                flashLightUseCase.off()
                LauncherData.flashLightCondition = false
            }

            is ChangeFlashLightConditionAction -> {
                if (LauncherData.flashLightCondition) {
                    flashLightUseCase.off()
                } else {
                    flashLightUseCase.on()
                }
                LauncherData.flashLightCondition = !LauncherData.flashLightCondition
            }

            is CallAction -> {
                telephoneUseCase.call(action.phoneNumber)
            }

            is DialAction -> {
                telephoneUseCase.dial(action.phoneNumber)
            }

            is OpenUrlAction -> {
                openUrlUseCase.open(action.url)
            }
        }
    }

    private fun getOffsets(
        circleMenus: List<CircleMenu>,
        size: Float
    ): Map<Int, List<DpOffset>> {
        val offsets = mutableMapOf<Int, List<DpOffset>>()
        circleMenus.forEach { circleMenu ->
            val itemSize = getSize(circleMenu.items.size)
            val alpha = 360f / circleMenu.items.size
            offsets[circleMenu.id] = circleMenu.items.indices.map { alpha * it }.map {
                DpOffset(
                    x = (size / 2 + sin((it + 0.5f * alpha + circleMenu.getStartOffset()) * PI / 180f).toFloat() * (size / 2 - itemSize / 2) - itemSize / 2).dp,
                    y = (size / 2 - cos((it + 0.5f * alpha + circleMenu.getStartOffset()) * PI / 180f).toFloat() * (size / 2 - itemSize / 2) - itemSize / 2).dp,
                )
            }
        }
        return offsets
    }

    private fun getSizes(
        circleMenus: List<CircleMenu>
    ): Map<Int, Dp> {
        val sizes = mutableMapOf<Int, Dp>()
        circleMenus.forEach { circleMenu ->
            sizes[circleMenu.id] = getSize(circleMenu.items.size).dp
        }
        return sizes
    }

    private fun getSize(
        elementsCount: Int
    ): Float {
        if (elementsCount == 0) {
            return size / 4
        }
        val value = sqrt((size / 2).pow(2) * (1 - cos(2 * PI / elementsCount))).toFloat() / 6 * 5
        return if (value > 0 && value < size / 4) {
            value
        } else {
            size / 4
        }
    }

    private fun getElementIndexOnCords(
        offset: Offset
    ): Int? {
        currentMenu.value?.circleMenu?.let { circleMenu ->
            if (offset.x.pow(2) + offset.y.pow(2) > radiusSq) {
                val alpha = 360f / circleMenu.items.size
                val angles = circleMenu.items.indices.map { alpha * it }
                val currentAngle = if (offset.y == 0f) {
                    ((if (offset.x > 0) 90 else 270) - circleMenu.getStartOffset()) % 360
                } else {
                    offset.getAngle(
                        abs((atan(offset.x / offset.y) / PI * 180)).toFloat(),
                        circleMenu.getStartOffset()
                    )
                }
                angles.forEachIndexed { index, it ->
                    if (it > currentAngle) {
                        return index - 1
                    }
                }
                return circleMenu.items.size - 1
            }
        }
        return null
    }

    private fun Offset.getAngle(angle: Float, startOffset: Float): Float {
        return (if (x > 0) {
            if (y < 0) {
                angle - startOffset
            } else {
                90 - angle + 90 - startOffset
            }
        } else {
            if (y < 0) {
                90 - angle + 270 - startOffset
            } else {
                angle + 180 - startOffset
            }
        }) % 360
    }

    private fun CircleMenu.getStartOffset(): Float {
        if (items.isEmpty()) {
            return 0f
        }
        return -360 / items.size / 2f
    }


// Search Box

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    fun search(text: String) {
        _searchText.value = text
    }

    fun clearSearch() {
        _searchText.value = ""
    }

    fun getSearchResults(allApplicationInfo: List<ApplicationInfo>): List<ApplicationData> {
        return applicationsUseCase.getAllApplicationData(
            applicationsUseCase
                .getNotHidden(allApplicationInfo)
                .filter {
                    it.title
                        .lowercase()
                        .contains(searchText.value.lowercase().trim())
                }
        )
    }

// ApplicationInfoDialog

    var userImageGetProcess = false

    fun getApplicationData(packageName: String): ApplicationData {
        return applicationsUseCase.getApplicationData(packageName)
    }

    fun getNotMaskApplicationData(packageName: String): ApplicationData {
        return applicationsUseCase.getNotMaskApplicationData(packageName)
    }

    fun getAppDetails(packageName: String) {
        applicationsUseCase.getAppDetails(packageName)
    }

    fun deleteApp(packageName: String) {
        applicationsUseCase.deleteApp(packageName)
    }

    fun changeApp(applicationData: ApplicationData) {
        viewModelScope.launch { applicationsUseCase.changeApp(applicationData) }
    }

    suspend fun addUserImage(uri: Uri) = withContext(Dispatchers.IO) {
        LauncherData.userImagesRepository.insert(uri = uri)?.let { UserImage(it) }
    }

    fun getAllApplicationsData(applicationsInfo: List<ApplicationInfo>): List<ApplicationData> {
        return applicationsUseCase.getAllApplicationData(applicationsInfo)
    }
}