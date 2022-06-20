package com.example.weatherapp.ui.search

import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.data.Result
import com.example.weatherapp.interactors.apicalls.GetCurrentCityWeather
import com.example.weatherapp.interactors.apicalls.GetCurrentCoordWeather
import com.example.weatherapp.interactors.apicalls.GetLocationNameByCoords
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
    private val getRecentCityNames: GetRecentCityNames,
    private val getLocationNameByCoords: GetLocationNameByCoords,
    private val resourceProvider: ResourceProvider
) : ObservableViewModel(), OnRecentClickListener {

    @Bindable
    var cityName: String? = null

    @Bindable
    var measure: Measure = Measure.METRIC

    private var _locations = MutableStateFlow(emptyList<LocalWeatherModel>())
    val locations: StateFlow<List<LocalWeatherModel>> = _locations

    private var _cityNames = MutableStateFlow(emptyList<City>())
    val cityNames: StateFlow<List<City>> = _cityNames

    private var _onLocationButtonPressed = SingleLiveEvent<Unit>()
    val onLocationButtonPressed: SingleLiveEvent<Unit>
        get() = _onLocationButtonPressed

    private var _onSearchButtonPressed = SingleLiveEvent<Unit>()
    val onSearchButtonPressed: SingleLiveEvent<Unit>
        get() = _onSearchButtonPressed

    private var _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String>
        get() = _errorMessage.asSharedFlow()


    var isNetworkAvailable: Boolean = true

    init {
        getRecentCityNames()
    }

    fun onCurrentLocationPressed() {
        _onLocationButtonPressed.call()
    }

    fun onSearchPressed() {
        _onSearchButtonPressed.call()
    }

    private fun getRecentCityNames() {
        viewModelScope.launch {
            getRecentCityNames.getRecentCityNames().collect {
                _cityNames.value = it
            }
        }
    }

    fun getCityNameByCoords(lat: Double, lon: Double){
        viewModelScope.launch {
            val locationResult = getLocationNameByCoords.getLocationName(lat, lon)
            if(locationResult is Result.Success<String>){

            }else{
                _errorMessage.emit(resourceProvider.getString(R.string.no_location_found))
            }
        }
    }

    override fun onItemClicked(city: String) {
        cityName = city
        onSearchPressed()
    }
}