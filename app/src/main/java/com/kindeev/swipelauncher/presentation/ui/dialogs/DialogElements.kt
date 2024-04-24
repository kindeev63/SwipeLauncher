package com.kindeev.swipelauncher.presentation.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.LauncherData
import com.kindeev.swipelauncher.domain.addUserImage
import com.kindeev.swipelauncher.domain.createBitmap
import com.kindeev.swipelauncher.domain.entities.CircleMenu
import com.kindeev.swipelauncher.domain.entities.circleMenuActions.CircleMenuActionTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.CircleMenuImageTypes
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.DefaultImage
import com.kindeev.swipelauncher.domain.entities.circleMenuImages.imageTypes.UserImage
import com.kindeev.swipelauncher.domain.entities.dialogTabs.DialogTab
import com.kindeev.swipelauncher.domain.entities.dialogTabs.OtherAction
import com.kindeev.swipelauncher.domain.getItemImage
import com.kindeev.swipelauncher.presentation.ui.elements.AppItem
import com.kindeev.swipelauncher.presentation.ui.elements.MiniCircleMenuItem
import com.kindeev.swipelauncher.presentation.ui.elements.OtherActionItem


@Composable
fun DialogTabs(
    tabs: List<DialogTab>,
    selectedTab: DialogTab,
    onSelectTab: (DialogTab) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        edgePadding = 0.dp
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = selectedTab.nameResourceId == tab.nameResourceId,
                onClick = {
                    if (selectedTab != tab) onSelectTab(tab)
                },
                text = {
                    Text(
                        text = stringResource(id = tab.nameResourceId),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            )
        }
    }
}

@Composable
fun PickAppTabContent(
    pickedPackageName: String?,
    onPick: (String) -> Unit
) {
    val allApplicationData = LauncherData.allApplicationData.observeAsState(emptyList())
    var searchText by rememberSaveable {
        mutableStateOf("")
    }
    Column {
        Spacer(modifier = Modifier.height(5.dp))
        SearchElement(searchText = searchText, onTextChange = { searchText = it })
        Spacer(modifier = Modifier.height(5.dp))
        LazyColumn {
            items(
                items = allApplicationData.value.filter {
                    it.name.lowercase().contains(searchText.lowercase())
                },
                key = { it.packageName }
            ) { applicationData ->
                AppItem(
                    applicationData = applicationData,
                    picked = applicationData.packageName == pickedPackageName
                ) {
                    onPick(applicationData.packageName)
                }
            }
        }
    }
}

@Composable
fun PickCircleMenuTabContent(
    pickedId: Int?,
    onPick: (CircleMenu) -> Unit
) {
    var searchText by rememberSaveable {
        mutableStateOf("")
    }
    Column {
        Spacer(modifier = Modifier.height(5.dp))
        SearchElement(searchText = searchText, onTextChange = { searchText = it })
        Spacer(modifier = Modifier.height(5.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ) {
            items(
                items = LauncherData.allCircleMenus.value?.filter {
                    it.title.lowercase().contains(searchText.lowercase())
                } ?: emptyList()
            ) { circleMenu ->
                MiniCircleMenuItem(
                    picked = circleMenu.id == pickedId,
                    size = (Integer.min(
                        LocalConfiguration.current.screenWidthDp,
                        LocalConfiguration.current.screenHeightDp
                    ) - 20f) / 3,
                    circleMenu = circleMenu
                ) {
                    onPick(circleMenu)
                }
            }
        }
    }
}

@Composable
fun PickOtherActionTabContent(
    picked: CircleMenuActionTypes,
    onPick: (OtherAction) -> Unit
) {
    LazyColumn {
        items(
            items = Constants.otherActionsList,
            key = { it.type }
        ) { otherAction ->
            OtherActionItem(otherAction = otherAction, picked = otherAction.type == picked) {
                onPick(otherAction)
            }
        }
    }
}

@Composable
fun PickDefaultImageTabContent(
    picked: DefaultImage?,
    onPick: (DefaultImage) -> Unit
) {
    var searchText by rememberSaveable {
        mutableStateOf("")
    }
    Column {
        Spacer(modifier = Modifier.height(5.dp))
        SearchElement(searchText = searchText, onTextChange = { searchText = it })
        Spacer(modifier = Modifier.height(5.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(((LocalConfiguration.current.screenWidthDp - 20) / 50))
        ) {
            items(
                items = Constants.defaultImages.keys.toList().filter { it.name.lowercase().contains(searchText.lowercase()) }
            ) { defaultImage ->
                Image(
                    modifier = Modifier
                        .size(50.dp)
                        .background(if (defaultImage == picked) Color.Gray.copy(alpha = 0.5f) else Color.Transparent)
                        .clickable {
                            onPick(defaultImage)
                        },
                    painter = painterResource(
                        id = Constants.defaultImages[defaultImage] ?: R.drawable.ic_error
                    ),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun PickUserImageTabContent(
    picked: UserImage?,
    onPick: (UserImage) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
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
            onPick(UserImage(id = newId))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (picked == null) {
            Button(onClick = { launcher.launch("image/*") }) {
                Text(text = stringResource(id = R.string.pick_image))
            }
        } else {
            val imageBitmap = CircleMenuImage(
                type = CircleMenuImageTypes.UserImage,
                data = picked
            ).getItemImage(context)
            if (imageBitmap == null) {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text(text = stringResource(id = R.string.pick_image))
                }
            } else {
                Image(
                    modifier = Modifier.size((LocalConfiguration.current.screenWidthDp / 4).dp),
                    bitmap = imageBitmap,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun SearchElement(searchText: String, onTextChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray.copy(alpha = 0.4f))
            .padding(horizontal = 15.dp, vertical = 5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (searchText.isEmpty()) {
            Text(
                text = stringResource(id = R.string.search),
                color = Color.Black.copy(alpha = 0.6f)
            )
        }
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            textStyle = TextStyle(
                color = Color.Black,
            ),
            value = searchText,
            onValueChange = onTextChange
        )
    }
}