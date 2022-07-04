package com.example.weatherapp.utils

import androidx.databinding.InverseMethod
import com.example.weatherapp.R
import com.example.weatherapp.models.measure.UnitSystem

@InverseMethod("idToMeasure")
fun measureToId(unitSystem: UnitSystem?): Int {
    return when (unitSystem) {
        UnitSystem.METRIC -> R.id.btn_metric
        UnitSystem.STANDARD -> R.id.btn_standard
        UnitSystem.IMPERIAL -> R.id.btn_imperial
        else -> R.id.btn_metric
    }
}

fun idToMeasure(selectedBtnId: Int): UnitSystem {
    return when (selectedBtnId) {
        R.id.btn_metric -> UnitSystem.METRIC
        R.id.btn_standard -> UnitSystem.STANDARD
        R.id.btn_imperial -> UnitSystem.IMPERIAL
        else -> UnitSystem.METRIC
    }
}
