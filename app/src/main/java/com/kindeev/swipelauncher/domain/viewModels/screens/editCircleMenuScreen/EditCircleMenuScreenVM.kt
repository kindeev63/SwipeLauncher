package com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen

import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImages
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.defaultImage.DefaultImage
import android.content.Context
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenAppAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuAction.actionTypes.OpenSettingsAction
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.dataBase.entities.circleMenu.circleMenuItem.circleMenuImage.imageTypes.AppImage
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.SettingNames
import com.kindeev.swipelauncher.domain.dataBase.entities.settings.settingValues.PickAppActionWithImage
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase
import com.kindeev.swipelauncher.domain.useCases.UserImagesUseCase
import com.kindeev.swipelauncher.domain.utils.getValueOf
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.entities.ActionItemData
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.entities.ActionItemDataType
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.entities.GhostCircleMenuItem
import com.kindeev.swipelauncher.domain.viewModels.screens.editCircleMenuScreen.entities.SelectedItemBoxData
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class EditCircleMenuScreenVM(
    circleMenuId: Int?,
    val size: Float,
    context: Context
) : ViewModel() {

    private val getItemImageUseCase = GetItemImageUseCase(context)
    private val applicationsUseCase = ApplicationsUseCase(context)
    private val userImagesUseCase = UserImagesUseCase(context)

    // CircleMenu
    private val _circleMenu = MutableLiveData<CircleMenu?>(null)
    val circleMenu: LiveData<CircleMenu?> = _circleMenu
    private val menu: CircleMenu
        get() = _circleMenu.value ?: throw IllegalArgumentException("Illegal CircleMenu")

    // ItemSize
    var itemSize = 0f

    init {
        if (circleMenuId == null) {
            val allIds = LauncherData.allCircleMenus.value?.map { it.id } ?: emptyList()
            var currentId = 0
            while (true) {
                if (currentId !in allIds) break
                currentId++
            }
            val circleMenu = CircleMenu(
                id = currentId,
                title = "New",
                items = emptyList()
            )
            _circleMenu.value = circleMenu
            itemSize = getItemSize(0)
        } else {
            val menu = LauncherData.allCircleMenus.value?.find { it.id == circleMenuId }
            _circleMenu.value = menu
            itemSize = getItemSize(menu?.items?.size ?: 0)
        }
    }

    fun getItemImage(circleMenuImage: CircleMenuImage): ImageBitmap? {
        return getItemImageUseCase.getItemImage(circleMenuImage)
    }

    fun changeTitle(title: String) {
        _circleMenu.value = circleMenu.value?.copy(title = title)
        updateCircleMenu()
    }

    val startOffset: Float
        get() = getStartOffset(menu.items.size)

    // SelectedBoxItem
    private val _selectedBoxData = MutableLiveData<SelectedItemBoxData?>(getSelectedBoxData(0))
    val selectedBoxData: LiveData<SelectedItemBoxData?> = _selectedBoxData

    // GhostItem
    private val _ghostItem = MutableLiveData<GhostCircleMenuItem?>(null)
    val ghostItem: LiveData<GhostCircleMenuItem?> = _ghostItem

    // Radius
    private var actionRadiusSq = getActionRadiusSq()
    private val swipeRadiusSq = (size / 2 - itemSize / 2).pow(2)

    // Density
    private val density = context.resources.displayMetrics.density

    // ActionItemData
    private val _actionItemData = MutableLiveData<ActionItemData?>(
        ActionItemData(
            size = getActionItemSize(),
            elementOnTop = false,
            action = ActionItemDataType.Add
        )
    )
    val actionItemData: LiveData<ActionItemData?> = _actionItemData

    private fun getItemSize(elementsCount: Int): Float {
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

    private fun getActionRadiusSq(): Float {
        val value = ((size - itemSize * 2) / 2).pow(2)
        return if (value > (size / 4).pow(2)) {
            (size / 4).pow(2)
        } else value
    }

    private fun getActionItemSize(): Float {
        val value = size / 2 - itemSize
        return if (value > size / 2) {
            size / 2
        } else {
            value
        }
    }

    fun onSwipe(): (MotionEvent) -> Boolean = { event ->
        val offset = Offset(
            x = event.x / density,
            y = event.y / density
        )
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                val ghostItem = getOffsets().getGhostItem(offset)
                if (ghostItem != null) {
                    _ghostItem.value = ghostItem
                } else {
                    if (isClickOnAdd(offset)) {
                        _ghostItem.value = getAddGhostItem(offset)
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                ghostItem.value?.let { item ->
                    val itemOffset = Offset(
                        x = offset.x + item.firstOffset.x,
                        y = offset.y + item.firstOffset.y
                    )
                    val index = getElementIndexOnCords(
                        Offset(
                            x = offset.x - size / 2,
                            y = offset.y - size / 2
                        )
                    )
                    if (index != null && item.index != index) {
                        if (item.index != null) {
                            val itemOnIndex = menu.items[index]
                            _circleMenu.value = menu.copy(
                                items = menu.items.toMutableList().apply {
                                    if (menu.items.isNotEmpty()) {
                                        this[index] = menu.items[item.index]
                                    }
                                    this[item.index] = itemOnIndex
                                }
                            )
                            _ghostItem.value = ghostItem.value?.copy(
                                offset = itemOffset,
                                index = index
                            )
                        } else {
                            _circleMenu.value = menu.copy(
                                items = menu.items.toMutableList().apply {
                                    add(
                                        index,
                                        CircleMenuItem(
                                            image = DefaultImage(DefaultImages.Build),
                                            action = OpenSettingsAction
                                        )
                                    )
                                }
                            )
                            _ghostItem.value = ghostItem.value?.copy(
                                offset = itemOffset,
                                index = index
                            )
                            itemSize = getItemSize(menu.items.size)
                            actionRadiusSq = getActionRadiusSq()
                        }
                    } else {
                        _ghostItem.value = ghostItem.value?.copy(offset = itemOffset)
                    }
                    _actionItemData.value = ActionItemData(
                        size = getActionItemSize(),
                        elementOnTop = elementOnDeleteValue(
                            Offset(
                                x = itemOffset.x - size / 2,
                                y = itemOffset.y - size / 2
                            )
                        ),
                        action = ActionItemDataType.Delete
                    )
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                ghostItem.value?.let { item ->
                    if (elementOnDeleteValue(
                            Offset(
                                x = offset.x + item.firstOffset.x - size / 2,
                                y = offset.y + item.firstOffset.y - size / 2
                            )
                        ) && item.index != null
                    ) {

                        // Delete element

                        _circleMenu.value = menu.copy(
                            items = menu.items.toMutableList().apply {
                                remove(this[item.index])
                            }
                        )

                        // Update variables
                        _ghostItem.value = null
                        itemSize = getItemSize(menu.items.size)
                        actionRadiusSq = getActionRadiusSq()

                        if (selectedBoxData.value?.index == item.index) {
                            _selectedBoxData.value = getSelectedBoxData(0)
                        } else {
                            _selectedBoxData.value =
                                getSelectedBoxData(selectedBoxData.value?.index ?: 0)
                        }
                    } else {
                        _selectedBoxData.value = getSelectedBoxData(item.index ?: 0)
                        _ghostItem.value = null
                    }
                    _actionItemData.value = ActionItemData(
                        size = getActionItemSize(),
                        elementOnTop = false,
                        action = ActionItemDataType.Add
                    )
                    updateCircleMenu()
                }
            }
        }
        true
    }

    private fun getOffsets(): List<CircleMenuItemWithOffset> {
        if (menu.items.isEmpty()) {
            return emptyList()
        }
        val alpha = 360f / menu.items.size
        val offsets = mutableListOf<CircleMenuItemWithOffset>()
        (0 until menu.items.size).map { index ->
            val item = menu.items[index]
            val angle = (alpha * index + 0.5f * alpha + startOffset) * PI / 180f
            val offset = Offset(
                x = size / 2f + sin(angle).toFloat() * (size / 2 - itemSize / 2) - itemSize / 2,
                y = size / 2f - cos(angle).toFloat() * (size / 2 - itemSize / 2) + itemSize / 2,
            )
            offsets.add(
                CircleMenuItemWithOffset(
                    index = index,
                    circleMenuItem = item,
                    xStart = offset.x,
                    xEnd = offset.x + itemSize,
                    yStart = offset.y - itemSize,
                    yEnd = offset.y
                )
            )
        }
        return offsets
    }

    private data class CircleMenuItemWithOffset(
        val index: Int,
        val circleMenuItem: CircleMenuItem,
        val xStart: Float,
        val xEnd: Float,
        val yStart: Float,
        val yEnd: Float
    )

    private fun List<CircleMenuItemWithOffset>.getGhostItem(offset: Offset): GhostCircleMenuItem? {
        forEach { item ->
            if ((item.xStart <= offset.x && offset.x <= item.xEnd) && (item.yStart <= offset.y && offset.y <= item.yEnd)) {
                val firstOffset = Offset(
                    x = (item.xStart + itemSize / 2) - offset.x,
                    y = (item.yEnd - itemSize / 2) - offset.y
                )
                return GhostCircleMenuItem(
                    index = item.index,
                    image = getItemImageUseCase.getItemImage(item.circleMenuItem.image),
                    offset = Offset(
                        x = offset.x + firstOffset.x,
                        y = offset.y + firstOffset.y
                    ),
                    firstOffset = firstOffset,
                    size = itemSize
                )
            }
        }
        return null
    }

    private fun getElementIndexOnCords(
        offset: Offset
    ): Int? {
        if (offset.x.pow(2) + offset.y.pow(2) > swipeRadiusSq) {
            if (menu.items.isEmpty()) {
                return 0
            }
            val alpha = 360f / menu.items.size
            val angles = (0 until menu.items.size).map { alpha * it }
            val currentAngle = if (offset.y == 0f) {
                ((if (offset.x > 0) 90 else 270) - startOffset) % 360
            } else {
                offset.getAngle(abs((atan(offset.x / offset.y) / PI * 180)).toFloat())
            }
            angles.forEachIndexed { index, it ->
                if (it > currentAngle) {
                    return index - 1
                }
            }
            return menu.items.size - 1
        }
        return null
    }

    private fun Offset.getAngle(angle: Float): Float {
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

    private fun elementOnDeleteValue(offset: Offset): Boolean {
        return offset.x.pow(2) + offset.y.pow(2) < actionRadiusSq
    }

    fun getItemsOffsets(): List<DpOffset> {
        val alpha = 360f / menu.items.size
        return (0 until menu.items.size).map { alpha * it }.map {
            DpOffset(
                x = (size / 2 + sin((it + 0.5f * alpha + startOffset) * PI / 180f).toFloat() * (size / 2 - itemSize / 2) - itemSize / 2).dp,
                y = (size / 2 - cos((it + 0.5f * alpha + startOffset) * PI / 180f).toFloat() * (size / 2 - itemSize / 2) - itemSize / 2).dp,
            )
        }
    }

    private fun getSelectedBoxData(
        index: Int
    ): SelectedItemBoxData? {
        if (menu.items.isEmpty()) {
            return null
        }
        val alpha = 360f / menu.items.size
        return SelectedItemBoxData(
            index = index,
            offset = Offset(
                x = (size / 2 + sin((alpha * index + 0.5f * alpha + startOffset) * PI / 180f).toFloat() * (size / 2 - itemSize / 2) - itemSize / 2) - 5,
                y = (size / 2 - cos((alpha * index + 0.5f * alpha + startOffset) * PI / 180f).toFloat() * (size / 2 - itemSize / 2) - itemSize / 2) - 5
            ),
            size = itemSize + 10
        )
    }

    private fun getStartOffset(elementsCount: Int): Float {
        if (elementsCount == 0) {
            return 0f
        }
        return -360 / elementsCount / 2f
    }

    private fun isClickOnAdd(offset: Offset): Boolean {
        return (offset.x - size / 2).pow(2) + (offset.y - size / 2).pow(2) < actionRadiusSq
    }

    private fun getAddGhostItem(offset: Offset): GhostCircleMenuItem {
        val firstOffset = Offset(
            x = size / 2 - offset.x,
            y = size / 2 - offset.y
        )
        return GhostCircleMenuItem(
            index = null,
            image = getItemImageUseCase.getItemImage(DefaultImage(DefaultImages.Build)),
            offset = Offset(
                x = offset.x + firstOffset.x,
                y = offset.y + firstOffset.y
            ),
            firstOffset = firstOffset,
            size = itemSize
        )
    }

    private fun updateCircleMenu() {
        viewModelScope.launch {
            circleMenu.value?.let {
                LauncherData.insertCircleMenu(it)
            }
        }
    }

    fun getApplicationInfo(packageName: String): ApplicationInfo {
        return applicationsUseCase.getApplicationInfo(packageName)
    }

    fun updateCircleMenuItem(item: CircleMenuItem, index: Int) = viewModelScope.launch {
        circleMenu.value?.let { circleMenu ->
            val newCircleMenu = circleMenu.copy(
                items = circleMenu.items.toMutableList()
                    .apply { this[index] = item })
            _circleMenu.value = newCircleMenu
            updateCircleMenu()
        }
    }

    fun updateImage(item: CircleMenuItem, index: Int) = viewModelScope.launch {
        circleMenu.value?.let { circleMenu ->
            var action = item.action
            if (item.image is AppImage && LauncherData.settings.value?.getValueOf(
                    SettingNames.PickAppActionWithImage,
                    PickAppActionWithImage::class.java
                )?.enabled == true
            ) {
                action = OpenAppAction(item.image.packageName)
            }
            val newCircleMenu = circleMenu.copy(
                items = circleMenu.items.toMutableList()
                    .apply { this[index] = item.copy(action = action) })
            _circleMenu.value = newCircleMenu
            updateCircleMenu()
        }
    }
}