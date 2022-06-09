package com.example.weatherapp.ui.utils

import android.content.Context
import android.view.MenuItem

fun MenuItem.setDrawable(context: Context?, id: Int){
    val drawable = context?.getDrawable(id)
    this.icon = drawable
}