package com.example.weatherapp.ui

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.weatherapp.R

@Composable
fun Appbar(
    title: String,
    menuItems: @Composable () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White
            )
        },
        backgroundColor = colorResource(id = R.color.royal_blue),
        actions = { menuItems() }
    )
}