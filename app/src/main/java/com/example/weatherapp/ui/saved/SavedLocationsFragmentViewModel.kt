package com.example.weatherapp.ui.saved

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.interactors.localcalls.hours.GetLocationHours
import com.example.weatherapp.interactors.localcalls.locations.GetFavoriteLocations
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.ui.ObservableViewModel
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
    private val getLocationHours: GetLocationHours,
) : ObservableViewModel(), OnSavedLocationClickedListener {

    private var _locations = MutableLiveData<List<LocalWeatherModel>>()
    val locations: LiveData<List<LocalWeatherModel>>
        get() = _locations

    private var _selectedLocation = MutableSharedFlow<String>()
    val selectedLocation: SharedFlow<String>
        get() = _selectedLocation.asSharedFlow()

    private var resultData: CurrentWeatherModel? = null

    var isNetworkAvailable: Boolean = false

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _locations.value = getFavoriteLocations.getFavoriteLocations()
        }
    }

    override fun onSavedLocationClicked(cityName: String) {
                getWeatherFromDatabase(cityName)
    }

    private fun getWeatherFromDatabase(cityName: String) {
        viewModelScope.launch {
            getLocationHours.getLocationHours(cityName).collect { map ->
                val localDataModel = map.keys.first()
                localDataModel?.let {
                    _selectedLocation.emit(localDataModel.name)
                }
            }
        }
    }
}