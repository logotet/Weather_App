package com.example.weatherapp.ui.current

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.R
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.models.utils.*

@Composable
fun TextAndIconForecast(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
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
    modifier: Modifier = Modifier,
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
            modifier = modifier
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