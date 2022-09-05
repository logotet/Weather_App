package com.example.weatherapp.ui.current

import androidx.compose.runtime.mutableStateOf
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
import com.google.android.gms.maps.model.LatLng
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

    private var _locationName = MutableStateFlow<String?>(null)
    val locationName: StateFlow<String?> = _locationName

    var weatherModelState = mutableStateOf<CurrentWeatherModel?>(null)

    val hoursState = mutableStateOf<List<HourWeatherModel>>(emptyList())

    val location = mutableStateOf(LatLng(20.0, 30.0))

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
            getNetworkWeatherResponse(city)
            getWeatherFromDatabase(city)
//            getNetworkHours()
//            getLocalHours(city)
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
                                    getNetworkHours()
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
                    weatherModelState.value = it
                    isSavedLocation(it?.name)
                    getLocalHours(city)
                },
                {}
            )
        }
    }

    private fun getNetworkHours() {
        viewModelScope.launch {
            weatherModelState.value?.let { model ->
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
                hoursState.value = it.mapToCurrentHours()
            }
        }
    }


    private fun isSavedLocation(name: String?) {
        viewModelScope.launch {
            name?.let {
                getFavoriteLocationByName.getSavedLocation(it).collect {
                    _locationName.emit(it)
                }
            }
        }
    }

    fun saveLocationToFavorites() {
        viewModelScope.launch {
            weatherModelState.value?.let {
                insertSavedLocation.insertAsSaved(SavedLocation(it.name))
            }
        }
    }

    fun removeLocationFromFavorites() {
        viewModelScope.launch {
            weatherModelState.value?.let {
                removeLocationFromFavorites.removeFromFavorites(it.name)
            }
        }
    }

    private fun insertRecentCity() {
        viewModelScope.launch {
            weatherModelState.value?.let {
                insertRecentCityName.insertCityName(City(it.name))
            }
        }
    }
}
