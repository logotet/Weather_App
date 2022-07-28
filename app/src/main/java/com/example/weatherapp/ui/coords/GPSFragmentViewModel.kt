package com.example.weatherapp.ui.coords

import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.checkStatus
import com.example.weatherapp.interactors.apicalls.GetCurrentCoordWeather
import com.example.weatherapp.interactors.localcalls.citynames.GetCurrentLocation
import com.example.weatherapp.interactors.localcalls.locations.DeleteCurrentLocation
import com.example.weatherapp.ui.utils.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GPSFragmentViewModel @Inject constructor(
    private val getCurrentCoordWeather: GetCurrentCoordWeather,
    private val getCurrentLocation: GetCurrentLocation,
    private val deleteCurrentLocation: DeleteCurrentLocation
) : ObservableViewModel() {

    private val _locationName = MutableSharedFlow<String>()
    val locationName: SharedFlow<String>
        get() = _locationName.asSharedFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String>
        get() = _errorMessage.asSharedFlow()

    fun setUpData(lat: Double?, lon: Double?) {
        viewModelScope.launch {
            deleteCurrentLocation.deleteCurrentLocation()
        }
        getWeatherData(lat, lon)
        getCoordsDataFromDatabase()
    }

    private fun getWeatherData(lat: Double?, lon: Double?) {
        viewModelScope.launch {
            lat?.let { lat ->
                lon?.let { lon ->
                    getCurrentCoordWeather.getCurrentCoordWeather(
                        lat,
                        lon,
                    )
                        .collect { result ->
                            result.checkStatus(
                                {},
                                {
                                    viewModelScope.launch {
                                        it.message?.let { message -> _errorMessage.emit(message) }
                                    }
                                }
                            )
                        }
                }
            }
        }
    }

    private fun getCoordsDataFromDatabase() {
        viewModelScope.launch {
            getCurrentLocation.getCurrentLocation().collect { location ->
                location?.name?.let { _locationName.emit(it) }
            }
        }
    }
}