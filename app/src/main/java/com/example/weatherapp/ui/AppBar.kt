package com.example.weatherapp.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.weatherapp.R

@Composable
fun Appbar(
    title: String,
    menuItems: @Composable () -> Unit,
    navigateUp: () -> Unit,
    navigationIcon: @Composable () -> Unit = {
        IconButton(onClick = { navigateUp() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                tint = Color.White,
                contentDescription = "Back"
            )
        }
    }
) {
    TopAppBar(
        navigationIcon = navigationIcon,
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