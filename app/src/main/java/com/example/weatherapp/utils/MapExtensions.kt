package com.example.weatherapp.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

fun GoogleMap.moveToLocation(lat: Double, lon: Double){
    val location = LatLng(lat, lon )
    this.addMarker(
        MarkerOptions()
            .position(location)
    )
    this.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
}