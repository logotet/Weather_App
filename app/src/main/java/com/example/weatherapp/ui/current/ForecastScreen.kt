package com.example.weatherapp.ui.current

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ForecastScreen(viewModel: ForecastViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    Surface(color = colorResource(id = R.color.jordi_blue)) {

        Column(modifier = Modifier.fillMaxSize(1f)) {
            val cityName by viewModel.locationName.collectAsState()

            TextAndIconWorkTitle(
                painter = painterResource(id = R.drawable.ic_location_name),
                text = cityName!!
            )

            TextAndIconWorkTitle(
                painter = rememberAsyncImagePainter("https://openweathermap.org/img/wn/03d@2x.png"),
                text = "32C"
            )

            Text(
                text = "broken clouds",
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
                TextAndIconWorkTitle(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .height(48.dp)
                        .padding(horizontal = 20.dp),
                    painter = painterResource(id = R.drawable.ic_raindrop),
                    text = "58%"
                )

                TextAndIconWorkTitle(
                    Modifier
                        .padding(top = 20.dp)
                        .height(48.dp)
                        .padding(horizontal = 20.dp),
                    painter = painterResource(id = R.drawable.ic_arrow_direction),
                    text = "2.2m/s"
                )
            }

            Text(
                text = "Next 48 hours",
                fontSize = 24.sp,
                color = Color.White
            )

//            val hours = viewModel.hours.collectAsState()

//            LazyRow {
//                items(hours.value) { hour ->
//                    Text(text = hour.hour.toString())
//                }
//            }

            val singapore = LatLng(1.35, 103.87)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(singapore, 10f)
            }
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    position = singapore,
                    title = "Singapore",
                    snippet = "Marker in Singapore"
                )
            }
        }
    }
}


@Composable
fun TextAndIconWorkTitle(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(top = 20.dp)
        .height(48.dp),
    painter: Painter,
    text: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = Color.White
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
