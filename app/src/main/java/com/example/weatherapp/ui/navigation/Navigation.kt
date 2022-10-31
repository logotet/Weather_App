package com.example.weatherapp.ui.navigation

import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.ui.destinations.GPSScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

fun DestinationsNavigator.navigateToGPSScreen(
    unitSystem: UnitSystem,
    isGPSEnabled: () -> Boolean,
    startGPS: () -> Unit
) {
    if (isGPSEnabled()) {
        this.navigate(GPSScreenDestination.invoke(unitSystem))
    } else {
        startGPS()
    }
}