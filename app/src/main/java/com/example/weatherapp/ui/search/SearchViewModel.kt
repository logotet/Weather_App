package com.example.weatherapp.ui.search

import android.location.Location
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.data.remote.checkResult
import com.example.weatherapp.data.remote.NetworkResult
import com.example.weatherapp.interactors.apicalls.GetCurrentCityWeather
import com.example.weatherapp.interactors.apicalls.GetCurrentCoordWeather
import com.example.weatherapp.interactors.localcalls.citynames.GetRecentCityNames
import com.example.weatherapp.models.Measure
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.local.City
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.ui.ObservableViewModel
import com.example.weatherapp.ui.utils.onNetworkAvailability
import com.example.weatherapp.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getCurrentCityWeather: GetCurrentCityWeather,
    private val getCurrentCoordWeather: GetCurrentCoordWeather,
    private val getRecentCityNames: GetRecentCityNames,
    private val resourceProvider: ResourceProvider,
) : ObservableViewModel(), OnRecentClickListener {

    var latitude: Double? = null
    var longitude: Double? = null

    @Bindable
    var cityName: String? = null

    @Bindable
    var measure: Measure = Measure.METRIC

    private var _sharedMeasure = MutableLiveData<Measure>()
    val sharedMeasure: LiveData<Measure>
        get() = _sharedMeasure

    private var _cityWeatherModel = MutableLiveData<CurrentWeatherModel>()
    val cityWeatherModel: MutableLiveData<CurrentWeatherModel>
        get() = _cityWeatherModel

    private var _locations = MutableStateFlow(emptyList<LocalWeatherModel>())
    val locations: StateFlow<List<LocalWeatherModel>> = _locations

    private var _cityNames = MutableStateFlow(emptyList<City>())
    val cityNames: StateFlow<List<City>> = _cityNames

    private var _errorMessage = SingleLiveEvent<String?>()
    val errorMessage: SingleLiveEvent<String?>
        get() = _errorMessage

    private var _onLocationButtonPressed = SingleLiveEvent<Unit>()
    val onLocationButtonPressed: SingleLiveEvent<Unit>
        get() = _onLocationButtonPressed

    private var _onSearchButtonPressed = SingleLiveEvent<Unit>()
    val onSearchButtonPressed: SingleLiveEvent<Unit>
        get() = _onSearchButtonPressed

    private var _navigationFired = SingleLiveEvent<Unit>()
    val navigationFired: SingleLiveEvent<Unit>
        get() = _navigationFired

    var isNetworkAvailable: Boolean = true

    init {
        _sharedMeasure.value = measure
        getRecentCityNames()
    }

    fun onCurrentLocationPressed() {
        _onLocationButtonPressed.call()
    }

    fun onSearchPressed() {
        _onSearchButtonPressed.call()
        //TODO check if this could be removed
        getCurrentCityWeather()
    }

    fun getCurrentCityWeather() {
        this.onNetworkAvailability(isNetworkAvailable,
            {
                viewModelScope.launch {
                    val result = cityName?.let {
                        getCurrentCityWeather.getCurrentWeather(it, measure.value)
                    }
                    checkCurrentWeatherResult(result)
                }
                notifyChange()
            },
            {
                _errorMessage.value = resourceProvider.getString(R.string.no_network_message)
            })

    }

    fun getCoordWeather(location: Location) {
        this.onNetworkAvailability(
            isNetworkAvailable,
            {
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
                    checkCurrentWeatherResult(result)
                    notifyChange()
                }
            },
            {
                _errorMessage.value = resourceProvider.getString(R.string.no_network_message)
            }
        )

    }

    private fun checkCurrentWeatherResult(result: NetworkResult<CurrentWeatherModel>?) {
        result?.checkResult(
            {
                _cityWeatherModel.value = it
                _sharedMeasure.value = measure
                _navigationFired.call()
            },
            {
                _errorMessage.value = it.message
            }
        )
    }

    private fun getRecentCityNames() {
        viewModelScope.launch {
            getRecentCityNames.getRecentCityNames().collect {
                _cityNames.value = it
            }
        }
    }

    override fun onItemClicked(city: String) {
        cityName = city
        getCurrentCityWeather()
    }

}