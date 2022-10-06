package com.example.weatherapp.ui.navigation

import androidx.navigation.NavController

fun NavController.navigateToForecastFromSearch(locationName: String?) {
    this.navigate(
        "${NavRoutes.ROUTE_FORECAST}/$locationName"
    )
}

fun NavController.navigateToForecastFromSaved(name: String) {
    this.navigate(
        "${NavRoutes.ROUTE_FORECAST}/$name"
    ) {
        popUpTo(NavRoutes.ROUTE_SAVED) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

fun NavController.navigateToForecastFromGps(locationName: String) {
    this.navigate(
        "${NavRoutes.ROUTE_FORECAST}/$locationName"
    ) {
        popUpTo(NavRoutes.ROUTE_SEARCH)
    }
}

fun NavController.navigateToSavedScreen() {
    this.navigate(NavRoutes.ROUTE_SAVED) {
        popUpTo(NavRoutes.ROUTE_SAVED) {
            inclusive = true
        }
    }
}