package com.example.weatherapp.ui.current

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.data.remote.collectResult
import com.example.weatherapp.interactors.apicalls.GetCurrentCityWeather
import com.example.weatherapp.interactors.apicalls.GetHourlyWeather
import com.example.weatherapp.interactors.localcalls.citynames.InsertRecentCityName
import com.example.weatherapp.interactors.localcalls.hours.GetLocationHours
import com.example.weatherapp.interactors.localcalls.locations.GetFavoriteLocationByName
import com.example.weatherapp.interactors.localcalls.locations.GetLocationByName
import com.example.weatherapp.interactors.localcalls.locations.InsertSavedLocation
import com.example.weatherapp.interactors.localcalls.locations.RemoveLocationFromFavorites
import com.example.weatherapp.models.api.Coord
import com.example.weatherapp.models.local.City
import com.example.weatherapp.models.local.SavedLocation
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.models.ui.CurrentWeatherModel
import com.example.weatherapp.models.ui.HourWeatherModel
import com.example.weatherapp.models.utils.mapToCurrentHours
import com.example.weatherapp.ui.utils.ObservableViewModel
import com.example.weatherapp.ui.utils.onNetworkAvailability
import com.example.weatherapp.utils.ResourceProvider
import com.example.weatherapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val getHourlyWeather: GetHourlyWeather,
    private val insertRecentCityName: InsertRecentCityName,
    private val resourceProvider: ResourceProvider,
    private val removeLocationFromFavorites: RemoveLocationFromFavorites,
    private val getLocationByName: GetLocationByName,
    private val getCurrentCityWeather: GetCurrentCityWeather,
    private val getLocationHours: GetLocationHours,
    private val getFavoriteLocationByName: GetFavoriteLocationByName,
    private val insertSavedLocation: InsertSavedLocation,
) : ObservableViewModel() {
    var savedState by mutableStateOf(false)

    private var _locationName = MutableStateFlow<String?>(null)
    val locationName: StateFlow<String?> = _locationName

    var weatherModelState by mutableStateOf<CurrentWeatherModel?>(null)

    var hoursState by mutableStateOf<List<HourWeatherModel>>(emptyList())

    private var _coords = MutableStateFlow<Coord?>(null)
    val coords: StateFlow<Coord?> = _coords

    var unitSystem: UnitSystem = UnitSystem.METRIC

    private var isNetworkAvailable: Boolean = true

    private var _errorMessage = SingleLiveEvent<String?>()
    val errorMessage: SingleLiveEvent<String?>
        get() = _errorMessage

    private var _errorDatabaseMessage = SingleLiveEvent<String?>()
    val errorDatabaseMessage: SingleLiveEvent<String?>
        get() = _errorDatabaseMessage

    private var _cancelRefresh = SingleLiveEvent<Unit>()
    val cancelRefresh: SingleLiveEvent<Unit>
        get() = _cancelRefresh

    fun setupData(city: String?, unitSystem: UnitSystem) {
        this.unitSystem = unitSystem
        if (!city.isNullOrEmpty()) {
            //todo handle all the data calls in a separate usecase
            getNetworkWeatherResponse(city)
            getWeatherFromDatabase(city)
            getLocalHours(city)
        }
    }

    private fun getNetworkWeatherResponse(city: String?) {
        onNetworkAvailability(isNetworkAvailable,
            {
                viewModelScope.launch {
                    city?.let {
                        getCurrentCityWeather.getCurrentWeather(it)
                            .collectResult(
                                {
                                    _cancelRefresh.call()
                                },
                                {
                                    _errorMessage.value = it.message
                                }
                            )
                    }
                }
                notifyChange()
            },
            {
                _errorMessage.value = resourceProvider.getString(R.string.no_network_message)
            })
    }

    private fun getWeatherFromDatabase(city: String) {
        viewModelScope.launch {
            getLocationByName.getCity(city).collectResult(
                {
                    weatherModelState = it
                    getNetworkHours()
                    isSavedLocation(it?.name)
                },
                {}
            )
        }
    }

    private fun getNetworkHours() {
        viewModelScope.launch {
            weatherModelState?.let { model ->
                getHourlyWeather.getHours(
                    model.lat,
                    model.lon,
                    model.name
                ).collectResult(
                    {
                        insertRecentCity()
                    },
                    {
                        _errorMessage.value = it.message
                    }
                )
                notifyChange()
            }
        }
    }

    private fun getLocalHours(locationName: String) {
        viewModelScope.launch {
            getLocationHours.getLocationHours(locationName).collect {
                hoursState = it.mapToCurrentHours()
            }
        }
    }


    private fun isSavedLocation(name: String?) {
        viewModelScope.launch {
            name?.let {
                getFavoriteLocationByName.getSavedLocation(it).collect { savedName ->
                    _locationName.emit(savedName)
                    savedState = savedName != null
                }
            }
        }
    }

    fun saveLocationToFavorites() {
        viewModelScope.launch {
            weatherModelState?.let {
                insertSavedLocation.insertAsSaved(SavedLocation(it.name))
            }
        }
    }

    fun removeLocationFromFavorites() {
        viewModelScope.launch {
            weatherModelState?.let {
                removeLocationFromFavorites.removeFromFavorites(it.name)
            }
        }
    }

    private fun insertRecentCity() {
        viewModelScope.launch {
            weatherModelState?.let {
                insertRecentCityName.insertCityName(City(it.name))
            }
        }
    }
}
