package com.kindeev.swipelauncher.presentation.ui.dialogs

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.kindeev.swipelauncher.data.coil.appImageUri
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.presentation.ui.elements.MaterialIcon
import com.kindeev.swipelauncher.presentation.viewModels.launcherScreen.LauncherScreenVM

@Composable
fun ApplicationInfoDialog(
    viewModel: LauncherScreenVM,
    applicationInfo: ApplicationInfo,
    onDismissRequest: () -> Unit,
) {
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
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                AsyncImage(
                    model = appImageUri(applicationInfo.packageName),
                    modifier = Modifier
                        .size(Constants.minScreenLength.dp / 7 + 10.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .padding(5.dp),
                    contentDescription = "Application Image"
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    modifier = Modifier
                        .weight(1f),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    text = applicationInfo.title
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
                            .clickable { viewModel.getAppDetails(applicationInfo.packageName) },
                        contentAlignment = Alignment.Center
                    ) {
                        MaterialIcon(
                            modifier = Modifier
                                .size(24.dp),
                            unicode = "\ue88e",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(Constants.minScreenLength.dp / 10)
                            .clip(RoundedCornerShape(7.dp))
                            .background(MaterialTheme.colorScheme.error)
                            .clickable {
                                viewModel.deleteApp(applicationInfo.packageName)
                                onDismissRequest()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        MaterialIcon(
                            modifier = Modifier
                                .size(24.dp),
                            unicode = "\ue872",
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }
        }
    }
}