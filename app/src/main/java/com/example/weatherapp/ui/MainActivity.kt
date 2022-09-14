package com.example.weatherapp.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherapp.R
import com.example.weatherapp.ui.coords.GPSScreen
import com.example.weatherapp.ui.current.ForecastScreen
import com.example.weatherapp.ui.saved.SavedLocationsScreen
import com.example.weatherapp.ui.search.SearchScreen
import com.example.weatherapp.utils.AppConstants.ARG_LOCATION
import com.example.weatherapp.utils.AppConstants.ROUTE_FORECAST
import com.example.weatherapp.utils.AppConstants.ROUTE_GPS
import com.example.weatherapp.utils.AppConstants.ROUTE_SAVED
import com.example.weatherapp.utils.AppConstants.ROUTE_SEARCH
import dagger.hilt.android.AndroidEntryPoint
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.constructLocationPermissionRequest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val activityViewModel: MainActivityViewModel by viewModels()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            Scaffold(topBar = {
                Appbar(navigateToSavedLocations = {
                    navController.navigate(ROUTE_SAVED)
                })
            }) {
                NavHost(
                    navController = navController,
                    startDestination = ROUTE_SEARCH,
                    modifier = Modifier.padding(it)
                ) {
                    composable(route = ROUTE_SEARCH) {
                        SearchScreen(
                            viewModel = hiltViewModel(),
                            searchLocation = { locationName ->
                                navController.navigate(
                                    "$ROUTE_FORECAST/$locationName"
                                )
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
                                locationName = it
                            )
                        }
                    }
                    composable(route = "gps") {
                        GPSScreen()
                    }
                    composable(route = "saved") {
                        SavedLocationsScreen(
                            viewModel = hiltViewModel(),
                            selectLocation = { /*TODO*/ }
                        )
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                navController.navigate(R.id.action_global_savedLocationsFragment)
                true
            }
            else -> false
        }
    }

    //////////
    @Composable
    fun Appbar(navigateToSavedLocations: () -> Unit) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = Color.White
                )
            },
            backgroundColor = colorResource(id = R.color.royal_blue),
            actions = {
                IconButton(onClick = {
                    navigateToSavedLocations()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_favorites_list), "",
                        tint = Color.White
                    )
                }
            }
        )
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