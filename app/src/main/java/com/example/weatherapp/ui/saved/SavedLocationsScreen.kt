package com.example.weatherapp.ui.saved

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.models.utils.formatTemperatureComposable
import com.example.weatherapp.models.utils.mapTemperature
import com.example.weatherapp.ui.Appbar

@Composable
fun SavedLocationsScreen(
    viewModel: SavedLocationsFragmentViewModel,
    selectLocation: (String) -> Unit
) {

//    viewModel.loadData()
    val savedLocations = viewModel.locations.collectAsState(initial = emptyList())

    Scaffold(topBar = {
        Appbar(
            title = stringResource(id = R.string.favorites_screen_title),
            menuItems = {}
        )
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            color = colorResource(id = R.color.jordi_blue),
        ) {
            LazyColumn() {
                items(savedLocations.value) { location ->
                    SavedLocationRow(localWeatherModel = location) { name ->
                        selectLocation(name)
                    }
                }
            }
        }
    }
}

@Composable
fun SavedLocationRow(
    localWeatherModel: LocalWeatherModel,
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

        val formattedTemperature = UnitSystem.METRIC.mapTemperature(localWeatherModel.temperature)
            .formatTemperatureComposable(unitSystem = UnitSystem.METRIC)

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

@Preview(showBackground = true)
@Composable
fun SomePreview() {
//        SavedLocationsScreen()
}