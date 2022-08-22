package com.example.weatherapp.ui.current

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.example.weatherapp.R

@Composable
fun ForecastScreen(viewModel: ForecastViewModel) {
    Surface(color = colorResource(id = R.color.jordi_blue)) {
        val cityName = viewModel.locationName.collectAsState()

        Column() {
            Row() {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location_name),
                    contentDescription = null
                )

//                    cityName.value?.let {
//                        Text(text = it, textColor = Color.White) }
            }
        }
    }
}