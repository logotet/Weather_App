package com.example.weatherapp.ui.current

import androidx.databinding.Bindable
import androidx.lifecycle.*
import com.example.weatherapp.interactors.GetHourlyWeather
import com.example.weatherapp.models.Measure
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.ui.ObservableViewModel
import com.example.weatherapp.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityFragmentViewModel @Inject constructor(
    private val getHourlyWeather: GetHourlyWeather,
    private val resourceProvider: ResourceProvider,
) : ObservableViewModel() {

    private var _hours = MutableLiveData<List<HourWeatherModel>>()
    val hours: LiveData<List<HourWeatherModel>>
    get() = _hours

    private var cityWeatherModel: CurrentWeatherModel? = null
        set(value) {
            field = value
            notifyChange()
        }

    var measure: Measure = Measure.METRIC

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
            _hours.value = cityWeatherModel?.let {
                getHourlyWeather.getHours(measure.value,
                    it.lat,
                    it.lon).data
            }
            notifyChange()
        }
    }

    fun setUpData(cityWeatherModel: CurrentWeatherModel?, measure: Measure) {
        this.cityWeatherModel = cityWeatherModel
        this.measure = measure
        getHourlyWeather()
    }


}
