@file:Suppress("DEPRECATION")

package com.kindeev.swipelauncher.presentation.screens

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.Constants
import com.kindeev.swipelauncher.domain.utils.showLauncherSelection
import com.kindeev.swipelauncher.domain.viewModels.screens.onBoardingScreen.OnBoardingScreenVM
import com.kindeev.swipelauncher.domain.viewModels.screens.onBoardingScreen.OnBoardingScreenVMFactory
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: OnBoardingScreenVM = viewModel(
        factory = OnBoardingScreenVMFactory(context)
    )
    val window = (LocalContext.current as Activity).window
    val view = LocalView.current
    val controller = WindowInsetsControllerCompat(window, view)
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val orientationPhone = remember {
        configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }
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
            count = 9
        ) { page ->
            if (orientationPhone) {
                PageContentPhone(
                    page = page,
                    viewModel = viewModel
                )
            } else {
                PageContentTablet(
                    page = page,
                    viewModel = viewModel
                )
            }
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
            visible = pagerState.currentPage == 8
        )
    }
}

@Composable
private fun PageContentTablet(
    page: Int,
    viewModel: OnBoardingScreenVM
) {
    val screenWidth = Constants.minScreenLength
    when (page) {
        0 -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = screenWidth.dp / 2)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(screenWidth.dp / 10 * 5),
                            bitmap = viewModel.getThisAppIcon(),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                        Text(
                            text = stringResource(id = R.string.on_boarding_first_title),
                            fontSize = screenWidth.sp / 20,
                            lineHeight = screenWidth.sp / 20,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.on_boarding_first_desc),
                        fontSize = screenWidth.sp / 25,
                        lineHeight = screenWidth.sp / 25
                    )
                }
            }
        }

        1 -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = screenWidth.dp / 2)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier
                                .size(screenWidth.dp / 10 * 5),
                            painter = painterResource(R.drawable.circle_menu_performance),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                        Text(
                            text = stringResource(id = R.string.on_boarding_second_title),
                            fontSize = screenWidth.sp / 20,
                            lineHeight = screenWidth.sp / 20,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.on_boarding_second_desc),
                        fontSize = screenWidth.sp / 25,
                        lineHeight = screenWidth.sp / 25
                    )
                }
            }
        }

        2 -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = screenWidth.dp / 2)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(screenWidth.dp / 10 * 5),
                            painter = painterResource(id = R.drawable.on_boarding_3_image),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                        Text(
                            text = stringResource(id = R.string.on_boarding_third_title),
                            fontSize = screenWidth.sp / 20,
                            lineHeight = screenWidth.sp / 20,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.on_boarding_third_desc),
                        fontSize = screenWidth.sp / 25,
                        lineHeight = screenWidth.sp / 25
                    )
                }
            }
        }
        3 -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = screenWidth.dp / 2)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(screenWidth.dp / 10 * 5),
                            painter = painterResource(id = R.drawable.on_boarding_4_image),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                        Text(
                            text = stringResource(id = R.string.on_boarding_fourth_title),
                            fontSize = screenWidth.sp / 20,
                            lineHeight = screenWidth.sp / 20,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.on_boarding_fourth_desc),
                        fontSize = screenWidth.sp / 25,
                        lineHeight = screenWidth.sp / 25
                    )
                }
            }
        }
        4 -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = screenWidth.dp / 2)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(screenWidth.dp / 10 * 5),
                            painter = painterResource(id = R.drawable.on_boarding_5_image),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                        Text(
                            text = stringResource(id = R.string.on_boarding_fifth_title),
                            fontSize = screenWidth.sp / 20,
                            lineHeight = screenWidth.sp / 20,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.on_boarding_fifth_desc),
                        fontSize = screenWidth.sp / 25,
                        lineHeight = screenWidth.sp / 25
                    )
                }
            }
        }
        5 -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = screenWidth.dp / 2)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(screenWidth.dp / 10 * 5),
                            painter = painterResource(id = R.drawable.on_boarding_6_image),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                        Text(
                            text = stringResource(id = R.string.on_boarding_sixth_title),
                            fontSize = screenWidth.sp / 20,
                            lineHeight = screenWidth.sp / 20,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.on_boarding_sixth_desc),
                        fontSize = screenWidth.sp / 25,
                        lineHeight = screenWidth.sp / 25
                    )
                }
            }
        }
        6 -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = screenWidth.dp / 2)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier
                                .size(screenWidth.dp / 10 * 5),
                            painter = painterResource(R.drawable.open_circle_menu_image),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                        Text(
                            text = stringResource(id = R.string.on_boarding_seventh_title),
                            fontSize = screenWidth.sp / 20,
                            lineHeight = screenWidth.sp / 20,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.on_boarding_seventh_desc),
                        fontSize = screenWidth.sp / 25,
                        lineHeight = screenWidth.sp / 25
                    )
                }
            }
        }
        7 -> {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = screenWidth.dp / 2)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(screenWidth.dp / 10 * 5),
                            painter = painterResource(id = R.drawable.on_boarding_8_image),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                        Text(
                            text = stringResource(id = R.string.on_boarding_eighth_title),
                            fontSize = screenWidth.sp / 20,
                            lineHeight = screenWidth.sp / 20,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.on_boarding_eighth_desc),
                        fontSize = screenWidth.sp / 25,
                        lineHeight = screenWidth.sp / 25
                    )
                }
            }
        }
        8 -> {
            val context = LocalContext.current
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = screenWidth.dp / 2)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(screenWidth.dp / 10 * 5),
                            painter = painterResource(id = R.drawable.on_boarding_9_image),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                        Text(
                            text = stringResource(id = R.string.on_boarding_ninth_title),
                            fontSize = screenWidth.sp / 20,
                            lineHeight = screenWidth.sp / 20,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.on_boarding_ninth_desc),
                        fontSize = screenWidth.sp / 25,
                        lineHeight = screenWidth.sp / 25
                    )
                    Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                    Button(onClick = { context.showLauncherSelection() }) {
                        Text(text = stringResource(id = R.string.go_to_settings))
                    }
                }
            }
        }
    }
}

