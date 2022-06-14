package com.example.weatherapp.ui.saved

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.checkResult
import com.example.weatherapp.interactors.apicalls.GetCurrentCityWeather
import com.example.weatherapp.interactors.localcalls.hours.GetLocationHours
import com.example.weatherapp.interactors.localcalls.locations.GetFavoriteLocations
import com.example.weatherapp.interactors.localcalls.locations.GetLocationByName
import com.example.weatherapp.models.Measure
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.models.utils.mapLocalToCurrentModel
import com.example.weatherapp.ui.ObservableViewModel
import com.example.weatherapp.ui.saved.locations.OnSavedLocationClickedListener
import com.example.weatherapp.ui.utils.onNetworkAvailability
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedLocationsFragmentViewModel @Inject constructor(
    private val getFavoriteLocations: GetFavoriteLocations,
    private val getLocationByName: GetLocationByName,
    private val getLocationHours: GetLocationHours,
    private val getCurrentCityWeather: GetCurrentCityWeather,
) : ObservableViewModel(), OnSavedLocationClickedListener {

    private var _locations = MutableLiveData<List<LocalWeatherModel>>()
    val locations: LiveData<List<LocalWeatherModel>>
        get() = _locations

    private var _selectedLocation = MutableSharedFlow<CurrentWeatherModel>()
    val selectedLocation: SharedFlow<CurrentWeatherModel>
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
        this.onNetworkAvailability(
            isNetworkAvailable,
            {
                getWeatherFromNetwork(cityName)
            },
            {
                getWeatherFromDatabase(cityName)
            }
        )
    }

    private fun getWeatherFromDatabase(cityName: String) {
        viewModelScope.launch {
            getLocationHours.getLocationHours(cityName).collect { map ->
                val localDataModel = map.keys.first()
                localDataModel?.let {
                    _selectedLocation.emit(localDataModel.mapLocalToCurrentModel())
                }
            }
        }
    }

    private fun getWeatherFromNetwork(cityName: String) {
        viewModelScope.launch {
            val result =
                getCurrentCityWeather.getCurrentWeather(cityName, Measure.METRIC.value)
            result.checkResult(
                {
                    resultData = it
                },
                {

                }
            )
            resultData?.let { _selectedLocation.emit(it) }
        }
    }

}