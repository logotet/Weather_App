package com.example.weatherapp.ui.hours

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.weatherapp.models.Measure
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.utils.*
import javax.inject.Inject

class HourViewModel @Inject constructor(
    private val measure: Measure,
    private val resourceProvider: ResourceProvider,
) : BaseObservable() {

    var hourModel: HourWeatherModel? = null
        set(value) {
            field = value
            notifyChange()
        }

    @get:Bindable
    val hourTemperature: String?
        get() = hourModel?.hourTemperature.formatTemperature(resourceProvider, measure)

    @get:Bindable
    val hourWindSpeed: String?
        get() = hourModel?.hourWindSpeed.formatSpeed(resourceProvider, measure)

    @get:Bindable
    val hourWeatherIconPath: String?
        get() = AppConstants.IMG_URL + hourModel?.hourIcon + AppConstants.IMG_URL_SUFFIX

    @get:Bindable
    val hourValue: String?
        @RequiresApi(Build.VERSION_CODES.O)
        get() = hourModel?.hour?.formatHour(resourceProvider, hourModel?.timeZoneOffset)

    @get:Bindable
    val rotation: Int?
        get() = hourModel?.windDirection
}