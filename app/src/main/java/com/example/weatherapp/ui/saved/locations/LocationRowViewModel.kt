package com.example.weatherapp.ui.saved.locations

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.models.utils.mapTemperature
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

    var unitSystem: UnitSystem? = null

    @get:Bindable
    val cityName: String?
        get() = location?.name

    @get:Bindable
    val cityTemperature: String?
        get() {
            val temperature = unitSystem?.mapTemperature(location?.temperature)
            return unitSystem?.let { temperature.formatTemperature(resourceProvider, it) }
        }

    fun onItemClicked() {
        cityName?.let {
            onSavedLocationClickedListener.onSavedLocationClicked(it)
        }
    }

}