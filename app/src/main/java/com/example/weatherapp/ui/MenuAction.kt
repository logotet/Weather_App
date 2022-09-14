package com.example.weatherapp.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.weatherapp.R

sealed class MenuAction(
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
    object Saved : MenuAction(R.string.favourites, R.drawable.ic_favorites_list)
}
