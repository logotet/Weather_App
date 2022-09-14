package com.example.weatherapp.ui.navigation

import androidx.navigation.NavController
import com.example.weatherapp.utils.AppConstants

fun NavController.navigateToForecastFromSearch(locationName: String?) {
    this.navigate(
        "${AppConstants.ROUTE_FORECAST}/$locationName"
    )
}

fun NavController.navigateToForecastFromSaved(name: String) {
    this.navigate(
        "${AppConstants.ROUTE_FORECAST}/$name"
    ) {
        popUpTo(AppConstants.ROUTE_FORECAST) {
            inclusive = true
        }
    }
}

fun NavController.navigateToForecastFromGps(locationName: String) {
    this.navigate(
        "${AppConstants.ROUTE_FORECAST}/$locationName"
    ) {
        popUpTo(AppConstants.ROUTE_SEARCH)
    }
}

fun NavController.navigateToSavedScreen() {
    this.navigate(AppConstants.ROUTE_SAVED) {
        popUpTo(AppConstants.ROUTE_SAVED) {
            inclusive = true
        }
    }
}