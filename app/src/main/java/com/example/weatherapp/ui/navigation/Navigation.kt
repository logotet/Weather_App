package com.example.weatherapp.ui.navigation

import com.example.weatherapp.ui.destinations.GPSScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

fun DestinationsNavigator.navigateToGPSScreen(
    isGPSEnabled: () -> Boolean,
    startGPS: () -> Unit
) {
    if (isGPSEnabled()) {
        this.navigate(GPSScreenDestination)
    } else {
        startGPS()
    }
}