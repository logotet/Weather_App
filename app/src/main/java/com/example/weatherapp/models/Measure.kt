package com.example.weatherapp.models

enum class Measure(val value: String) {
    METRIC("metric"),
    STANDARD("standard"),
    IMPERIAL("imperial");

    companion object {
        fun getMeasure(value: String?): Measure {
            return when (value) {
                "metric" -> METRIC
                "standard" -> STANDARD
                "imperial" -> IMPERIAL
                else -> METRIC
            }
        }
    }
}