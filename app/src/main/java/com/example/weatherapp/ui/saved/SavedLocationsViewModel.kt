package com.example.weatherapp.ui.saved

import androidx.lifecycle.viewModelScope
import com.example.weatherapp.interactors.localcalls.locations.GetFavoriteLocations
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.ui.utils.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedLocationsViewModel @Inject constructor(
    private val getFavoriteLocations: GetFavoriteLocations,
) : ObservableViewModel() {

    private var _locations = MutableSharedFlow<List<LocalWeatherModel>>()
    val locations: SharedFlow<List<LocalWeatherModel>>
        get() = _locations

    var isNetworkAvailable: Boolean = false

    fun loadData() {
        viewModelScope.launch {
            getFavoriteLocations.getFavoriteLocations().collect {
                _locations.emit(it)
            }
        }
    }
}