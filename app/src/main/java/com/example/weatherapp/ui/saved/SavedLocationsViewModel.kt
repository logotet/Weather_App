package com.example.weatherapp.ui.saved

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.interactors.localcalls.locations.GetFavoriteLocations
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.ui.utils.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedLocationsViewModel @Inject constructor(
    private val getFavoriteLocations: GetFavoriteLocations,
) : ObservableViewModel(), DefaultLifecycleObserver {

    private var _locations = MutableStateFlow<List<LocalWeatherModel>>(emptyList())
    val locations: StateFlow<List<LocalWeatherModel>> = _locations.asStateFlow()

    override fun onStart(owner: LifecycleOwner) {
        viewModelScope.launch {
            getFavoriteLocations.getFavoriteLocations().collect {
                _locations.emit(it)
            }
        }
    }
}