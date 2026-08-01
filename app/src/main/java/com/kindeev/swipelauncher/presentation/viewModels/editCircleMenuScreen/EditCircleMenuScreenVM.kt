package com.kindeev.swipelauncher.presentation.viewModels.editCircleMenuScreen

import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImages
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.DefaultImage
import android.view.MotionEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.data.applications.ApplicationsManager
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.circleMenu.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenAppAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.OpenSettingsAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.AppImage
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuAction.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenu.circleMenuItem.circleMenuImage.CircleMenuImage
import com.kindeev.swipelauncher.domain.useCases.SaveCircleMenuWithDebounceUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.CircleMenuStateFlowUseCase
import com.kindeev.swipelauncher.domain.useCases.stateFlows.SettingsStateFlowUseCase
import com.kindeev.swipelauncher.presentation.entities.CircleMenuItemToDraw
import com.kindeev.swipelauncher.presentation.entities.CircleMenuToDraw
import com.kindeev.swipelauncher.presentation.entities.CircleMenuToDrawParameters
import com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen.entities.ActionItemData
import com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen.entities.ActionItemState
import com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen.entities.GhostCircleMenuItem
import com.kindeev.swipelauncher.presentation.ui.screens.settings.editCircleMenuScreen.entities.SelectedItemBoxData
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuImageToImageBitmapUseCase
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuItemIndexOnCordsUseCase
import com.kindeev.swipelauncher.presentation.useCases.CircleMenuParametersUseCase
import com.kindeev.swipelauncher.presentation.useCases.menuParameters.corsOutRadiusGenerator
import com.kindeev.swipelauncher.presentation.useCases.menuParameters.getSwipeRadius
import com.kindeev.swipelauncher.presentation.useCases.menuParameters.makeCircleMenuParametersGenerator
import com.kindeev.swipelauncher.presentation.viewModels.editCircleMenuScreen.entities.ItemBorders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.filterNotNull
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class EditCircleMenuScreenVM(
    private val circleMenuStateFlowUseCase: CircleMenuStateFlowUseCase,
    private val circleMenuParametersUseCase: CircleMenuParametersUseCase,
    private val circleMenuItemIndexOnCordsUseCase: CircleMenuItemIndexOnCordsUseCase,
    private val circleMenuImageToImageBitmapUseCase: CircleMenuImageToImageBitmapUseCase,
    private val saveCircleMenuWithDebounceUseCase: SaveCircleMenuWithDebounceUseCase,
    private val applicationsManager: ApplicationsManager,
    private val settingsStateFlowUseCase: SettingsStateFlowUseCase,
    private val density: Float,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _menuSize = MutableStateFlow(Constants.minScreenLength / 6 * 5f)
    val menuSize: StateFlow<Float> = _menuSize.asStateFlow()

    private val cordsOutRadius =
        menuSize.map { menuSize -> corsOutRadiusGenerator(menuSize, ::getSwipeRadius) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = { _ -> false }
        )

    private val id: Int

    private val images = mutableMapOf<CircleMenuImage, ImageBitmap>()

    // CircleMenu
    private val _circleMenuItems = MutableStateFlow<List<CircleMenuItem>>(emptyList())
    val circleMenuItems: StateFlow<List<CircleMenuItem>> = _circleMenuItems.asStateFlow()

    private val drawItemsData = circleMenuItems
        .map { it.size }
        .distinctUntilChanged()
        .combine(menuSize) { itemsCount, menuSize ->
            makeCircleMenuParametersGenerator(itemsCount)(menuSize)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CircleMenuToDrawParameters(emptyMap(), 0f)
        )

    private val itemIndexOnCords = drawItemsData.map { data ->
        circleMenuItemIndexOnCordsUseCase.getItemIndexOnCordsGenerator(data.offsets.size)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = { _ -> 0 }
    )
    private val _circleMenuTitle = MutableStateFlow(TextFieldValue("New"))
    val circleMenuTitle = _circleMenuTitle.asStateFlow()

    private val circleMenuTitleText =
        circleMenuTitle.map { it.text }.distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ""
        )

    val circleMenuToDraw =
        combine(circleMenuItems, circleMenuTitleText, menuSize) { items, title, menuSize ->
            val parameters =
                circleMenuParametersUseCase.getParametersGenerator(items.size)(menuSize)
            CircleMenuToDraw(
                id = id,
                title = title,
                menuSize = menuSize,
                itemSize = parameters.itemSize,
                items = items.mapIndexed { index, item ->
                    parameters.offsets[index]?.let { offset ->
                        getImageBitmap(item.image)?.let { imageBitmap ->
                            CircleMenuItemToDraw(
                                offset = offset,
                                imageBitmap = imageBitmap
                            )
                        }
                    }
                }.filterNotNull()
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CircleMenuToDraw(0, "", 0f, 0f, emptyList())
        )

    fun updateMenuSize(menuSize: Float) {
        _menuSize.value = menuSize
    }

    fun getCircleMenuToDrawForEditAction(id: Int): CircleMenuToDraw? {
        val menuSize = menuSize.value / 5 - 10
        val imageMapper = circleMenuImageToImageBitmapUseCase.mapper.value
        return circleMenuStateFlowUseCase.circleMenus.value.find { it.id == id }?.let { menu ->
            val parameters =
                circleMenuParametersUseCase.getParametersGenerator(menu.items.size)(menuSize)
            CircleMenuToDraw(
                id = menu.id,
                title = menu.title,
                menuSize = menuSize,
                itemSize = parameters.itemSize,
                items = menu.items.mapIndexed { index, item ->
                    parameters.offsets[index]?.let { offset ->
                        imageMapper[item.image]?.let { imageBitmap ->
                            CircleMenuItemToDraw(
                                offset = offset,
                                imageBitmap = imageBitmap
                            )
                        }
                    }
                }.filterNotNull()
            )
        }
    }

    private val itemBorders = drawItemsData.combine(menuSize) { data, menuSize ->
        if (data.offsets.isEmpty()) {
            emptyList()
        } else {
            val count = data.offsets.size
            val alpha = 360f / count
            (0 until count).map {
                ((alpha * (0.5f + it) + -360 / data.offsets.size / 2f) * PI / 180f).let { angle ->
                    val x =
                        menuSize / 2f + sin(angle).toFloat() * (menuSize / 2 - data.itemSize / 2) - data.itemSize / 2
                    val y =
                        menuSize / 2f - cos(angle).toFloat() * (menuSize / 2 - data.itemSize / 2) + data.itemSize / 2

                    ItemBorders(
                        xStart = x,
                        xEnd = x + data.itemSize,
                        yStart = y - data.itemSize,
                        yEnd = y
                    )
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    var needSave = false

    private fun saveCircleMenu() {
        saveCircleMenuWithDebounceUseCase.save(
            CircleMenu(
                id = id,
                title = circleMenuTitleText.value,
                items = circleMenuItems.value
            )
        )
    }

    private fun cancelSaving() {
        saveCircleMenuWithDebounceUseCase.cancel()
    }

    init {
        val circleMenuId = savedStateHandle.get<Int>("circleMenuId")
        if (circleMenuId == null) {
            val allIds = circleMenuStateFlowUseCase.circleMenus.value.map { it.id }
            id = generateSequence(1) { it + 1 }.first { it !in allIds }
        } else {
            val menu = circleMenuStateFlowUseCase.circleMenus.value.find { it.id == circleMenuId }
            if (menu == null) {
                id = 0
            } else {
                id = menu.id
                _circleMenuItems.value = menu.items
                _circleMenuTitle.value = TextFieldValue(menu.title)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                circleMenuTitleText
                    .drop(1)
                    .collect {
                        saveCircleMenu()
                    }
            }
            launch {
                savedStateHandle.getStateFlow<CircleMenuAction?>("pickedAction", null)
                    .filterNotNull().collect { action ->
                        updateAction(action)
                        savedStateHandle["pickedAction"] = null
                    }
            }
            launch {
                savedStateHandle.getStateFlow<CircleMenuImage?>("pickedImage", null)
                    .filterNotNull().collect { image ->
                        updateImage(image)
                        savedStateHandle["pickedImage"] = null
                    }
            }
        }
    }

    fun getImageBitmap(image: CircleMenuImage): ImageBitmap? {
        return images[image] ?: circleMenuImageToImageBitmapUseCase.getImageBitmap(image)?.apply {
            images[image] = this
        }
    }

    fun changeTitle(value: TextFieldValue) {
        _circleMenuTitle.value = value
    }

    // SelectedBoxItem
    private val selectedIndex = MutableStateFlow(0)
    val selectedBoxData = selectedIndex.combine(drawItemsData) { index, data ->
        if (data.offsets.isEmpty()) {
            null
        } else {
            val offsets = data.offsets[index]
            if (offsets == null) {
                selectedIndex.value = 0
                null
            } else {
                SelectedItemBoxData(
                    index = index,
                    offset = Offset(
                        x = offsets.x - 5,
                        y = offsets.y - 5
                    ),
                    size = data.itemSize + 10
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val selectedItem = selectedIndex.combine(circleMenuItems) { index, items ->
        items.getOrNull(index)
    }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    // GhostItem
    private val _ghostItem = MutableStateFlow<GhostCircleMenuItem?>(null)
    val ghostItem: StateFlow<GhostCircleMenuItem?> = _ghostItem

    // ActionItemData
    private val actionItemSize = menuSize.map { menuSize ->
        menuSize / 4
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0f
    )

    private val actionItemState = MutableStateFlow<ActionItemState>(ActionItemState.Add)
    val actionItemData = actionItemState.combine(actionItemSize) { state, size ->
        ActionItemData(
            size = size,
            state = state
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ActionItemData(menuSize.value / 4, ActionItemState.Add)
    )

    // Radius
    private val actionRadiusSq = actionItemSize.map {
        it.pow(2)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = 0f
    )

    fun onSwipe(): (MotionEvent) -> Boolean = { event ->
        val offset = Offset(
            x = event.x / density,
            y = event.y / density
        )
        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                needSave = saveCircleMenuWithDebounceUseCase.isActive()
                if (needSave) cancelSaving()
                val ghostItem = getGhostItem(offset)
                if (ghostItem != null) {
                    _ghostItem.value = ghostItem
                } else {
                    if (isClickOnAdd(offset)) {
                        _ghostItem.value = getAddGhostItem(offset)
                        actionItemState.value = ActionItemState.DeleteActive
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                ghostItem.value?.let { item ->
                    val itemOffset = Offset(
                        x = offset.x + item.firstOffset.x,
                        y = offset.y + item.firstOffset.y
                    )
                    val swipeOffset = Offset(
                        x = offset.x - menuSize.value / 2,
                        y = offset.y - menuSize.value / 2
                    )
                    if (cordsOutRadius.value(swipeOffset)) {
                        val index = itemIndexOnCords.value(swipeOffset)
                        if (index != item.index) {
                            needSave = true
                            _ghostItem.value = ghostItem.value?.copy(
                                offset = itemOffset,
                                index = index
                            )
                            if (item.index != null) {
                                _circleMenuItems.value =
                                    circleMenuItems.value.toMutableList().apply {
                                        if (isNotEmpty()) {
                                            val indexItem = this[index]
                                            this[index] = this[item.index]
                                            this[item.index] = indexItem
                                        }
                                    }
                                selectedIndex.value = index
                            } else {
                                _circleMenuItems.value =
                                    circleMenuItems.value.toMutableList().apply {
                                        add(
                                            index,
                                            CircleMenuItem(
                                                image = DefaultImage(DefaultImages.Build),
                                                action = OpenSettingsAction
                                            )
                                        )
                                    }
                            }
                        } else {
                            _ghostItem.value = ghostItem.value?.copy(offset = itemOffset)
                        }
                    } else {
                        _ghostItem.value = ghostItem.value?.copy(offset = itemOffset)
                    }
                    if (elementOnDeleteValue(
                            Offset(
                                x = itemOffset.x - menuSize.value / 2,
                                y = itemOffset.y - menuSize.value / 2
                            )
                        )
                    ) {
                        actionItemState.value = ActionItemState.DeleteActive
                    } else {
                        actionItemState.value = ActionItemState.Delete
                    }
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                ghostItem.value?.let { item ->
                    if (elementOnDeleteValue(
                            Offset(
                                x = offset.x + item.firstOffset.x - menuSize.value / 2,
                                y = offset.y + item.firstOffset.y - menuSize.value / 2
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
                        needSave = true

                    } else {
                        _ghostItem.value = null
                        item.index?.let { selectedIndex.value = it }
                    }
                }
                actionItemState.value = ActionItemState.Add
                if (needSave) {
                    saveCircleMenu()
                    needSave = false
                }
            }
        }
        true
    }

    private fun getGhostItem(offset: Offset): GhostCircleMenuItem? {
        itemBorders.value.forEachIndexed { index, borders ->
            if ((borders.xStart <= offset.x && offset.x <= borders.xEnd) && (borders.yStart <= offset.y && offset.y <= borders.yEnd)) {
                val firstOffset = Offset(
                    x = (borders.xStart + drawItemsData.value.itemSize / 2) - offset.x,
                    y = (borders.yEnd - drawItemsData.value.itemSize / 2) - offset.y
                )
                return GhostCircleMenuItem(
                    index = index,
                    image = getImageBitmap(circleMenuItems.value[index].image) ?: return null,
                    offset = Offset(
                        x = offset.x + firstOffset.x,
                        y = offset.y + firstOffset.y
                    ),
                    firstOffset = firstOffset,
                    size = drawItemsData.value.itemSize
                )
            }
        }
        return null
    }

    private fun elementOnDeleteValue(offset: Offset): Boolean {
        return offset.x.pow(2) + offset.y.pow(2) < actionRadiusSq.value
    }

    private fun isClickOnAdd(offset: Offset): Boolean {
        return (offset.x - menuSize.value / 2).pow(2) + (offset.y - menuSize.value / 2).pow(2) < actionRadiusSq.value
    }

    private fun getAddGhostItem(offset: Offset): GhostCircleMenuItem? {
        val firstOffset = Offset(
            x = menuSize.value / 2 - offset.x,
            y = menuSize.value / 2 - offset.y
        )
        return GhostCircleMenuItem(
            index = null,
            image = getImageBitmap(DefaultImage(DefaultImages.Build)) ?: return null,
            offset = Offset(
                x = offset.x + firstOffset.x,
                y = offset.y + firstOffset.y
            ),
            firstOffset = firstOffset,
            size = drawItemsData.value.itemSize
        )
    }

    fun getApplicationInfo(packageName: String): ApplicationInfo? {
        return applicationsManager.getApplication(packageName)
    }

    fun updateAction(action: CircleMenuAction) = viewModelScope.launch {
        _circleMenuItems.value = circleMenuItems.value.toMutableList().apply {
            selectedItem.value?.let {
                this[selectedIndex.value] = it.copy(action = action)
            }
        }
        saveCircleMenu()
    }

    fun updateImage(image: CircleMenuImage) = viewModelScope.launch {
        selectedIndex.value.let { index ->
            selectedItem.value?.let { item ->
                val oldImage = circleMenuItems.value[index].image
                if (settingsStateFlowUseCase.settings.value.pickAppActionWithImage && image is AppImage) {
                    _circleMenuItems.value = circleMenuItems.value.toMutableList().apply {
                        this[index] = CircleMenuItem(
                            image = image,
                            action = OpenAppAction(
                                image.packageName
                            )
                        )
                    }
                } else {
                    _circleMenuItems.value = circleMenuItems.value.toMutableList().apply {
                        this[index] = item.copy(
                            image = image
                        )
                    }
                }
                if (oldImage !in circleMenuItems.value.map { it.image }) {
                    images.remove(oldImage)
                }
            }
        }
        saveCircleMenu()
    }

}