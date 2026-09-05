@file:Suppress("DEPRECATION")

package com.kindeev.swipelauncher.presentation.viewModels.onBoardingScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.PagerState
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.presentation.interfaces.StringGetter
import com.kindeev.swipelauncher.presentation.useCases.ShowLauncherSelectionUseCase
import kotlinx.coroutines.launch

class OnBoardingScreenVM(
    private val showLauncherSelectionUseCase: ShowLauncherSelectionUseCase,
    private val stringGetter: StringGetter,
    private val onFinish: () -> Unit
) : ViewModel() {

    val pagerState = PagerState(0)

    fun showLauncherSelection() {
        showLauncherSelectionUseCase.show()
    }

    fun nextButtonText(): String =
        stringGetter.getString(
            if (pagerState.currentPage == 8) R.string.finish else R.string.next
        )

    fun clickNextButton() {
        val page = pagerState.currentPage
        if (page == 8) {
            onFinish()
        } else {
            viewModelScope.launch {
                pagerState.animateScrollToPage(page + 1)
            }
        }
    }
}