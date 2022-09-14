package com.example.weatherapp.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherapp.ui.coords.GPSScreen
import com.example.weatherapp.ui.current.ForecastScreen
import com.example.weatherapp.ui.navigation.navigateToForecastFromGps
import com.example.weatherapp.ui.navigation.navigateToForecastFromSaved
import com.example.weatherapp.ui.navigation.navigateToForecastFromSearch
import com.example.weatherapp.ui.navigation.navigateToSavedScreen
import com.example.weatherapp.ui.saved.SavedLocationsScreen
import com.example.weatherapp.ui.search.SearchScreen
import com.example.weatherapp.utils.AppConstants.ARG_LOCATION
import com.example.weatherapp.utils.AppConstants.ROUTE_FORECAST
import com.example.weatherapp.utils.AppConstants.ROUTE_GPS
import com.example.weatherapp.utils.AppConstants.ROUTE_SAVED
import com.example.weatherapp.utils.AppConstants.ROUTE_SEARCH
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.constructLocationPermissionRequest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val activityViewModel: MainActivityViewModel by viewModels()

    private lateinit var navController: NavController

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val cancellationTokenSource = CancellationTokenSource()

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
                                requiresPermission = { navController.navigate(ROUTE_GPS) }
                            ).launch()
                        },
                        selectUnitSystem = { unitSystem ->
                            activityViewModel.unitSystem = unitSystem
                        },
                        navigateToSavedLocations = { navController.navigateToSavedScreen() }
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
                            navigateToSavedLocations = navController::navigateToSavedScreen,
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
                        }
                    )
                }
            }
        }
    }

    private fun onGetLocationRationale(permissionRequest: PermissionRequest) {
        permissionRequest.proceed()
    }

    private fun onLocationPermissionDenied() {
//        this.view?.let {
//            Snackbar.make(it, getString(R.string.location_denied), Snackbar.LENGTH_LONG).show()
//            activityViewModel.barVisible = false
//        }
    }
}