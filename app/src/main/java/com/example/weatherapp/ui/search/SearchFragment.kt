package com.example.weatherapp.ui.search

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.ui.MainActivityViewModel
import com.example.weatherapp.ui.utils.isNetworkAvailable
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.constructLocationPermissionRequest


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var gpsActivationLaunched: Boolean = false

    private val firebaseAnalytics = Firebase.analytics

    override fun onResume() {
        super.onResume()
        viewModel.isNetworkAvailable = this.isNetworkAvailable()

        if (gpsActivationLaunched) {
            goToCoordinatesFragment()
        }
        gpsActivationLaunched = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SearchScreen(viewModel = viewModel,
                    { locationName ->
                        searchLocation(locationName)
                    },
                    {
                        constructLocationPermissionRequest(
                            LocationPermission.FINE,
                            onShowRationale = ::onGetLocationRationale,
                            onPermissionDenied = ::onLocationPermissionDenied,
                            requiresPermission = ::goToCoordinatesFragment
                        ).launch()
                    },
                    { unitSystem ->
                        activityViewModel.unitSystem = unitSystem
                    }
                )
            }
        }
    }

    private fun searchLocation(locationName: String?) {
        if (!locationName.isNullOrBlank()) {
            setNavigationWithData(locationName)
        } else {
            view?.let { view ->
                Snackbar.make(
                    view,
                    getString(R.string.valid_location),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setNavigationWithData(locationName: String) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToCurrentWeatherFragment(
                cityName = locationName
            )
        )
    }

    private fun onGetLocationRationale(permissionRequest: PermissionRequest) {
        permissionRequest.proceed()
    }

    private fun goToCoordinatesFragment() {
        if (isGPSEnabled()) {
            findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToCoordsFragment())
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            gpsActivationLaunched = true
        }
    }

    private fun onLocationPermissionDenied() {
        this.view?.let {
            Snackbar.make(it, getString(R.string.location_denied), Snackbar.LENGTH_LONG).show()
            activityViewModel.barVisible = false
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}





