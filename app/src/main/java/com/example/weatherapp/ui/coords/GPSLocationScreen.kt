package com.example.weatherapp.ui.coords

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R
import com.example.weatherapp.ui.Appbar

@SuppressLint("UnrememberedMutableState")
@Composable
fun GPSScreen() {
    Scaffold(topBar = {
        Appbar(
            title = stringResource(id = R.string.gps_screen_title),
            menuItems = {}
        )
    }) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.location_animation))
            val progress by animateLottieCompositionAsState(composition)
            LottieAnimation(
                modifier = Modifier.fillMaxSize(),
                composition = composition,
                progress = progress,
            )
        }
    }

}