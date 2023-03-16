package com.example.weatherapp.ui.search

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.weatherapp.R
import com.example.weatherapp.models.local.City
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.ui.Appbar
import com.example.weatherapp.ui.destinations.ForecastScreenDestination
import com.example.weatherapp.ui.destinations.GPSScreenDestination
import com.example.weatherapp.ui.destinations.SavedLocationsScreenDestination
import com.example.weatherapp.ui.navigation.navigateToGPSScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.constructLocationPermissionRequest

@RootNavGraph(start = true)
@Destination
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    var isGPSActivationLaunched = false

    val errorMessage = stringResource(R.string.valid_location)

    var units by remember {
        mutableStateOf(UnitSystem.METRIC)
    }

    val recentLocationsList = viewModel.cityNames.collectAsState()

    DisposableEffect(lifecycleOwner) {
        //todo maybe use it in the view model
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (isGPSActivationLaunched && isGPSEnabled(context)) {
                        navigator.navigate(GPSScreenDestination.invoke(units))
                    }

                    isGPSActivationLaunched = false
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val locationDeniedMessage = stringResource(id = R.string.location_denied)

    LaunchedEffect(Unit) {
        viewModel.errorMessage.collectLatest {
            scaffoldState.snackbarHostState.showSnackbar(locationDeniedMessage)
        }
    }

    SearchScreenContent(
        scaffoldState = scaffoldState,
        recentLocationsList = recentLocationsList.value,
        onUnitsSelected = { selectedUnits -> units = selectedUnits },
        actionSearch = { location ->
            searchLocation(location, navigator, units, context, errorMessage)
        },
        navigateToSavedLocations = {
            navigator.navigate(SavedLocationsScreenDestination.invoke(units = units)) {
                popUpTo(SavedLocationsScreenDestination.route) {
                    inclusive = true
                }
            }
        },
        handleGpsRequest = {
            (context as? FragmentActivity)?.constructLocationPermissionRequest(
                LocationPermission.FINE,
                onShowRationale = ::onGetLocationRationale,
                onPermissionDenied = { viewModel.setErrorMessage() },
                requiresPermission = {
                    navigator.navigateToGPSScreen(
                        units,
                        { isGPSEnabled(context) },
                        {
                            isGPSActivationLaunched = true
                            openGPSSystemPage(context)
                        }
                    )
                }
            )?.launch()
        },
        selectLocation = { selectedLocation ->
            navigator.navigate(
                ForecastScreenDestination.invoke(
                    selectedLocation,
                    units
                )
            )
        }
    )
}

@Composable
fun SearchScreenContent(
    scaffoldState: ScaffoldState,
    recentLocationsList: List<City>,
    onUnitsSelected: (UnitSystem) -> Unit,
    navigateToSavedLocations: () -> Unit,
    actionSearch: (TextFieldValue) -> Unit,
    handleGpsRequest: () -> Unit,
    selectLocation: (String) -> Unit
) {
    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(id = R.string.search),
                navigateUp = {},
                navigationIcon = null,
                menuItems = {
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
        },
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it)
                .background(colorResource(R.color.jordi_blue)),
        ) {
            val unitSystemText =
                remember { mutableStateOf(UnitSystem.METRIC.name.capitalizeFirst()) }

            TextSearchScreen(
                modifier = Modifier.padding(top = 20.dp),
                text = unitSystemText.value,
                textAlign = TextAlign.Center,
                fontSize = 26.sp
            )

            UnitsRadioGroup(
                selectUnits = { selectedUnits ->
                    onUnitsSelected(selectedUnits)
                },
                getNewUnitText = { newUnitText ->
                    unitSystemText.value =
                        newUnitText.capitalizeFirst()
                }
            )

            var locationNameText by remember { mutableStateOf(TextFieldValue("")) }

            OutlinedTextField(
                value = locationNameText,
                placeholder = { Text(text = stringResource(id = R.string.city)) },
                onValueChange = { newInput ->
                    locationNameText = newInput
                },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(6.dp),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null
                    )
                },
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions { actionSearch(locationNameText) }
            )

            ButtonWide(
                action = { actionSearch(locationNameText) },
                buttonText = stringResource(R.string.search)
            )

            TextSearchScreen(
                text = stringResource(R.string.or),
                textAlign = TextAlign.Center
            )

            ButtonWide(
                action = { handleGpsRequest() },
                buttonText = stringResource(R.string.get_my_location)
            )

            if (recentLocationsList.isNotEmpty()) {
                TextSearchScreen(
                    text = stringResource(R.string.recently_searched_locations),
                    textAlign = TextAlign.Start,
                    fontSize = 20.sp,
                )
            }

            Column {
                recentLocationsList.forEach { recentLocation ->
                    RecentLocationRow(city = recentLocation,
                        selectLocation = { selectedLocation ->
                            selectLocation(selectedLocation)
                        }
                    )
                }
            }
        }
    }
}

private fun isGPSEnabled(context: Context): Boolean {
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

private fun openGPSSystemPage(context: Context) {
    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
}

private fun searchLocation(
    locationNameText: TextFieldValue,
    navigator: DestinationsNavigator,
    unit: UnitSystem,
    context: Context,
    errorMessage: String
) {
    if (locationNameText.text.isNotEmpty())
        navigator.navigate(
            ForecastScreenDestination.invoke(
                locationNameText.text.trim(), unit
            )
        )
    else
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
}

private fun onGetLocationRationale(permissionRequest: PermissionRequest) {
    permissionRequest.proceed()
}

//todo extract
private fun String.capitalizeFirst(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreenContent(
        scaffoldState = rememberScaffoldState(),
        recentLocationsList = emptyList(),
        onUnitsSelected = {},
        navigateToSavedLocations = {},
        actionSearch = {},
        handleGpsRequest = {},
        selectLocation = {}
    )
}

