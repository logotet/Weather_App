package com.example.weatherapp.models.utils

import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.models.measure.UnitSystem.IMPERIAL
import com.example.weatherapp.models.measure.UnitSystem.METRIC

fun UnitSystem.mapTemperature(temperature: Double?): Double? {
    return when (this) {
        METRIC -> temperature?.fromKelvinToCelsius()
        IMPERIAL -> temperature?.fromKelvinToFahrenheit()
        else -> temperature
    }
}

fun UnitSystem.mapWindSpeed(speed: Double?): Double? {
    return when (this) {
        IMPERIAL -> speed?.fromMStoMPH()
        else -> speed
    }
}

private fun Double.fromKelvinToCelsius(): Double {
    return this - 273.15
}

private fun Double.fromKelvinToFahrenheit(): Double {
    return 1.8 * (this - 273) + 32
}

private fun Double.fromMStoMPH(): Double {
    return this * 2.2369
}
