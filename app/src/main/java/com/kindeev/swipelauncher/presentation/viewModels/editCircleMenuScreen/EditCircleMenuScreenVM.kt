package com.kindeev.swipelauncher.presentation.viewModels.editCircleMenuScreen

import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImages
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImage
import android.content.Context
import android.net.Uri
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.di.container
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenAppAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenSettingsAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.AppImage
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.UserImage
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen.entities.ActionItemData
import com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen.entities.ActionItemDataType
import com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen.entities.GhostCircleMenuItem
import com.kindeev.swipelauncher.presentation.screens.editCircleMenuScreen.entities.SelectedItemBoxData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds

class EditCircleMenuScreenVM(
    circleMenuId: Int?,
    val size: Float,
    context: Context
) : ViewModel() {

    private val container = context.container
    private val id: Int

    private val images = mutableMapOf<CircleMenuImage, ImageBitmap>()

    // CircleMenu
    private val _circleMenuItems = MutableStateFlow<List<CircleMenuItem>>(emptyList())
    val circleMenuItems: StateFlow<List<CircleMenuItem>> = _circleMenuItems.asStateFlow()

    private val _circleMenuTitle = MutableStateFlow(TextFieldValue("New"))
    val circleMenuTitle = _circleMenuTitle.asStateFlow()

    // ItemSize
    var itemSize = size / 4

    init {
        if (circleMenuId == null) {
            val allIds = container.circleMenus.value.map { it.id }
            id = generateSequence(1) { it + 1 }.first { it !in allIds }
        } else {
            val menu = container.circleMenus.value.find { it.id == circleMenuId }
            if (menu == null) {
                id = 0
            } else {
                id = menu.id
                _circleMenuItems.value = menu.items
                _circleMenuTitle.value = TextFieldValue(menu.title)
                itemSize = getItemSize(menu.items.size)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            launch {
                @OptIn(FlowPreview::class)
                _circleMenuTitle
                    .map { it.text }
                    .distinctUntilChanged()
                    .debounce(500.milliseconds)
                    .collect {
                        updateCircleMenuInDatabase()
                    }
            }
            launch {
                @OptIn(FlowPreview::class)
                _circleMenuItems
                    .debounce(500.milliseconds)
                    .collect {
                        updateCircleMenuInDatabase()
                    }
            }
        }
    }

    fun getImage(image: CircleMenuImage): ImageBitmap? {
        return images[image] ?: container.circleMenuForUIMapper.circleMenuImageToUI(image)?.apply {
            images[image] = this
        }

    }

    private fun updateCircleMenuInDatabase() =
        viewModelScope.launch(Dispatchers.IO) {
            container.dataRepository.insertCircleMenu(
                CircleMenu(
                    id = id,
                    title = circleMenuTitle.value.text,
                    items = circleMenuItems.value
                )
            )
        }

    fun changeTitle(value: TextFieldValue) {
        _circleMenuTitle.value = value
    }

    val startOffset: Float
        get() = getStartOffset(circleMenuItems.value.size)

    // SelectedBoxItem
    private val _selectedBoxData = MutableStateFlow(getSelectedBoxData(0))
    val selectedBoxData: StateFlow<SelectedItemBoxData?> = _selectedBoxData

    // GhostItem
    private val _ghostItem = MutableStateFlow<GhostCircleMenuItem?>(null)
    val ghostItem: StateFlow<GhostCircleMenuItem?> = _ghostItem

    // Radius
    private var actionRadiusSq = getActionRadiusSq()
    private val swipeRadiusSq = (size / 2 - itemSize / 2).pow(2)

    // Density
    private val density = context.resources.displayMetrics.density

    // ActionItemData
    private val _actionItemData = MutableStateFlow<ActionItemData?>(
        ActionItemData(
            size = getActionItemSize(),
            elementOnTop = false,
            action = ActionItemDataType.Add
        )
    )
    val actionItemData: StateFlow<ActionItemData?> = _actionItemData

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

    fun getImageBitmap(circleMenuImage: CircleMenuImage): ImageBitmap? =
        container.circleMenuForUIMapper.circleMenuImageToUI(circleMenuImage)

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
                            val itemOnIndex = circleMenuItems.value[index]
                            _circleMenuItems.value = circleMenuItems.value.toMutableList().apply {
                                if (isNotEmpty()) {
                                    this[index] = this[item.index]
                                }
                                this[item.index] = itemOnIndex
                            }
                            _ghostItem.value = ghostItem.value?.copy(
                                offset = itemOffset,
                                index = index
                            )
                        } else {
                            _circleMenuItems.value = circleMenuItems.value.toMutableList().apply {
                                add(
                                    index,
                                    CircleMenuItem(
                                        image = DefaultImage(DefaultImages.Build),
                                        action = OpenSettingsAction
                                    )
                                )
                            }
                            _ghostItem.value = ghostItem.value?.copy(
                                offset = itemOffset,
                                index = index
                            )
                            itemSize = getItemSize(circleMenuItems.value.size)
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
                        val oldImage = _circleMenuItems.value[item.index].image
                        _circleMenuItems.value = circleMenuItems.value.toMutableList().apply {
                            remove(this[item.index])
                        }
                        viewModelScope.launch(Dispatchers.IO) {
                            if (oldImage !in circleMenuItems.value.map { it.image }) {
                                images.remove(oldImage)
                            }
                        }


                        // Update variables
                        _ghostItem.value = null
                        itemSize = getItemSize(circleMenuItems.value.size)
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
                }
            }
        }
        true
    }

    private fun getOffsets(): List<CircleMenuItemWithOffset> {
        if (circleMenuItems.value.isEmpty()) {
            return emptyList()
        }
        val alpha = 360f / circleMenuItems.value.size
        val offsets = mutableListOf<CircleMenuItemWithOffset>()
        circleMenuItems.value.indices.forEach { index ->
            val item = circleMenuItems.value[index]
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
                    image = getImage(item.circleMenuItem.image) ?: return null,
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
            if (circleMenuItems.value.isEmpty()) {
                return 0
            }
            val alpha = 360f / circleMenuItems.value.size
            val angles = circleMenuItems.value.indices.map { alpha * it }
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
            return circleMenuItems.value.size - 1
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
        val alpha = 360f / circleMenuItems.value.size
        return circleMenuItems.value.indices.map { alpha * it }.map {
            DpOffset(
                x = (size / 2 + sin((it + 0.5f * alpha + startOffset) * PI / 180f).toFloat() * (size / 2 - itemSize / 2) - itemSize / 2).dp,
                y = (size / 2 - cos((it + 0.5f * alpha + startOffset) * PI / 180f).toFloat() * (size / 2 - itemSize / 2) - itemSize / 2).dp,
            )
        }
    }

    private fun getSelectedBoxData(
        index: Int
    ): SelectedItemBoxData? {
        if (circleMenuItems.value.isEmpty()) {
            return null
        }
        val alpha = 360f / circleMenuItems.value.size
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

    private fun getAddGhostItem(offset: Offset): GhostCircleMenuItem? {
        val firstOffset = Offset(
            x = size / 2 - offset.x,
            y = size / 2 - offset.y
        )
        return GhostCircleMenuItem(
            index = null,
            image = getImage(DefaultImage(DefaultImages.Build)) ?: return null,
            offset = Offset(
                x = offset.x + firstOffset.x,
                y = offset.y + firstOffset.y
            ),
            firstOffset = firstOffset,
            size = itemSize
        )
    }

    fun getApplicationInfo(packageName: String): ApplicationInfo? {
        return container.applicationsManager.getApplication(packageName)
    }

    fun updateCircleMenuItem(item: CircleMenuItem, index: Int) = viewModelScope.launch {
        _circleMenuItems.value = circleMenuItems.value.toMutableList().apply {
            this[index] = item
        }
    }

    fun updateImage(item: CircleMenuItem, index: Int) = viewModelScope.launch {
        val oldImage = circleMenuItems.value[index].image
        if (container.settings.value.pickAppActionWithImage && item.image is AppImage) {
            _circleMenuItems.value = circleMenuItems.value.toMutableList().apply {
                this[index] = item.copy(
                    action = OpenAppAction(
                        item.image.packageName
                    )
                )
            }
        } else {
            _circleMenuItems.value = circleMenuItems.value.toMutableList().apply {
                this[index] = item.copy()
            }
        }
        if (oldImage !in circleMenuItems.value.map { it.image }) {
            images.remove(oldImage)
        }
    }

    suspend fun addUserImage(uri: Uri): UserImage? = withContext(Dispatchers.IO) {
        container.userImagesRepository.insert(uri = uri)?.let { UserImage(it) }
    }
}