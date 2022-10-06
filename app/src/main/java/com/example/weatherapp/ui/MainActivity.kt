package com.example.weatherapp.ui

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.coroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherapp.R
import com.example.weatherapp.ui.coords.GPSScreen
import com.example.weatherapp.ui.current.ForecastScreen
import com.example.weatherapp.ui.navigation.*
import com.example.weatherapp.ui.navigation.NavRoutes.Companion.ROUTE_FORECAST
import com.example.weatherapp.ui.navigation.NavRoutes.Companion.ROUTE_GPS
import com.example.weatherapp.ui.navigation.NavRoutes.Companion.ROUTE_SAVED
import com.example.weatherapp.ui.navigation.NavRoutes.Companion.ROUTE_SEARCH
import com.example.weatherapp.ui.saved.SavedLocationsScreen
import com.example.weatherapp.ui.search.SearchScreen
import com.example.weatherapp.utils.AppConstants.ARG_LOCATION
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.constructLocationPermissionRequest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val activityViewModel: MainActivityViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val cancellationTokenSource = CancellationTokenSource()

    private var gpsActivationLaunched: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = ROUTE_SEARCH
            ) {
                composable(route = ROUTE_SEARCH) {
                    SearchScreen(
                        viewModel = hiltViewModel(),
                        searchLocation = { locationName ->
                            navController.navigateToForecastFromSearch(locationName)
                        },
                        getCurrentLocation = {
                            constructLocationPermissionRequest(
                                LocationPermission.FINE,
                                onShowRationale = ::onGetLocationRationale,
                                onPermissionDenied = ::onLocationPermissionDenied,
                                requiresPermission = {
                                    navController.navigateToGPSScreen(
                                        { isGPSEnabled() },
                                        { openGPSSystemPage() }
                                    )
                                }
                            ).launch()
                        },
                        selectUnitSystem = { unitSystem ->
                            activityViewModel.unitSystem = unitSystem
                        },
                        navigateToSavedLocations = { navController.navigateToSavedScreen() },
                        handleGPSActivation = { scaffoldState ->
                            if (gpsActivationLaunched && isGPSEnabled()) {
                                navController.navigate(ROUTE_GPS)
                                gpsActivationLaunched = false
                            } else if (gpsActivationLaunched && !isGPSEnabled()) {
                                lifecycle.coroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        getString(R.string.location_turned_off)
                                    )
                                }
                            }
                        }
                    )
                }

                composable(route = "$ROUTE_FORECAST/{$ARG_LOCATION}", arguments = listOf(
                    navArgument(ARG_LOCATION) {
                        type = NavType.StringType
                    }
                )) { entry ->
                    entry.arguments?.getString(ARG_LOCATION)?.let {
                        ForecastScreen(
                            viewModel = hiltViewModel(),
                            locationName = it,
                            unitSystem = activityViewModel.unitSystem,
                            navigateToSavedLocations = navController::navigateToSavedScreen,
                            navigateUp = { navController.navigateUp() }
                        )
                    }
                }

                composable(route = ROUTE_GPS) {
                    GPSScreen(
                        viewModel = hiltViewModel(),
                        fusedLocationProviderClient = fusedLocationClient,
                        cancellationTokenSource = cancellationTokenSource,
                        navigateToForecast = { locationName ->
                            navController.navigateToForecastFromGps(locationName)
                        }
                    )
                }

                composable(route = ROUTE_SAVED) {
                    SavedLocationsScreen(
                        viewModel = hiltViewModel(),
                        selectLocation = { name ->
                            navController.navigateToForecastFromSaved(name)
                        },
                        navigateUp = { navController.navigateUp() }
                    )
                }
            }
        }
    }

    private fun openGPSSystemPage() {
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        gpsActivationLaunched = true
    }

    private fun onGetLocationRationale(permissionRequest: PermissionRequest) {
        permissionRequest.proceed()
    }

    private fun onLocationPermissionDenied() {
        Snackbar.make(
            findViewById<View>
                (android.R.id.content).rootView,
            getString(R.string.location_denied),
            Snackbar.LENGTH_LONG
        ).show()
        activityViewModel.barVisible = false
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}