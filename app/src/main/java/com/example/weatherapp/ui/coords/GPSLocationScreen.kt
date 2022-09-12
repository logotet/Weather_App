package com.example.weatherapp.ui.coords

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.R

@SuppressLint("UnrememberedMutableState")
@Composable
fun GPSScreen() {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.location_animation))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        modifier = Modifier.fillMaxSize(),
        composition = composition,
        progress = progress,
    )
}