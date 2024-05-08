package com.kindeev.swipelauncher.presentation.ui.elements.editImageAndAction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.CircleMenuItem
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.getActionType
import com.kindeev.swipelauncher.domain.getImageType
import com.kindeev.swipelauncher.domain.getMinScreenLengthDp
import com.kindeev.swipelauncher.domain.getMinScreenLengthSp
import com.kindeev.swipelauncher.presentation.entities.ActionType
import com.kindeev.swipelauncher.presentation.entities.ImageType
import com.kindeev.swipelauncher.presentation.ui.dialogs.ActionDialog
import com.kindeev.swipelauncher.presentation.ui.dialogs.ImageDialog

@Composable
fun ImageAndAction(
    circleMenuItem: CircleMenuItem,
    width: Dp = Constants.minScreenLength.dp / 9 * 8,
    onChangeAction: (CircleMenuAction) -> Unit,
    onChangeImage: (CircleMenuImage) -> Unit
) {
    Column(
        modifier = Modifier
            .width(width)
            .clip(RoundedCornerShape(7.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        ImageBox(image = circleMenuItem.image, onChangeImage = onChangeImage)
        Divider(modifier = Modifier.padding(horizontal = 10.dp))
        ActionBox(action = circleMenuItem.action, onChangeAction = onChangeAction)
    }
}

@Composable
private fun ImageBox(
    image: CircleMenuImage,
    onChangeImage: (CircleMenuImage) -> Unit
) {
    var showImage by rememberSaveable {
        mutableStateOf(true)
    }
    val dropdownArrowRotation by animateFloatAsState(
        targetValue = if (showImage) 0f else 180f,
        label = ""
    )
    Row(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { showImage = !showImage },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = stringResource(id = R.string.image),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = Constants.minScreenLength.sp / 20
        )
        Spacer(modifier = Modifier.width(3.dp))
        Icon(
            modifier = Modifier
                .size(Constants.minScreenLength.dp / 10)
                .rotate(dropdownArrowRotation),
            tint = MaterialTheme.colorScheme.onPrimary,
            imageVector = Icons.Rounded.ArrowDropDown,
            contentDescription = "Show or hide image data"
        )
    }
    AnimatedVisibility(
        visible = showImage,
        enter = fadeIn() + expandVertically(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column {
            ImageTypeItem(
                imageType = image.type.getImageType()
                    ?: throw IllegalAccessException(
                        "Illegal image type"
                    ),
                onChangeImage = onChangeImage
            )
            ImageDataByType(image = image, onChangeImage = onChangeImage)
        }
    }
}

@Composable
private fun ActionBox(
    action: CircleMenuAction,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    var showAction by rememberSaveable {
        mutableStateOf(true)
    }
    val dropdownArrowRotation by animateFloatAsState(
        targetValue = if (showAction) 0f else 180f,
        label = ""
    )
    Row(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { showAction = !showAction },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = stringResource(id = R.string.action),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = Constants.minScreenLength.sp / 20
        )
        Spacer(modifier = Modifier.width(3.dp))
        Icon(
            modifier = Modifier
                .size(Constants.minScreenLength.dp / 10)
                .rotate(dropdownArrowRotation),
            tint = MaterialTheme.colorScheme.onPrimary,
            imageVector = Icons.Rounded.ArrowDropDown,
            contentDescription = "Show or hide action data"
        )
    }
    AnimatedVisibility(
        visible = showAction,
        enter = fadeIn() + expandVertically(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column {
            ActionTypeItem(
                actionType = action.type.getActionType() ?: throw IllegalAccessException(
                    "Illegal action type"
                ),
                onChangeAction = onChangeAction
            )
            ActionDataByType(action = action, onChangeAction = onChangeAction)
        }
    }
}

@Composable
fun ActionTypeItem(
    actionType: ActionType,
    onChangeAction: (CircleMenuAction) -> Unit
) {
    var showActionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showActionDialog) {
        ActionDialog(
            onDismissRequest = { showActionDialog = false },
            onPick = onChangeAction
        )
    }

    val minScreenLength = getMinScreenLengthDp()
    Row(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .clickable { showActionDialog = true }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(minScreenLength / 8),
            painter = painterResource(id = actionType.imageResId),
            contentDescription = "Action type image"
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = actionType.name,
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = getMinScreenLengthSp() / 25
        )
    }
}

@Composable
private fun ImageTypeItem(
    imageType: ImageType,
    onChangeImage: (CircleMenuImage) -> Unit
) {
    var showImageDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showImageDialog) {
        ImageDialog(
            onDismissRequest = { showImageDialog = false },
            onPick = onChangeImage
        )
    }
    val minScreenLength = getMinScreenLengthDp()
    Row(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .clickable { showImageDialog = true }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(minScreenLength / 8),
            painter = painterResource(id = imageType.imageResId),
            contentDescription = "Image type image"
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = imageType.name,
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = getMinScreenLengthSp() / 25
        )
    }
}