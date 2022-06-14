package com.example.weatherapp.ui.search

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.RecentLocationViewBinding
import com.example.weatherapp.databinding.SearchFragmentBinding
import com.example.weatherapp.ui.MainActivityViewModel
import com.example.weatherapp.ui.utils.isNetworkAvailable
import com.example.weatherapp.utils.ResourceProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.constructLocationPermissionRequest
import javax.inject.Inject


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: SearchFragmentBinding

    private val viewModel: SearchViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var gpsActivationLaunched: Boolean = false
    private val cancellationTokenSource = CancellationTokenSource()

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val firebaseAnalytics = Firebase.analytics

    private var viewGroup: ViewGroup? = null

    override fun onResume() {
        super.onResume()
        viewModel.isNetworkAvailable = this.isNetworkAvailable(context)

        if (gpsActivationLaunched) {
            getCurrentLocation()
        }
        gpsActivationLaunched = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.search_fragment, container, false)
        binding = SearchFragmentBinding.bind(view)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewGroup = container
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val constructLocationPermissionRequest = constructLocationPermissionRequest(
            LocationPermission.FINE,
            onShowRationale = ::onGetLocationRationale,
            onPermissionDenied = ::onLocationPermissionDenied,
            requiresPermission = ::getCurrentLocation
        )

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.cityNames.collectLatest { recentLocations ->
                if (recentLocations.isNotEmpty()) {
                    binding.txtRecentlySearched.visibility = VISIBLE
                } else {
                    binding.txtRecentlySearched.visibility = INVISIBLE
                }

                for (location in recentLocations) {
                    val locationView =
                        LayoutInflater.from(context)
                            .inflate(R.layout.recent_location_view, viewGroup, false)
                    val cityNameBinding = RecentLocationViewBinding.bind(locationView)
                    cityNameBinding.txtRecentLocationName.text =
                        location.cityName
                    binding.recentlyViewed.addView(locationView)
                    locationView.setOnClickListener {
                        viewModel.onItemClicked(location.cityName)
                    }
                }
            }
        }

        viewModel.onLocationButtonPressed.observe(viewLifecycleOwner) {
            firebaseAnalytics.logEvent("weather_location_button_pressed", null)
            constructLocationPermissionRequest.launch()
        }

        viewModel.onSearchButtonPressed.observe(viewLifecycleOwner) {
            firebaseAnalytics.logEvent("weather_search_button_pressed", null)
        }

        viewModel.cityWeatherModel.observe(viewLifecycleOwner) {
            activityViewModel.model = it
        }

        viewModel.sharedMeasure.observe(viewLifecycleOwner) {
            activityViewModel.measure = it
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Snackbar.make(view, it.toString(), Snackbar.LENGTH_LONG).show()
        }

        viewModel.navigationFired.observe(viewLifecycleOwner) {
            val bundle = bundleOf("measure" to viewModel.measure.value)
            findNavController().navigate(R.id.action_searchFragment_to_currentWeatherFragment,
                bundle)
        }
    }

    private fun onGetLocationRationale(permissionRequest: PermissionRequest) {
        permissionRequest.proceed()
    }

    private fun getCurrentLocation() {
        if (isGPSEnabled()) {
            activityViewModel.barVisible = true

            fusedLocationClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnCompleteListener {task ->
                viewModel.getCoordWeather(task.result)
            }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            gpsActivationLaunched = true
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun onLocationPermissionDenied() {
        this.view?.let {
            Snackbar.make(it, getString(R.string.location_denied), Snackbar.LENGTH_LONG).show()
            activityViewModel.barVisible = false
        }
    }
}





