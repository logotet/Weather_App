package com.example.weatherapp.utils

import androidx.databinding.InverseMethod
import com.example.weatherapp.R

@InverseMethod("idToMeasure")
fun measureToId(measure: Measure?): Int {
    return when (measure) {
        Measure.METRIC -> R.id.btn_metric
        Measure.STANDARD -> R.id.btn_standard
        Measure.IMPERIAL -> R.id.btn_imperial
        else -> R.id.btn_metric
    }
}

fun idToMeasure(selectedBtnId: Int): Measure{
    return when(selectedBtnId){
        R.id.btn_metric -> Measure.METRIC
        R.id.btn_standard -> Measure.STANDARD
        R.id.btn_imperial -> Measure.IMPERIAL
        else -> Measure.METRIC
    }
}
