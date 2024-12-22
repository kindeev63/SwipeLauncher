package com.kindeev.swipelauncher.presentation.ui.dialogs

import androidx.compose.runtime.getValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.viewModels.screens.launcherScreen.LauncherScreenVM

@Composable
fun ApplicationInfoDialog(
    viewModel: LauncherScreenVM,
    packageName: String,
    onDismissRequest: () -> Unit,
) {
    val firstApplicationData = rememberSaveable { viewModel.getApplicationData(packageName) }
    var appData by rememberSaveable { mutableStateOf(firstApplicationData) }
    var imageDialogVisibility by rememberSaveable { mutableStateOf(false) }
    if (imageDialogVisibility) {
        ImageDialog(
            onDismissRequest = { imageDialogVisibility = false },
            onPick = { appData = appData.copy(image = it)}
        )
    }
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .width(Constants.minScreenLength.dp - 20.dp)
                .heightIn(max = (Constants.minScreenLength / 3 * 2).dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                Image(
                    modifier = Modifier
                        .size(Constants.minScreenLength.dp / 7 + 10.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .clickable { imageDialogVisibility = true }
                        .padding(5.dp),
                    bitmap = viewModel.getItemImage(appData.image)
                        ?: throw IllegalArgumentException("Illegal image"),
                    contentDescription = "Application Image"
                )
                Spacer(modifier = Modifier.width(15.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color(0xFFE3E3E3))
                        .padding(horizontal = 5.dp, vertical = 2.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 20.sp
                        ),
                        value = appData.title,
                        onValueChange = { appData = appData.copy(title = it) }
                    )
                }
                Spacer(modifier = Modifier.width(15.dp))
                Image(
                    modifier = Modifier
                        .size(Constants.minScreenLength.dp / 10)
                        .clip(CircleShape)
                        .clickable { appData = viewModel.getNotMaskApplicationData(packageName) }
                        .padding(5.dp),
                    painter = painterResource(id = R.drawable.reset_image),
                    contentDescription = "Reset"
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Box(
                        modifier = Modifier
                            .size(Constants.minScreenLength.dp / 10)
                            .clip(RoundedCornerShape(7.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { viewModel.getAppDetails(packageName) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(Constants.minScreenLength.dp / 12),
                            painter = painterResource(id = R.drawable.info_image),
                            contentDescription = "App info"
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(Constants.minScreenLength.dp / 10)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color.Red)
                            .clickable {
                                viewModel.deleteApp(packageName)
                                onDismissRequest()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(Constants.minScreenLength.dp / 12),
                            painter = painterResource(id = R.drawable.delete_image),
                            contentDescription = "Delete app"
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(Constants.minScreenLength.dp / 10)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color.Green)
                            .clickable { appData = appData.copy(hidden = !appData.hidden) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(Constants.minScreenLength.dp / 12),
                            painter = painterResource(id = if (appData.hidden) R.drawable.hidden_image else R.drawable.showed_image),
                            contentDescription = "Hide app"
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (appData != firstApplicationData) {
                        Box(
                            modifier = Modifier
                                .height(Constants.minScreenLength.dp / 10)
                                .clip(RoundedCornerShape(7.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable {
                                    viewModel.changeApp(appData)
                                    onDismissRequest()
                                }
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.save),
                                color = MaterialTheme.colorScheme.onPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .height(Constants.minScreenLength.dp / 10)
                                .clip(RoundedCornerShape(7.dp))
                                .background(Color(0xFFBDBDBD))
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.save),
                                color = Color(0xFF686868),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}