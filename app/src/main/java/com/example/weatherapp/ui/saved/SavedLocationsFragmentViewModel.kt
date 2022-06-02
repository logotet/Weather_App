package com.example.weatherapp.ui.saved

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.interactors.localcalls.GetFavoriteLocations
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedLocationsFragmentViewModel @Inject constructor(
    private val getFavoriteLocations: GetFavoriteLocations
): ObservableViewModel() {

    private var _locations = MutableLiveData<List<LocalWeatherModel>>()
    val locations: LiveData<List<LocalWeatherModel>>
    get() = _locations

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _locations.value = getFavoriteLocations.getFavoriteLocations()
        }
    }

}