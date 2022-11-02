package com.example.weatherapp.ui.search

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
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
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    val scaffoldState = rememberScaffoldState()

    var isGPSActivationLaunched = false
    var units by remember {
        mutableStateOf(UnitSystem.METRIC)
    }

    DisposableEffect(lifecycleOwner) {
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

    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(id = R.string.search),
                navigateUp = {},
                navigationIcon = null,
                menuItems = {
                    IconButton(onClick = {
                        navigator.navigate(SavedLocationsScreenDestination.invoke(units = units)) {
                            popUpTo(SavedLocationsScreenDestination.route) {
                                inclusive = true
                            }
                        }
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
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = colorResource(id = R.color.jordi_blue)
        ) {
            Column(modifier = Modifier.padding(top = 50.dp)) {
                val unitSystemText =
                    remember { mutableStateOf(UnitSystem.METRIC.name.capitalizeFirst()) }

                val errorMessage = stringResource(R.string.valid_location)

                TextSearchScreen(text = unitSystemText.value, TextAlign.Center, 26.sp)

                UnitsRadioGroup(
                    { selectedUnits
                        ->
                        units = selectedUnits
                    }
                ) { newUnitText ->
                    unitSystemText.value =
                        newUnitText.capitalizeFirst()
                }

                var locationNameText by remember { mutableStateOf(TextFieldValue("")) }

                TextField(
                    value = locationNameText,
                    label = { Text(text = stringResource(id = R.string.city)) },
                    onValueChange = { newInput ->
                        locationNameText = newInput
                    },
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = TextFieldDefaults.OutlinedTextFieldShape,
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
                    keyboardActions = KeyboardActions {
                        searchLocation(locationNameText, navigator, units, context, errorMessage)
                    }
                )

                ButtonSearchScreen(
                    {
                        searchLocation(locationNameText, navigator, units, context, errorMessage)
                    },
                    stringResource(R.string.search)
                )

                TextSearchScreen(
                    stringResource(R.string.or),
                    textAlign = TextAlign.Center
                )

                val locationDeniedMessage = stringResource(id = R.string.location_denied)
                ButtonSearchScreen(
                    {
                        val onLocationPermissionDenied = {
//                            scaffoldState.snackbarHostState.showSnackbar(locationDeniedMessage)
                        }

                        (context as? FragmentActivity)?.constructLocationPermissionRequest(
                            LocationPermission.FINE,
                            onShowRationale = ::onGetLocationRationale,
                            onPermissionDenied = { onLocationPermissionDenied() },
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
                    stringResource(R.string.get_my_location)
                )

                val recentLocationsList = viewModel.cityNames.collectAsState()

                if (recentLocationsList.value.isNotEmpty()) {
                    TextSearchScreen(
                        stringResource(R.string.recently_searched_locations),
                        textAlign = TextAlign.Start,
                        fontSize = 20.sp,
                    )
                }

                LazyColumn {
                    items(recentLocationsList.value) { recentLocation ->
                        RecentLocationRow(recentLocation) { selectedLocation ->
                            navigator.navigate(
                                ForecastScreenDestination.invoke(
                                    selectedLocation,
                                    units
                                )
                            )
                        }
                    }
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
                locationNameText.text, unit
            )
        )
    else
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
}

@Composable
fun UnitsRadioGroup(
    selectUnits: (UnitSystem) -> Unit,
    setNewUnitText: (String) -> Unit
) {
    val unitsList = listOf(UnitSystem.METRIC, UnitSystem.STANDARD, UnitSystem.IMPERIAL)
    var selectedValue by remember { mutableStateOf(unitsList[0]) }
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
    ) {

        val blueColor = colorResource(id = R.color.royal_blue)
        val lightGreyColor = colorResource(id = R.color.light_grey)

        unitsList.forEach { unit ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .selectable(
                        selected = (selectedValue == unit),
                        onClick = {
                            selectedValue = unit
                        },
                        role = Role.RadioButton
                    )
                    .padding(8.dp)
            ) {
                IconToggleButton(
                    checked = selectedValue == unit,
                    onCheckedChange = {
                        selectedValue = unit
                        setNewUnitText(unit.name)
                        selectUnits(unit)
                    },
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        val colorBlue = colorResource(R.color.royal_blue)
                        val colorGrey = colorResource(R.color.dusty_grey)

                        Canvas(modifier = Modifier.size(70.dp), onDraw = {

                            drawCircle(
                                color = if (selectedValue == unit) {
                                    colorBlue
                                } else {
                                    colorGrey
                                }
                            )
                        })
                        Text(
                            text = getUnitsFormat(unit),
                            color = Color.White,
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getUnitsFormat(unit: UnitSystem) = when (unit) {
    UnitSystem.METRIC -> stringResource(R.string.celsius_temperature_format)
    UnitSystem.STANDARD -> stringResource(R.string.standard_temperature_format)
    UnitSystem.IMPERIAL -> stringResource(R.string.imperial_temperature_format)
}

@Composable
private fun ButtonSearchScreen(
    action: () -> Unit,
    btnText: String
) {
    val colorBlue = colorResource(id = R.color.royal_blue)

    Button(
        onClick = action,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 10.dp,
                horizontal = 20.dp
            )
            .background(colorBlue)
    ) {
        Text(text = btnText)
    }
}

@Composable
private fun TextSearchScreen(
    text: String,
    textAlign: TextAlign,
    fontSize: TextUnit = 16.sp,
) {
    Text(
        text = text,
        color = Color.White,
        textAlign = textAlign,
        fontSize = fontSize,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                top = 10.dp
            )
    )
}

@Composable
fun RecentLocationRow(city: City, selectLocation: (String) -> Unit) {
    Text(
        text = city.cityName,
        fontSize = 24.sp,
        color = Color.White,
        modifier = Modifier
            .padding(
                start = 20.dp,
                top = 10.dp
            )
            .fillMaxWidth()
            .clickable { selectLocation(city.cityName) }
    )
}

fun String.capitalizeFirst(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SearchScreenPreview() {
    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(id = R.string.search),
                navigateUp = {},
                navigationIcon = {},
                menuItems = {
                    IconButton(onClick = {
                        { }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_favorites_list), "",
                            tint = Color.White
                        )
                    }
                }
            )
        },
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = colorResource(id = R.color.jordi_blue)
        ) {
            Column(modifier = Modifier.padding(top = 50.dp)) {
                val unitSystemText =
                    remember { mutableStateOf(UnitSystem.METRIC.name.capitalizeFirst()) }
                TextSearchScreen(text = unitSystemText.value, TextAlign.Center, 26.sp)

                UnitsRadioGroup(
                    { }
                ) { newUnitText ->
                    unitSystemText.value =
                        newUnitText.capitalizeFirst()
                }

                var locationNameText by remember { mutableStateOf(TextFieldValue("")) }
                TextField(
                    value = locationNameText,
                    label = { Text(text = stringResource(id = R.string.city)) },
                    onValueChange = { newInput ->
                        locationNameText = newInput
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = TextFieldDefaults.OutlinedTextFieldShape,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White)
                )

                val errorMessage = stringResource(R.string.valid_location)
                ButtonSearchScreen(
                    {
                    },
                    stringResource(R.string.search)
                )

                TextSearchScreen(
                    stringResource(R.string.or),
                    textAlign = TextAlign.Center
                )

                ButtonSearchScreen(
                    { },
                    stringResource(R.string.get_my_location)
                )

                LazyColumn {
                    items(5) {
                        RecentLocationRow(City("Sofia")) {
                        }
                    }
                }
            }
        }
    }
}

private fun onGetLocationRationale(permissionRequest: PermissionRequest) {
    permissionRequest.proceed()
}

