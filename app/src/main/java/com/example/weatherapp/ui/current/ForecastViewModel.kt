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
import com.example.weatherapp.models.local.City
import com.example.weatherapp.models.local.SavedLocation
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.models.ui.CurrentWeatherModel
import com.example.weatherapp.models.ui.HourWeatherModel
import com.example.weatherapp.models.utils.mapToCurrentHours
import com.example.weatherapp.ui.utils.ObservableViewModel
import com.example.weatherapp.utils.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    var weatherModelState by mutableStateOf<CurrentWeatherModel?>(null)

    var hoursState by mutableStateOf<List<HourWeatherModel>>(emptyList())

    var unitSystem: UnitSystem = UnitSystem.METRIC

    private var _errorMessage = MutableSharedFlow<String?>()
    val errorMessage: SharedFlow<String?> = _errorMessage.asSharedFlow()

    var cityName: String = ""
        set(value) {
            if (weatherModelState == null) {
                getNetworkWeatherResponse(value)
                getWeatherFromDatabase(value)
                getLocalHours(value)
            }
        }

    fun setData(cityName: String, unitSystem: UnitSystem) {
        this.cityName = cityName
        this.unitSystem = unitSystem
    }

    private fun getNetworkWeatherResponse(city: String?) {
        viewModelScope.launch {
            city?.let {
                getCurrentCityWeather.getCurrentWeather(it)
                    .collectResult(
                        {},
                        {
                            viewModelScope.launch {
                                _errorMessage.emit(resourceProvider.getString(R.string.no_location_found))
                            }
                        }
                    )
            }
        }
        notifyChange()
    }

    private fun getWeatherFromDatabase(city: String) {
        viewModelScope.launch {
            getLocationByName.getCity(city).collectResult(
                {
                    weatherModelState = it
                    isSavedLocation(it?.name)
                    getNetworkHours()
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
                        viewModelScope.launch {
                            _errorMessage.emit(it.message)
                        }
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
