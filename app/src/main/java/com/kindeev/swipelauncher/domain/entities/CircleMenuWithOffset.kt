package com.kindeev.swipelauncher.domain.entities

import androidx.compose.ui.geometry.Offset
import com.kindeev.swipelauncher.presentation.entities.CircleMenuForUI

data class CircleMenuWithOffset(val circleMenuForUI: CircleMenuForUI, val offset: Offset?)
