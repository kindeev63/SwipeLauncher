package com.kindeev.swipelauncher.domain.viewModels.elements.searchBox

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kindeev.swipelauncher.domain.entities.ApplicationInfo
import com.kindeev.swipelauncher.domain.useCases.ApplicationsUseCase
import com.kindeev.swipelauncher.domain.useCases.GetItemImageUseCase
import com.kindeev.swipelauncher.presentation.entities.searchBox.AppSBR
import com.kindeev.swipelauncher.presentation.entities.searchBox.SearchBoxResult
import kotlin.text.lowercase

class SearchBoxVM(context: Context) : ViewModel() {
    private val getItemImageUseCase = GetItemImageUseCase(context)
    private val applicationsUseCase = ApplicationsUseCase(context, getItemImageUseCase)

    private val _searchText = MutableLiveData("")
    val searchText: LiveData<String> = _searchText

    fun search(text: String) {
        _searchText.postValue(text)
    }

    fun getSearchResults(allApplicationInfo: List<ApplicationInfo>): List<SearchBoxResult> {
        searchText.value?.let { searchText ->
            return applicationsUseCase
                .getNotHidden(allApplicationInfo)
                .filter {
                    it.title
                        .lowercase()
                        .contains(searchText.lowercase())
                }
                .map { AppSBR(it) }
        }
        return emptyList()
    }
}