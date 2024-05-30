@file:Suppress("DEPRECATION")

package com.kindeev.swipelauncher.presentation.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.getItemOffset
import com.kindeev.swipelauncher.domain.getThisAppIcon
import com.kindeev.swipelauncher.domain.showLauncherSelection
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val window = (LocalContext.current as Activity).window
    val view = LocalView.current
    val controller = WindowInsetsControllerCompat(window, view)
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        controller.isAppearanceLightStatusBars = true
    }
    val pagerState = rememberPagerState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            modifier = Modifier.weight(10f),
            state = pagerState,
            count = 4
        ) { page ->
            PageContent(page = page)
        }
        HorizontalPagerIndicator(
            modifier = Modifier.weight(1f),
            pagerState = pagerState
        )
        FinishButton(
            modifier = Modifier.weight(1f),
            onClick = {
                scope.launch { controller.isAppearanceLightStatusBars = false }
                onFinish()
            },
            visible = pagerState.currentPage == 3
        )
    }
}

@Composable
private fun PageContent(page: Int) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    when (page) {
        0 -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = screenWidth.dp / 7),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Image(
                    modifier = Modifier.size(screenWidth.dp / 10 * 6),
                    bitmap = LocalContext.current.getThisAppIcon(),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_first_title),
                    fontSize = screenWidth.sp / 15,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_first_desc),
                    fontSize = screenWidth.sp / 25
                )
            }
        }

        1 -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = screenWidth.dp / 7),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Box(
                    modifier = Modifier
                        .size(screenWidth.dp / 10 * 6)
                ) {
                    (0..7).forEach { index ->
                        val imageResId = Constants.onBoarding2MenuImageResIds[index]
                        val offset = Constants.menuCords[index].getItemOffset(screenWidth / 10 * 6f)
                        Image(
                            modifier = Modifier
                                .offset(
                                    x = offset.x.dp,
                                    y = offset.y.dp
                                )
                                .size(screenWidth.dp / 50 * 6),
                            painter = painterResource(id = imageResId),
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_second_title),
                    fontSize = screenWidth.sp / 15,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_second_desc),
                    fontSize = screenWidth.sp / 25
                )
            }
        }

        2 -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = screenWidth.dp / 7),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Image(
                    modifier = Modifier.size(screenWidth.dp / 10 * 6),
                    painter = painterResource(id = R.drawable.on_boarding_3_image),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_third_title),
                    fontSize = screenWidth.sp / 15,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_third_desc),
                    fontSize = screenWidth.sp / 25
                )
            }
        }

        3 -> {
            val context = LocalContext.current
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = screenWidth.dp / 7),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Image(
                    modifier = Modifier.size(screenWidth.dp / 10 * 6),
                    painter = painterResource(id = R.drawable.on_boarding_4_image),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_fourth_title),
                    fontSize = screenWidth.sp / 15,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_fourth_desc),
                    fontSize = screenWidth.sp / 25
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Button(onClick = { context.showLauncherSelection() }) {
                    Text(text = stringResource(id = R.string.go_to_settings))
                }
            }
        }
    }
}

@Composable
fun FinishButton(
    modifier: Modifier,
    onClick: () -> Unit,
    visible: Boolean
) {
    Box(
        modifier = modifier.padding(horizontal = 40.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Button(onClick = onClick) {
                Text(text = "Завершить")
            }
        }
    }
}