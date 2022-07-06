package com.example.weatherapp.ui.current

import androidx.databinding.Bindable
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
import com.example.weatherapp.models.utils.mapTemperature
import com.example.weatherapp.models.utils.mapToCurrentHours
import com.example.weatherapp.models.utils.mapWindSpeed
import com.example.weatherapp.ui.utils.ObservableViewModel
import com.example.weatherapp.ui.utils.onNetworkAvailability
import com.example.weatherapp.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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

    private var _hours = MutableStateFlow<List<HourWeatherModel>?>(null)
    val hours: StateFlow<List<HourWeatherModel>?>
        get() = _hours

    private var _coords = MutableStateFlow<Coord?>(null)
    val coords: StateFlow<Coord?> = _coords

    private var weatherModel: CurrentWeatherModel? = null
        set(value) {
            field = value
            value?.let {
                _coords.value = Coord(value.lon, value.lat)
            }
            getNetworkHours()
            notifyChange()
        }

    var unitSystem: UnitSystem = UnitSystem.METRIC

    private var isNetworkAvailable: Boolean = true

    private var _errorMessage = SingleLiveEvent<String?>()
    val errorMessage: SingleLiveEvent<String?>
        get() = _errorMessage

    private var _errorDatabaseMessage = SingleLiveEvent<String?>()
    val errorDatabaseMessage: SingleLiveEvent<String?>
        get() = _errorDatabaseMessage

    @get:Bindable
    val cityName: String?
        get() = weatherModel?.name

    @get:Bindable
    val description: String?
        get() = weatherModel?.description

    @get:Bindable
    val temperature: String
        get() {
            val temperature = unitSystem.mapTemperature(weatherModel?.temperature)
            return temperature.formatTemperature(resourceProvider, unitSystem)
        }

    @get:Bindable
    val humidity: String
        get() = weatherModel?.humidity.formatHumidity(resourceProvider)

    @get:Bindable
    val windSpeed: String
        get() {
            val windSpeed = unitSystem.mapWindSpeed(weatherModel?.windSpeed)
            return windSpeed.formatSpeed(resourceProvider, unitSystem)
        }

    @get:Bindable
    val iconPath: String
        get() = AppConstants.IMG_URL + weatherModel?.icon + AppConstants.IMG_URL_SUFFIX

    @get:Bindable
    val rotation: Int?
        get() = weatherModel?.windDirection


    fun setupData(city: String?, unitSystem: UnitSystem) {
        this.unitSystem = unitSystem
        if (!city.isNullOrEmpty()) {
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
                                {},
                                { _errorMessage.value = it.message }
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
                    weatherModel = it
                    isSavedLocation(it?.name)
                },
                {
                }
            )
        }
    }

    private fun getNetworkHours() {
        viewModelScope.launch {
            weatherModel?.let { model ->
                getHourlyWeather.getHours(
                    model.lat,
                    model.lon,
                    model.name).collectResult(
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
                _hours.emit(it.mapToCurrentHours())
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
            weatherModel?.let {
                insertSavedLocation.insertAsSaved(SavedLocation(it.name))
            }
        }
    }

    fun removeLocationFromFavorites() {
        viewModelScope.launch {
            weatherModel?.let {
                removeLocationFromFavorites.removeFromFavorites(it.name)
            }
        }
    }

    private fun insertRecentCity() {
        viewModelScope.launch {
            weatherModel?.let {
                insertRecentCityName.insertCityName(City(it.name))
            }
        }
    }

    fun refreshData() {
        cityName?.let {
            getNetworkWeatherResponse(cityName)
            getNetworkHours()
            notifyChange()
        }
    }
}
