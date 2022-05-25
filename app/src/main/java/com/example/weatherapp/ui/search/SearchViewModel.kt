package com.example.weatherapp.ui.search

import android.location.Location
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.NetworkResult
import com.example.weatherapp.interactors.GetCurrentCityWeather
import com.example.weatherapp.interactors.GetCurrentCoordWeather
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getCurrentCityWeather: GetCurrentCityWeather,
    private val getCurrentCoordWeather: GetCurrentCoordWeather,
) : ObservableViewModel() {

    @Bindable
    var cityName: String? = null

    var latitude: Double? = null
    var longitude: Double? = null

    @Bindable
    var measure: Measure = Measure.METRIC

    private var _sharedMeasure = MutableLiveData<Measure>()
    val sharedMeasure: LiveData<Measure>
        get() = _sharedMeasure

    private var _cityWeatherModel = MutableLiveData<CurrentWeatherModel>()
    val cityWeatherModel: MutableLiveData<CurrentWeatherModel>?
        get() = _cityWeatherModel

    private var _errorMessage = SingleLiveEvent<String>()
    val errorMessage: SingleLiveEvent<String>
        get() = _errorMessage

    private var _onLocationButtonPressed = SingleLiveEvent<Boolean>()
    val onLocationButtonPressed: SingleLiveEvent<Boolean>
        get() = _onLocationButtonPressed

    private var locationRequestGranted: Boolean = false

    init {
        _sharedMeasure.value = measure
    }

    fun getCurrentCityWeather() {
        viewModelScope.launch {
            val result = cityName?.let {
                getCurrentCityWeather.getCurrentWeather(it, measure.value)
            }
            setResult(result)
        }
    }

    fun onCurrentLocationPressed() {
        _onLocationButtonPressed.value = true
    }

    private fun setResult(result: NetworkResult<CurrentWeatherModel>?) {
        when (result) {
            is NetworkResult.Success -> {
                _cityWeatherModel.value = result.data!!
                _sharedMeasure.value = measure
            }
            is NetworkResult.Error -> _errorMessage.value = result.message!!
        }
        notifyChange()
    }

    fun getCoordWeather(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
        viewModelScope.launch {
            val result = latitude?.let { lat ->
                longitude?.let { lon ->
                    getCurrentCoordWeather.getCurrentCoordWeather(lat,
                        lon,
                        measure.value)
                }
            }
            setResult(result)
        }
    }
}