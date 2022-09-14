package com.example.weatherapp.ui.coords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.databinding.FragmentGpsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GPSFragment : Fragment() {
    private var binding: FragmentGpsBinding? = null
    private val viewModel: GPSFragmentViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val cancellationTokenSource = CancellationTokenSource()
    private var lat: Double? = null
    private var lon: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
//                GPSScreen()
            }
        }
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

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.errorMessage.collect {
                setBackNavigation(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource.cancel()
    }

    private fun getCurrentLocation() {
        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            location?.let {
                lat = location.latitude
                lon = location.longitude
                viewModel.setUpData(lat, lon)
            }
        }
    }

    private fun setNavigationWithData(locationName: String) {
        findNavController().navigate(
            GPSFragmentDirections.actionCoordsFragmentToCurrentWeatherFragment2(
                cityName = locationName
            )
        )
    }

    private fun setBackNavigation(error: String) {
        activity?.onBackPressed()
        view?.let { Snackbar.make(it, error, Snackbar.LENGTH_SHORT).show() }
    }
}