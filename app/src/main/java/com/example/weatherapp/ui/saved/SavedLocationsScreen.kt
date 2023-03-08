package com.example.weatherapp.ui.saved

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherapp.R
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.ui.Appbar
import com.example.weatherapp.ui.destinations.ForecastScreenDestination
import com.example.weatherapp.ui.destinations.SavedLocationsScreenDestination
import com.example.weatherapp.utils.observeLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun SavedLocationsScreen(
    viewModel: SavedLocationsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    units: UnitSystem
) {
    viewModel.observeLifecycle(lifecycle = LocalLifecycleOwner.current.lifecycle)

    val savedLocations = viewModel.locations.collectAsState()

    SavedLocationsScreenContent(
        savedLocations = savedLocations.value,
        units = units,
        navigateUp = { navigator.navigateUp() },
        navigateToWeather = { location ->
            navigator.navigate(
                ForecastScreenDestination.invoke(
                    location,
                    units
                )
            ) {
                popUpTo(SavedLocationsScreenDestination.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    )
}

@Composable
fun SavedLocationsScreenContent(
    savedLocations: List<LocalWeatherModel>,
    units: UnitSystem,
    navigateUp: () -> Unit,
    navigateToWeather: (String) -> Unit
) {
    Scaffold(topBar = {
        Appbar(
            title = stringResource(id = R.string.favorites_screen_title),
            menuItems = {},
            navigateUp = navigateUp
        )
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = colorResource(id = R.color.jordi_blue),
        ) {
            LazyColumn {
                items(savedLocations) { location ->
                    SavedLocationRow(localWeatherModel = location, units = units) { name ->
                        navigateToWeather(name)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavedLocationsPreview() {
    SavedLocationsScreenContent(
        savedLocations = listOf(
            LocalWeatherModel(
                name = "Sofia",
                description = "Broken clouds",
                temperature = 300.0,
                windSpeed = 20.0,
                humidity = 20,
                icon = "",
                lat = 20.0,
                lon = 20.0,
                windDirection = 20,
                addedAt = 1235676
            )
        ),
        units = UnitSystem.METRIC,
        navigateUp = {},
        navigateToWeather = {}
    )
}