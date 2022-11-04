package com.example.weatherapp.ui.search

import androidx.lifecycle.viewModelScope
import com.example.weatherapp.interactors.localcalls.citynames.GetRecentCityNames
import com.example.weatherapp.models.local.City
import com.example.weatherapp.ui.utils.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getRecentCityNames: GetRecentCityNames,
) : ObservableViewModel() {

    private var _cityNames = MutableStateFlow(emptyList<City>())
    val cityNames: StateFlow<List<City>> = _cityNames

    private var _errorMessage = MutableSharedFlow<Unit>()
    val errorMessage: SharedFlow<Unit> = _errorMessage.asSharedFlow()

    init {
        getRecentCityNames()
    }

    private fun getRecentCityNames() {
        viewModelScope.launch {
            getRecentCityNames.getRecentCityNames().collect {
                _cityNames.value = it
            }
        }
    }

    fun setErrorMessage() {
        viewModelScope.launch {
            _errorMessage.emit(Unit)
        }
    }
}