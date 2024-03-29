package com.example.weatherapp.ui.saved

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.models.utils.formatTemperatureComposable
import com.example.weatherapp.models.utils.mapTemperature

@Composable
fun SavedLocationRow(
    localWeatherModel: LocalWeatherModel,
    units: UnitSystem,
    selectLocation: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 30.dp, end = 30.dp)
            .clickable(onClick = { selectLocation(localWeatherModel.name) }),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = localWeatherModel.name,
            fontSize = 40.sp,
            color = Color.White,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
        )

        val formattedTemperature = units.mapTemperature(localWeatherModel.temperature)
            .formatTemperatureComposable(unitSystem = units)

        Text(
            text = formattedTemperature,
            fontSize = 40.sp,
            color = Color.White,
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
        )
    }
}