@Composable
private fun PageContentPhone(
    page: Int,
    viewModel: OnBoardingScreenVM
) {
    val screenWidth = Constants.minScreenLength
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
                    bitmap = viewModel.getThisAppIcon(),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_first_title),
                    fontSize = screenWidth.sp / 15,
                    lineHeight = screenWidth.sp / 15,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_first_desc),
                    fontSize = screenWidth.sp / 25,
                    lineHeight = screenWidth.sp / 25
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
                Image(
                    modifier = Modifier
                        .size(screenWidth.dp / 10 * 6),
                    painter = painterResource(R.drawable.circle_menu_performance),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_second_title),
                    fontSize = screenWidth.sp / 15,
                    lineHeight = screenWidth.sp / 15,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_second_desc),
                    fontSize = screenWidth.sp / 25,
                    lineHeight = screenWidth.sp / 25
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
                    lineHeight = screenWidth.sp / 15,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_third_desc),
                    fontSize = screenWidth.sp / 25,
                    lineHeight = screenWidth.sp / 25
                )
            }
        }
        3 -> {
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
                    lineHeight = screenWidth.sp / 15,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_fourth_desc),
                    fontSize = screenWidth.sp / 25,
                    lineHeight = screenWidth.sp / 25
                )
            }
        }
        4 -> {
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
                    painter = painterResource(id = R.drawable.on_boarding_5_image),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_fifth_title),
                    fontSize = screenWidth.sp / 15,
                    lineHeight = screenWidth.sp / 15,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_fifth_desc),
                    fontSize = screenWidth.sp / 25,
                    lineHeight = screenWidth.sp / 25
                )
            }
        }
        5 -> {
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
                    painter = painterResource(id = R.drawable.on_boarding_6_image),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_sixth_title),
                    fontSize = screenWidth.sp / 15,
                    lineHeight = screenWidth.sp / 15,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_sixth_desc),
                    fontSize = screenWidth.sp / 25,
                    lineHeight = screenWidth.sp / 25
                )
            }
        }
        6 -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = screenWidth.dp / 7),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Image(
                    modifier = Modifier
                        .size(screenWidth.dp / 10 * 6),
                    painter = painterResource(R.drawable.open_circle_menu_image),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_seventh_title),
                    fontSize = screenWidth.sp / 15,
                    lineHeight = screenWidth.sp / 15,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_seventh_desc),
                    fontSize = screenWidth.sp / 25,
                    lineHeight = screenWidth.sp / 25
                )
            }
        }
        7 -> {
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
                    painter = painterResource(id = R.drawable.on_boarding_8_image),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_eighth_title),
                    fontSize = screenWidth.sp / 15,
                    lineHeight = screenWidth.sp / 15,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_eighth_desc),
                    fontSize = screenWidth.sp / 25,
                    lineHeight = screenWidth.sp / 25
                )
            }
        }
        8 -> {
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
                    painter = painterResource(id = R.drawable.on_boarding_9_image),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_ninth_title),
                    fontSize = screenWidth.sp / 15,
                    lineHeight = screenWidth.sp / 15,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(screenWidth.dp / 20))
                Text(
                    text = stringResource(id = R.string.on_boarding_ninth_desc),
                    fontSize = screenWidth.sp / 25,
                    lineHeight = screenWidth.sp / 25
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
                Text(text = stringResource(id = R.string.finish))
            }
        }
    }
}