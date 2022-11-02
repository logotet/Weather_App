package com.example.weatherapp.ui.current

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.R
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.models.ui.CurrentWeatherModel
import com.example.weatherapp.models.utils.*
import com.example.weatherapp.ui.Appbar
import com.example.weatherapp.ui.destinations.ForecastScreenDestination
import com.example.weatherapp.ui.destinations.SavedLocationsScreenDestination
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest

@Destination
@Composable
fun ForecastScreen(
    viewModel: ForecastViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    locationName: String,
    unitSystem: UnitSystem
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = {
            Appbar(
                title = stringResource(id = R.string.forecast_screen_title),
                navigateUp = { navigator.navigateUp() },
                menuItems = {

                    val isSaved = viewModel.savedState
                    val heartIcon =
                        if (isSaved) R.drawable.ic_heart_full else R.drawable.ic_heart_empty

                    IconButton(onClick = {
                        if (isSaved)
                            viewModel.removeLocationFromFavorites()
                        else
                            viewModel.saveLocationToFavorites()
                    }) {
                        Icon(
                            painter = painterResource(id = heartIcon), "",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = {
                        navigator.navigate(SavedLocationsScreenDestination.invoke(units = unitSystem)) {
                            popUpTo(ForecastScreenDestination.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
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
        scaffoldState = scaffoldState,
        backgroundColor = colorResource(id = R.color.jordi_blue)
    ) {
        Surface(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState()),
            color = colorResource(id = R.color.jordi_blue)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(1f)
            ) {

                viewModel.setData(locationName, unitSystem)

                val weatherModel = viewModel.weatherModelState

                LaunchedEffect(Unit) {
                    viewModel.errorMessage.collectLatest { message ->
                        message?.let { scaffoldState.snackbarHostState.showSnackbar(message) }
                        navigator.navigateUp()
                    }
                }

                if (weatherModel == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(600.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                weatherModel?.let { model ->
                    TextAndIconForecast(
                        painter = painterResource(id = R.drawable.ic_location_name),
                        text = model.name,
                        iconSize = 50.dp,
                    )

                    val formattedTemperature = unitSystem.mapTemperature(model.temperature)
                        .formatTemperatureComposable(unitSystem = unitSystem)
                    TextAndIconForecast(
                        painter = rememberAsyncImagePainter(model.icon.getIconUrl()),
                        text = formattedTemperature,
                        tint = Color.Unspecified,
                        rowHeight = 80.dp,
                        iconSize = 100.dp,
                        fontSize = 44.sp
                    )

                    Text(
                        text = model.description.capitalizeFirstChar(),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 28.sp,
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextAndIconForecast(
                            modifier = Modifier.fillMaxWidth(0.3f),
                            painter = painterResource(id = R.drawable.ic_raindrop),
                            text = model.humidity.formatHumidityComposable()
                        )

                        val formattedTWindSpeed = unitSystem.mapWindSpeed(model.windSpeed)
                        TextAndIconForecast(
                            modifier = Modifier.fillMaxWidth(0.6f),
                            iconModifier = Modifier
                                .padding(end = 6.dp)
                                .size(32.dp)
                                .fillMaxSize()
                                .align(Alignment.CenterVertically),
                            painter = painterResource(id = R.drawable.ic_arrow_direction),
                            text = formattedTWindSpeed.formatSpeedComposable(unitSystem),
                            rotation = model.windDirection
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.hourly),
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(
                            top = 20.dp,
                            start = 4.dp,
                            bottom = 4.dp
                        )
                    )

                    val hours = viewModel.hoursState
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp,
                            bottomEnd = 10.dp,
                            bottomStart = 10.dp
                        ),
                        color = colorResource(id = R.color.malibu)
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                        ) {
                            items(hours) { hour ->
                                Hour(
                                    hour = hour.hour,
                                    timeOffset = hour.timeZoneOffset,
                                    temperature = hour.hourTemperature,
                                    iconCode = hour.hourIcon,
                                    humidity = hour.humidity,
                                    windSpeed = hour.hourWindSpeed,
                                    rotation = hour.windDirection,
                                    unitSystem = unitSystem
                                )
                            }
                        }
                    }

                    val location = LatLng(
                        model.lat,
                        model.lon
                    )
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            location,
                            10f
                        )
                    }
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding(
                                top = 10.dp,
                                start = 4.dp,
                                end = 4.dp
                            ),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(
                            position = location,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TextAndIconForecast(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    rowHeight: Dp = 48.dp,
    painter: Painter,
    text: String,
    tint: Color = Color.White,
    iconSize: Dp = 30.dp,
    rotation: Int = 0,
    fontSize: TextUnit = 28.sp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .height(rowHeight),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = tint,
            modifier = iconModifier
                .size(iconSize)
                .rotate(rotation.toFloat())
        )

        ForecastScreenText(
            modifier = Modifier,
            text = text,
            fontSize = fontSize
        )
    }
}

@Composable
fun ForecastScreenText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = fontSize,
        color = Color.White,
    )
}

@Composable
fun ForecastScreenHourText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 20.sp,
    padding: Dp = 4.dp,
) {
    Text(
        text = text,
        fontSize = fontSize,
        color = Color.White,
        modifier = modifier.padding(padding)
    )
}

@Composable
fun Hour(
    hour: Long,
    timeOffset: Int,
    iconCode: String,
    temperature: Double,
    humidity: Int,
    windSpeed: Double,
    rotation: Int,
    unitSystem: UnitSystem
) {
    Surface(
        color = colorResource(id = R.color.malibu)
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .width(width = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val hourValue = hour.formatHour(timeOffset)
            ForecastScreenHourText(
                text = hourValue,
                padding = 2.dp,
                fontSize = 16.sp
            )

            Icon(
                painter = rememberAsyncImagePainter(iconCode.getIconUrl()),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(50.dp),
            )

            val formattedTemperature = unitSystem.mapTemperature(temperature)
                .formatTemperatureComposable(unitSystem = unitSystem)

            ForecastScreenHourText(text = formattedTemperature)

            val formattedHumidity = humidity.formatHumidityComposable()
            ForecastScreenHourText(text = formattedHumidity, fontSize = 16.sp)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_direction),
                    contentDescription = "image",
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(16.dp)
                        .fillMaxSize()
                        .rotate(rotation.toFloat()),
                    tint = Color.White
                )

                val formattedSpeed = unitSystem.mapWindSpeed(windSpeed)
                ForecastScreenHourText(
                    text = formattedSpeed.formatSpeedComposable(unitSystem = unitSystem),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ForecastScreenPreview() {
    Surface(
        modifier = Modifier,
        color = colorResource(id = R.color.jordi_blue)
    ) {
        Column {
            CurrentWeatherDataPreview()
            HourListPreview()
            MapPreview()
        }
    }
}

@Composable
fun CurrentWeatherDataPreview() {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp)
    ) {
        val model = CurrentWeatherModel(
            name = "Sofia",
            description = "broken clouds",
            temperature = 300.0,
            windSpeed = 5.1,
            humidity = 25,
            icon = "2n",
            lat = 23.21,
            lon = 43.46,
            windDirection = 280,
        )

        val unitSystem = UnitSystem.METRIC

        TextAndIconForecast(
            painter = painterResource(id = R.drawable.ic_location_name),
            text = model.name,
            iconSize = 50.dp,
        )

        val formattedTemperature = unitSystem.mapTemperature(model.temperature)
            .formatTemperatureComposable(unitSystem = unitSystem)
        TextAndIconForecast(
            painter = rememberAsyncImagePainter(model.icon.getIconUrl()),
            text = formattedTemperature,
            tint = Color.Unspecified,
            rowHeight = 80.dp,
            iconSize = 100.dp,
            fontSize = 44.sp
        )

        Text(
            text = model.description.capitalizeFirstChar(),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 28.sp,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextAndIconForecast(
                modifier = Modifier.fillMaxWidth(0.3f),
                painter = painterResource(id = R.drawable.ic_raindrop),
                text = model.humidity.formatHumidityComposable()
            )

            TextAndIconForecast(
                modifier = Modifier.fillMaxWidth(0.6f),
                iconModifier = Modifier
                    .padding(end = 6.dp)
                    .size(32.dp)
                    .fillMaxSize()
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.ic_arrow_direction),
                text = model.windSpeed.formatSpeedComposable(unitSystem),
                rotation = model.windDirection
            )
        }

        Text(
            text = stringResource(id = R.string.hourly),
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.padding(
                top = 20.dp,
                start = 4.dp,
                bottom = 4.dp
            )
        )
    }
}

@Composable
fun HourListPreview() {
    Surface(
        shape = RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp,
            bottomEnd = 10.dp,
            bottomStart = 10.dp
        )
    ) {
        LazyRow {
            items(7) {
                Hour(
                    hour = 10,
                    timeOffset = 3,
                    iconCode = "2n",
                    temperature = 300.0,
                    humidity = 20,
                    windSpeed = 20.0,
                    rotation = 20,
                    unitSystem = UnitSystem.METRIC
                )
            }
        }
    }
}

@Composable
fun MapPreview() {
    val location = LatLng(
        24.21,
        23.45
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            location,
            10f
        )
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(top = 10.dp),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            position = location,
        )
    }
}

