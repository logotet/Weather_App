package com.example.weatherapp.models.measure

enum class UnitSystem(val value: String) {
    METRIC("metric"),
    STANDARD("standard"),
    IMPERIAL("imperial");

    companion object {
        fun getMeasure(value: String?): UnitSystem {
            return when (value) {
                "metric" -> METRIC
                "standard" -> STANDARD
                "imperial" -> IMPERIAL
                else -> METRIC
            }
        }
    }
}