package com.example.weatherapp.ui.saved.locations

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.weatherapp.models.Measure
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.utils.ResourceProvider
import com.example.weatherapp.utils.formatTemperature

class LocationRowViewModel(
    private val resourceProvider: ResourceProvider,
    private val onSavedLocationClickedListener: OnSavedLocationClickedListener,
) : BaseObservable() {

    var location: LocalWeatherModel? = null
        set(value) {
            field = value
            notifyChange()
        }

    //TODO the value should not be constant
    var measure: Measure = Measure.METRIC

    @get:Bindable
    val cityName: String?
        get() = location?.name

    @get:Bindable
    val cityTemperature: String?
        get() = location?.temperature.formatTemperature(resourceProvider, measure)

    fun onItemClicked() {
        cityName?.let {
            onSavedLocationClickedListener.onSavedLocationClicked(it)
        }
    }

}