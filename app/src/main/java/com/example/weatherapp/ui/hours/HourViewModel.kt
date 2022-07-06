package com.example.weatherapp.ui.hours

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.models.ui.HourWeatherModel
import com.example.weatherapp.models.utils.mapTemperature
import com.example.weatherapp.models.utils.mapWindSpeed
import com.example.weatherapp.utils.*
import javax.inject.Inject

class HourViewModel @Inject constructor(
    private val unitSystem: UnitSystem,
    private val resourceProvider: ResourceProvider,
) : BaseObservable() {

    var hourModel: HourWeatherModel? = null
        set(value) {
            field = value
            notifyChange()
        }

    @get:Bindable
    val hourTemperature: String
        get() {
            val hourMappedTemperature = unitSystem.mapTemperature(hourModel?.hourTemperature)
            return hourMappedTemperature.formatTemperature(resourceProvider, unitSystem)
        }

    @get:Bindable
    val hourWindSpeed: String
        get() {
            val hourMappedSpeed = unitSystem.mapWindSpeed(hourModel?.hourWindSpeed)
            return hourMappedSpeed.formatSpeed(resourceProvider, unitSystem)
        }

    @get:Bindable
    val hourWeatherIconPath: String
        get() = AppConstants.IMG_URL + hourModel?.hourIcon + AppConstants.IMG_URL_SUFFIX

    @get:Bindable
    val hourValue: String?
        @RequiresApi(Build.VERSION_CODES.O)
        get() = hourModel?.hour?.formatHour(resourceProvider, hourModel?.timeZoneOffset)

    @get:Bindable
    val rotation: Int?
        get() = hourModel?.windDirection
}