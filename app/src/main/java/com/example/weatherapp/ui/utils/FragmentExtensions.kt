package com.example.weatherapp.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

@SuppressLint("MissingPermission")
fun Fragment.isNetworkAvailable(context: Context?): Boolean {
    val connectivityManager = context?.getSystemService(ConnectivityManager::class.java)
    return connectivityManager?.activeNetwork != null
}

fun ViewModel.onNetworkAvailability(
    isAvlb: Boolean,
    onAvailable: () -> Unit,
    onUnavailable: () -> Unit,
) {
    if (isAvlb) {
        onAvailable()
    } else {
        onUnavailable()
    }
}