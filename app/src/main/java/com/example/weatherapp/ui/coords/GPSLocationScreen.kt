package com.example.weatherapp.ui.coords

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.ui.Appbar
import com.example.weatherapp.ui.destinations.ForecastScreenDestination
import com.example.weatherapp.ui.destinations.SearchScreenDestination
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@SuppressLint("UnrememberedMutableState")
@Composable
fun GPSScreen(
    viewModel: GPSFragmentViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {

    val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(LocalContext.current)
    val cancellationTokenSource = CancellationTokenSource()

    getCurrentLocation(
        fusedLocationProviderClient,
        cancellationTokenSource,
        viewModel
    )

    Scaffold(topBar = {
        Appbar(
            title = stringResource(id = R.string.gps_screen_title),
            menuItems = {},
            navigateUp = {},
            navigationIcon = null
        )
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.location_animation))
            val progress by animateLottieCompositionAsState(composition)
            LottieAnimation(
                modifier = Modifier.fillMaxSize(),
                composition = composition,
                progress = progress,
            )
        }
    }

    val location = viewModel.locationName
    location?.let {
        LaunchedEffect(key1 = true) {
            navigator.navigate(ForecastScreenDestination.invoke(location, UnitSystem.METRIC)) {
                popUpTo(SearchScreenDestination.route)
            }
        }
    }
}

private fun getCurrentLocation(
    fusedLocationProviderClient: FusedLocationProviderClient,
    cancellationTokenSource: CancellationTokenSource,
    viewModel: GPSFragmentViewModel
) {
    fusedLocationProviderClient.getCurrentLocation(
        LocationRequest.PRIORITY_HIGH_ACCURACY,
        cancellationTokenSource.token
    ).addOnSuccessListener { location ->
        location?.let {
            val lat = location.latitude
            val lon = location.longitude
            viewModel.setUpData(lat, lon)
        }
    }
}