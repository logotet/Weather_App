package com.example.weatherapp.ui.current

import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.checkResult
import com.example.weatherapp.interactors.apicalls.GetHourlyWeather
import com.example.weatherapp.interactors.localcalls.InsertCityName
import com.example.weatherapp.interactors.localcalls.InsertIntoDatabase
import com.example.weatherapp.models.Measure
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.models.local.City
import com.example.weatherapp.models.utils.mapApiToCurrentModel
import com.example.weatherapp.ui.ObservableViewModel
import com.example.weatherapp.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityFragmentViewModel @Inject constructor(
    private val getHourlyWeather: GetHourlyWeather,
    private val insertIntoDatabase: InsertIntoDatabase,
    private val insertCityName: InsertCityName,
    private val resourceProvider: ResourceProvider,
) : ObservableViewModel() {

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
    val temperature: String?
        get() = cityWeatherModel?.temperature.formatTemperature(resourceProvider, measure)

    @get:Bindable
    val humidity: String?
        get() = cityWeatherModel?.humidity.formatHumidity(resourceProvider)

    @get:Bindable
    val windSpeed: String?
        get() = cityWeatherModel?.windSpeed.formatSpeed(resourceProvider, measure)

    @get:Bindable
    val iconPath: String?
        get() = AppConstants.IMG_URL + cityWeatherModel?.icon + AppConstants.IMG_URL_SUFFIX

    @get:Bindable
    val rotation: Int?
        get() = cityWeatherModel?.windDirection

    private fun getHourlyWeather() {
        viewModelScope.launch {
            cityWeatherModel?.let {
                val result = getHourlyWeather.getHours(measure.value,
                    it.lat,
                    it.lon)
                result.checkResult(
                    {
                        _hours.value = it
                        insertLocation()
                        insertRecentCity()
                    },
                    {
                        _errorMessage.value = it.message
                    }
                )
            }
            notifyChange()
        }
    }

    fun insertLocationAsSaved() {
        viewModelScope.launch {
            cityWeatherModel?.let {
                val localModel = it.mapApiToCurrentModel()
                localModel.saved = true
                insertIntoDatabase.insertData(localModel)
            }
        }
    }

    private fun insertLocation() {
        viewModelScope.launch {
            cityWeatherModel?.let {
                insertIntoDatabase.insertData(it.mapApiToCurrentModel())
            }
        }
    }

    private fun insertRecentCity(){
        viewModelScope.launch {
            cityWeatherModel?.let {
                insertCityName.insertCityName(City(it.name))
            }
        }
    }

    fun setUpData(cityWeatherModel: CurrentWeatherModel?, measure: Measure) {
        this.cityWeatherModel = cityWeatherModel
        this.measure = measure
        getHourlyWeather()
    }
}
