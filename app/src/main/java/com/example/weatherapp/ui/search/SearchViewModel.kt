package com.example.weatherapp.ui.search

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.remote.NetworkResult
import com.example.weatherapp.interactors.GetCurrentWeather
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getCurrentWeather: GetCurrentWeather
) : ObservableViewModel() {

    @Bindable
    var cityName: String? = null

    @Bindable
    var measure: Measure = Measure.METRIC

    private var _sharedMeasure = MutableLiveData<Measure>()
    val sharedMeasure: LiveData<Measure>
    get() = _sharedMeasure

    private var _cityWeatherModel = MutableLiveData<CurrentWeatherModel>()
    val cityWeatherModel: MutableLiveData<CurrentWeatherModel>?
        get() = _cityWeatherModel

    private var _errorMessage = SingleLiveEvent<String>()
    val errorMessage: SingleLiveEvent<String>
    get() = _errorMessage

    init {
        _sharedMeasure.value = measure
    }

    fun getCurrentWeather() {
        if (cityName != null)
            viewModelScope.launch {
                val result = getCurrentWeather.getCurrentWeather(cityName!!, measure.value)
                if(result is NetworkResult.Success){
                    _cityWeatherModel.value = result.data!!
                    _sharedMeasure.value = measure
                }else if(result is NetworkResult.Error){
                    _errorMessage.value = result.message!!
                }
                notifyChange()
            }
    }
}