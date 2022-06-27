package com.example.weatherapp.models.measure

enum class UnitSystem(val value: String) {
    METRIC("metric"),
    STANDARD("standard"),
    IMPERIAL("imperial");

    companion object {
        //todo do you still use this function? If not delete it
        fun getMeasure(value: String?): UnitSystem {
            return when (value) {
                // todo use METRIC.value instead of the plain string for all the cases
                "metric" -> METRIC
                "standard" -> STANDARD
                "imperial" -> IMPERIAL
                else -> METRIC
            }
        }
    }
}