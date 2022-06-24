package com.example.weatherapp.ui.saved

import androidx.lifecycle.viewModelScope
import com.example.weatherapp.interactors.localcalls.locations.GetFavoriteLocations
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.ui.utils.ObservableViewModel
import com.example.weatherapp.ui.saved.locations.OnSavedLocationClickedListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedLocationsFragmentViewModel @Inject constructor(
    private val getFavoriteLocations: GetFavoriteLocations,
) : ObservableViewModel(), OnSavedLocationClickedListener {

    private var _locations = MutableSharedFlow<List<LocalWeatherModel>>()
    val locations: SharedFlow<List<LocalWeatherModel>>
        get() = _locations

    private var _selectedLocation = MutableSharedFlow<String>()
    val selectedLocation: SharedFlow<String>
        get() = _selectedLocation.asSharedFlow()

    var isNetworkAvailable: Boolean = false

    fun loadData() {
        viewModelScope.launch {
            getFavoriteLocations.getFavoriteLocations().collect {
                _locations.emit(it)
            }
        }
    }

    override fun onSavedLocationClicked(cityName: String) {
        viewModelScope.launch {
            _selectedLocation.emit(cityName)
        }
    }
}