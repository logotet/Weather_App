package com.example.weatherapp.ui.coords

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentGpsBinding
import com.example.weatherapp.ui.MainActivityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class GPSFragment : Fragment() {
    private var binding: FragmentGpsBinding? = null
    private val viewModel: GPSFragmentViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var gpsActivationLaunched: Boolean = false
    private val cancellationTokenSource = CancellationTokenSource()
    private var lat: Double? = null
    private var lon: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_gps, container, false)
        binding = DataBindingUtil.bind(view)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getCurrentLocation()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.locationName.collect {
                setNavigationWithData(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource.cancel()
    }

    private fun getCurrentLocation() {
        if (isGPSEnabled()) {
            fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                location?.let {
                    lat = location.latitude
                    lon = location.longitude
                    viewModel.setUpData(lat, lon, activityViewModel.unitSystem)
                }
            }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            gpsActivationLaunched = true
        }
    }

    private fun setNavigationWithData(locationName: String) {
        findNavController().navigate(GPSFragmentDirections.actionCoordsFragmentToCurrentWeatherFragment2(
            cityName = locationName
        ))
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}