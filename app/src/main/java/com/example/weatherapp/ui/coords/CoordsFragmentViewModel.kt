package com.example.weatherapp.ui.coords

import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.checkStatus
import com.example.weatherapp.interactors.apicalls.GetCurrentCoordWeather
import com.example.weatherapp.interactors.localcalls.locations.GetSavedLocationByCoords
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.ui.utils.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoordsFragmentViewModel @Inject constructor(
    private val getCurrentCoordWeather: GetCurrentCoordWeather,
    private val getSavedLocationByCoords: GetSavedLocationByCoords,
) : ObservableViewModel() {

    private val _locationName = MutableSharedFlow<String>()
    val locationName: SharedFlow<String>
        get() = _locationName.asSharedFlow()

    fun setUpData(lat: Double?, lon: Double?, unitSystem: UnitSystem) {
        getWeatherData(lat, lon, unitSystem)
        getSavedLocationByCoords()
    }

    private fun getWeatherData(lat: Double?, lon: Double?, unitSystem: UnitSystem) {
        viewModelScope.launch {
            lat?.let { lat ->
                lon?.let { lon ->
                    getCurrentCoordWeather.getCurrentCoordWeather(lat,
                        lon,
                        unitSystem.value)
                        .collect { result ->
                            result.checkStatus(
                                {},
                                {
//                _errorMessage.value = it.message
                                }
                            )
                        }
                }
            }
        }
    }

    private fun getSavedLocationByCoords() {
        viewModelScope.launch {
            //todo Try to create extension function on Flow that will accept Result<T> value and
            // will handle success and error cases. The idea is to simplify the collect call
            getSavedLocationByCoords.getCityByCoords().collect { result ->
                result.checkStatus(
                    { model ->
                        viewModelScope.launch {
                            model?.name?.let { _locationName.emit(it) }
                        }
                    },
                    {
                        return@checkStatus
                    }
                )
            }
        }
    }
}