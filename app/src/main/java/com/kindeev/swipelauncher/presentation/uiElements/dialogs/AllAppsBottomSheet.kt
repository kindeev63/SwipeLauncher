package com.kindeev.swipelauncher.presentation.uiElements.dialogs

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.data.DataObject
import com.kindeev.swipelauncher.presentation.activities.SettingsActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AllAppsBottomSheet(
    sheetState: ModalBottomSheetState
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .background(Color.White)
            .padding(2.dp)

    ) {
        val context = LocalContext.current
        val screenWidth = LocalConfiguration.current.screenWidthDp
        var searchText by remember {
            mutableStateOf("")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Gray)
            )
        }
        SearchItem(
            searchText = searchText,
            onTextChanged = {
                searchText = it
            },
            hint = "Поиск",
            goToSettings = {
                val intent = Intent(context, SettingsActivity::class.java)
                context.startActivity(intent)
            }
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed((screenWidth / 80).toInt())
        ) {
            items(
                items = DataObject.allApplicationData.filter {
                    it.name.lowercase().contains(searchText.lowercase())
                }
            ) { applicationData ->
                Column(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            val intent =
                                context.packageManager.getLaunchIntentForPackage(applicationData.packageName)
                            intent?.let {
                                scope.launch {
                                    sheetState.hide()
                                }
                                searchText = ""
                                context.startActivity(it)
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Image(
                        modifier = Modifier.size(35.dp),
                        bitmap = applicationData.icon,
                        contentDescription = null
                    )
                    Text(
                        text = applicationData.name.replace("\n", " "),
                        fontSize = (LocalConfiguration.current.screenWidthDp / 30).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchItem(
    searchText: String,
    onTextChanged: (String) -> Unit,
    hint: String,
    goToSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .height(40.dp)
                .padding(start = 5.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.LightGray)
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (searchText.isEmpty()) {
                Text(text = hint)
            }
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = searchText,
                onValueChange = onTextChanged
            )
        }
        Spacer(modifier = Modifier.width(2.dp))
        Image(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = goToSettings)
                .padding(5.dp),
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Gray)
        )
        Spacer(modifier = Modifier.width(2.dp))
    }

}