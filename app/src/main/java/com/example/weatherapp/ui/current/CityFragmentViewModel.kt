package com.example.weatherapp.ui.current

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.checkResult
import com.example.weatherapp.interactors.apicalls.GetHourlyWeather
import com.example.weatherapp.interactors.localcalls.*
import com.example.weatherapp.interactors.localcalls.citynames.InsertCityName
import com.example.weatherapp.interactors.localcalls.hours.GetLocationHours
import com.example.weatherapp.interactors.localcalls.hours.InsertListOfHours
import com.example.weatherapp.interactors.localcalls.locations.GetLocationByName
import com.example.weatherapp.interactors.localcalls.locations.InsertIntoDatabase
import com.example.weatherapp.interactors.localcalls.locations.RemoveLocationFromFavorites
import com.example.weatherapp.models.Measure
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.models.local.City
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.models.utils.mapApiToCurrentModel
import com.example.weatherapp.models.utils.mapToCurrentHours
import com.example.weatherapp.models.utils.mapToLocalHours
import com.example.weatherapp.ui.ObservableViewModel
import com.example.weatherapp.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityFragmentViewModel @Inject constructor(
    private val getHourlyWeather: GetHourlyWeather,
    private val insertIntoDatabase: InsertIntoDatabase,
    private val insertCityName: InsertCityName,
    private val resourceProvider: ResourceProvider,
    private val removeLocationFromFavorites: RemoveLocationFromFavorites,
    private val getLocationByName: GetLocationByName,
    private val insertListOfHours: InsertListOfHours,
    private val getLocationHours: GetLocationHours
) : ObservableViewModel() {

    private var _cityLocalModel = MutableStateFlow<LocalWeatherModel?>(null)
    val cityLocalModel: StateFlow<LocalWeatherModel?> = _cityLocalModel

    private var _hours = MutableLiveData<List<HourWeatherModel>>()
    val hours: MutableLiveData<List<HourWeatherModel>>
        get() = _hours

    private var cityWeatherModel: CurrentWeatherModel? = null
        set(value) {
            field = value
            notifyChange()
        }

    var measure: Measure = Measure.METRIC

    private var _errorMessage = SingleLiveEvent<String?>()
    val errorMessage: SingleLiveEvent<String?>
        get() = _errorMessage

    @get:Bindable
    val cityName: String?
        get() = cityWeatherModel?.name

    @get:Bindable
    val description: String?
        get() = cityWeatherModel?.description

    @get:Bindable
    val temperature: String
        get() = cityWeatherModel?.temperature.formatTemperature(resourceProvider, measure)

    @get:Bindable
    val humidity: String
        get() = cityWeatherModel?.humidity.formatHumidity(resourceProvider)

    @get:Bindable
    val windSpeed: String
        get() = cityWeatherModel?.windSpeed.formatSpeed(resourceProvider, measure)

    @get:Bindable
    val iconPath: String
        get() = AppConstants.IMG_URL + cityWeatherModel?.icon + AppConstants.IMG_URL_SUFFIX

    @get:Bindable
    val rotation: Int?
        get() = cityWeatherModel?.windDirection

    init {
        cityWeatherModel?.let {
            checkSavedLocation(it.name)
        }
    }

    private fun getHourlyWeather() {
        viewModelScope.launch {
            cityWeatherModel?.let {
                val result = getHourlyWeather.getHours(measure.value,
                    it.lat,
                    it.lon)
                result.checkResult(
                    {
                        _hours.value = it
                        insertRecentCity()
                    },
                    {
                        _errorMessage.value = it.message
                    }
                )
            }
//            //TODO experimental
//            viewModelScope.launch {
//                getLocationHours.getLocationHours("sofia").collect { map ->
//                    val hours = map.values.first()
//                    _hours.value = hours.mapToCurrentHours()
//                }
//            }
            notifyChange()
        }
    }

    fun saveWeatherData(){
        insertLocationAsSaved()
        insertLocationHoursAsSaved()
    }

    private fun insertLocationAsSaved() {
        viewModelScope.launch {
            cityWeatherModel?.let {
                val localModel = it.mapApiToCurrentModel()
                localModel.saved = true
                insertIntoDatabase.insertData(localModel)
                checkSavedLocation(it.name)
            }
        }
    }

    private fun insertLocationHoursAsSaved() {
        viewModelScope.launch {
            hours.value?.let {
                insertListOfHours.insertHours(it.mapToLocalHours(cityWeatherModel!!.name))
            }
        }
    }

    fun removeLocationFromFavorites() {
        viewModelScope.launch {
            cityWeatherModel?.let {
                removeLocationFromFavorites.removeFromFavorites(it.name)
                checkSavedLocation(it.name)
            }
        }
    }

    private fun insertRecentCity() {
        viewModelScope.launch {
            cityWeatherModel?.let {
                insertCityName.insertCityName(City(it.name))
            }
        }
    }

    fun checkSavedLocation(city: String) {
        viewModelScope.launch {
            getLocationByName.getCity(city).collect {
                _cityLocalModel.value = it
            }
        }
    }

    fun setUpData(cityWeatherModel: CurrentWeatherModel?, measure: Measure) {
        this.cityWeatherModel = cityWeatherModel
        this.measure = measure
        getHourlyWeather()
    }
}
