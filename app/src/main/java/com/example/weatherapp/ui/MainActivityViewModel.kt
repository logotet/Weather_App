package com.example.weatherapp.ui

import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.utils.Measure
import com.example.weatherapp.utils.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ObservableViewModel() {

    var model: CurrentWeatherModel? = null
    var measure:Measure = Measure.METRIC
}