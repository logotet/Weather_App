package com.example.weatherapp.ui.current

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.R
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.models.utils.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ForecastScreen(viewModel: ForecastViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    Surface(color = colorResource(id = R.color.jordi_blue)) {
        Column(modifier = Modifier.fillMaxSize(1f)) {
            val weatherModel = viewModel.weatherModelState.value

            weatherModel?.let { model ->
                TextAndIconForecast(
                    painter = painterResource(id = R.drawable.ic_location_name),
                    text = model.name
                )

                val formattedTemperature = UnitSystem.METRIC.mapTemperature(model.temperature)
                    .formatTemperatureComposable(unitSystem = UnitSystem.METRIC)
                TextAndIconForecast(
                    painter = rememberAsyncImagePainter(model.icon.getIconUrl()),
                    text = formattedTemperature,
                    tint = Color.Unspecified,
                    //todo size does not correspond to the size value
                    iconSize = 60.dp
                )

                Text(
                    text = model.description.capitalizeFirstChar(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontSize = 36.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextAndIconForecast(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .height(48.dp)
                            .padding(horizontal = 20.dp),
                        painter = painterResource(id = R.drawable.ic_raindrop),
                        text = model.humidity.formatHumidityComposable()
                    )

                    TextAndIconForecast(
                        Modifier
                            .padding(top = 20.dp)
                            .height(48.dp)
                            .padding(horizontal = 20.dp),
                        painter = painterResource(id = R.drawable.ic_arrow_direction),
                        text = model.windSpeed.formatSpeedComposable(UnitSystem.METRIC)
                    )
                }

                Text(
                    text = stringResource(id = R.string.hourly),
                    fontSize = 24.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 20.dp)
                )

                //todo hours list is not always loaded?
                val hours = viewModel.hoursState.value
                LazyRow {
                    items(hours) { hour ->
                        Hour(
                            hour = hour.hour,
                            timeOffset = hour.timeZoneOffset,
                            temperature = hour.hourTemperature,
                            iconCode = hour.hourIcon,
                            humidity = hour.humidity,
                            windSpeed = hour.hourWindSpeed,
                            rotation = hour.windDirection
                        )
                    }
                }

                val location = LatLng(
                    model.lat,
                    model.lon
                )
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(location, 10f)
                }
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(top = 30.dp),
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

@Composable
fun TextAndIconForecast(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(top = 20.dp)
        .height(48.dp),
    painter: Painter,
    text: String,
    tint: Color = Color.White,
    iconSize: Dp = 20.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )

        ForecastScreenText(text = text)
    }
}

@Composable
fun ForecastScreenText(text: String) {
    Text(
        text = text,
        fontSize = 36.sp,
        color = Color.White,
    )
}

@Composable
fun ForecastScreenHourText(
    text: String,
    fontSize: TextUnit = 20.sp,
    padding: Dp = 1.dp
) {
    Text(
        text = text,
        fontSize = fontSize,
        color = Color.White,
        modifier = Modifier.padding(padding)
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
    rotation: Int
) {
    Surface(color = colorResource(id = R.color.jordi_blue)) {
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
                padding = 2.dp
            )

            Icon(
                painter = rememberAsyncImagePainter(iconCode.getIconUrl()),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(60.dp)
            )

            val formattedTemperature = UnitSystem.METRIC.mapTemperature(temperature)
                .formatTemperatureComposable(unitSystem = UnitSystem.METRIC)

            ForecastScreenHourText(text = formattedTemperature, fontSize = 28.sp)

            val formattedHumidity = humidity.formatHumidityComposable()
            ForecastScreenHourText(text = formattedHumidity)

            Row {
                val formattedSpeed = windSpeed.formatSpeedComposable(unitSystem = UnitSystem.METRIC)
                ForecastScreenHourText(text = formattedSpeed)

                //todo align the icon to the text baseline
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_direction),
                    "image",
                    Modifier
                        .size(16.dp)
                        .fillMaxSize()
                        .rotate(rotation.toFloat()),
                    tint = Color.White
                )
            }
        }
    }
}
