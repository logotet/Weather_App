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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSearchBinding
import com.example.weatherapp.databinding.LayoutRecentLocationBinding
import com.example.weatherapp.ui.MainActivityViewModel
import com.example.weatherapp.ui.utils.isNetworkAvailable
import com.example.weatherapp.utils.ResourceProvider
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
    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var gpsActivationLaunched: Boolean = false

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val firebaseAnalytics = Firebase.analytics

    private var viewGroup: ViewGroup? = null

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
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        binding = FragmentSearchBinding.bind(view)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewGroup = container
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val constructLocationPermissionRequest = constructLocationPermissionRequest(
            LocationPermission.FINE,
            onShowRationale = ::onGetLocationRationale,
            onPermissionDenied = ::onLocationPermissionDenied,
            requiresPermission = ::goToCoordinatesFragment
        )

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.units.collectLatest {
                activityViewModel.unitSystem = it
            }
        }

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
                            .inflate(R.layout.layout_recent_location, viewGroup, false)
                    val cityNameBinding = LayoutRecentLocationBinding.bind(locationView)
                    cityNameBinding.txtRecentLocationName.text =
                        location.cityName
                    binding.recentlyViewed.addView(locationView)
                    locationView.setOnClickListener {
                        viewModel.onItemClicked(location.cityName)
                    }
                }
            }
        }

        viewModel.onSearchButtonPressed.observe(viewLifecycleOwner) {
            firebaseAnalytics.logEvent(getString(R.string.search_button_pressed), null)
            if (!viewModel.cityName.isNullOrBlank()) {
                setNavigationWithData()
            } else {
                getView()?.let { view ->
                    Snackbar.make(view,
                        getString(R.string.valid_location),
                        Snackbar.LENGTH_LONG).show()
                }
            }
        }

        viewModel.onLocationButtonPressed.observe(viewLifecycleOwner) {
            binding.edtCity.text = null
            firebaseAnalytics.logEvent(getString(R.string.location_button_pressed), null)
            constructLocationPermissionRequest.launch()
        }
    }

    private fun setNavigationWithData() {
        findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToCurrentWeatherFragment(
            cityName = viewModel.cityName
        ))
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





