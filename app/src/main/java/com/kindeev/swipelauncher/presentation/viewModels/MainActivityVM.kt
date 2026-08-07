package com.kindeev.swipelauncher.presentation.viewModels

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kindeev.swipelauncher.R
import com.kindeev.swipelauncher.domain.interfaces.DataRepository
import com.kindeev.swipelauncher.domain.useCases.GetRootCircleMenuUseCase
import com.kindeev.swipelauncher.presentation.navigation.MainActivityNav
import com.knomster.navigation_component.NavigationComponent
import kotlinx.coroutines.launch

class MainActivityVM(
    private val navigationComponent: NavigationComponent<MainActivityNav>,
    getRootCircleMenuUseCase: GetRootCircleMenuUseCase,
    dataRepository: DataRepository,
    context: Context
) : ViewModel() {

    private val firstRunPrefs = context.getSharedPreferences("data", Context.MODE_PRIVATE)
    val navigationBackStack = navigationComponent.backStack


    init {
        if (!firstRunPrefs.contains("first_run")) {
            navigationComponent.changeBackStack { backStack ->
                backStack.clear()
                backStack.add(MainActivityNav.OnBoarding)
            }
            viewModelScope.launch {
                dataRepository.insertCircleMenu(
                    getRootCircleMenuUseCase.get(
                        context.resources.getString(
                            R.string.root
                        )
                    )
                )
            }
        }
    }

    fun navigationOnBack() {
        navigationComponent.popUpBackStackSafe()
    }

    fun onCompleteOnBoarding() {
        navigationComponent.changeBackStack { backStack ->
            backStack.clear()
            backStack.add(MainActivityNav.Launcher)
        }
        viewModelScope.launch {
            firstRunPrefs.edit {
                putBoolean("first_run", false)
            }
        }
    }

}