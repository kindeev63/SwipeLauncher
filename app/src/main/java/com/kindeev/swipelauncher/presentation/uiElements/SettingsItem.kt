package com.kindeev.swipelauncher.presentation.uiElements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.DataObject
import com.kindeev.swipelauncher.domain.DataObject.getAs
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuAction
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.actionTypes.OpenApp
import com.kindeev.swipelauncher.presentation.uiElements.dialogs.PickActionDialogWithoutOpenCircleMenu

@Composable
fun SwitchSettingsItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                onClick = {
                    onCheckedChange(!checked)
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            style = TextStyle(
                lineHeightStyle = LineHeightStyle(
                    trim = LineHeightStyle.Trim.None,
                    alignment = LineHeightStyle.Alignment.Center
                )
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(5.dp))
        Switch(
            checked = checked,
            onCheckedChange = null
        )
        Spacer(modifier = Modifier.width(5.dp))
    }
}

@Composable
fun ClickableSettingsItem(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            style = TextStyle(
                lineHeightStyle = LineHeightStyle(
                    trim = LineHeightStyle.Trim.None,
                    alignment = LineHeightStyle.Alignment.Center
                )
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SwitchAndActionSettingsItem(
    text: String,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onActionChange: (CircleMenuAction) -> Unit,
    circleMenuAction: CircleMenuAction?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    onClick = {
                        onCheckedChange(!enabled)
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = text,
                style = TextStyle(
                    lineHeightStyle = LineHeightStyle(
                        trim = LineHeightStyle.Trim.None,
                        alignment = LineHeightStyle.Alignment.Center
                    )
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(5.dp))
            Switch(
                checked = enabled,
                onCheckedChange = onCheckedChange
            )
            Spacer(modifier = Modifier.width(5.dp))
        }
        if (enabled) {
            var openDialog by remember {
                mutableStateOf(false)
            }
            if (openDialog) {
                PickActionDialogWithoutOpenCircleMenu(
                    onDismissRequest = { openDialog = false },
                    picked = circleMenuAction ?: CircleMenuAction(CircleMenuActionTypes.OpenSettings),
                    onPick = {
                        onActionChange(it)
                        openDialog = false
                    }
                )
            }
            val allApplicationData = DataObject.allApplicationData.observeAsState(emptyList())
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.action) + ":",
                    color = MaterialTheme.colorScheme.onBackground
                )
                when (circleMenuAction?.type) {

                    CircleMenuActionTypes.OpenApp -> {
                        val openApp = circleMenuAction.data.getAs(OpenApp::class.java)
                        val painter = when (val applicationData =
                            allApplicationData.value.find { it.packageName == openApp.packageName }) {
                            null -> {
                                val context = LocalContext.current
                                val applicationInfo =
                                    context.packageManager.getApplicationInfo(openApp.packageName, 0)
                                val imageBitmap =
                                    applicationInfo.loadIcon(context.packageManager).toBitmap()
                                        .asImageBitmap()
                                remember(imageBitmap) {
                                    BitmapPainter(
                                        imageBitmap,
                                        filterQuality = DrawScope.DefaultFilterQuality
                                    )
                                }
                            }

                            else -> {
                                val imageBitmap = applicationData.icon
                                remember(imageBitmap) {
                                    BitmapPainter(
                                        imageBitmap,
                                        filterQuality = DrawScope.DefaultFilterQuality
                                    )
                                }
                            }
                        }
                        Image(
                            modifier = Modifier
                                .size((LocalConfiguration.current.screenWidthDp / 6).dp)
                                .clickable {
                                    openDialog = true
                                },
                            painter = painter,
                            contentDescription = null
                        )

                    }

                    CircleMenuActionTypes.OpenSettings -> {
                        Image(
                            modifier = Modifier
                                .size((LocalConfiguration.current.screenWidthDp / 6).dp)
                                .clickable {
                                    openDialog = true
                                },
                            painter = painterResource(id = R.drawable.ic_settings),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                            contentDescription = null
                        )
                    }

                    else -> {
                        Image(
                            modifier = Modifier
                                .size((LocalConfiguration.current.screenWidthDp / 6).dp)
                                .clickable {
                                    openDialog = true
                                },
                            painter = painterResource(
                                id =
                                DataObject.CircleMenuDataObject.defaultImages[DataObject.otherActionsList.find { it.type == circleMenuAction?.type }?.image]
                                    ?: R.drawable.ic_error
                            ),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                            contentDescription = null
                        )
                    }
                }
            }
        }

    }

}