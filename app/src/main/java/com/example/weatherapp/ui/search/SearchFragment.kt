package com.example.weatherapp.ui.search

import android.os.Bundle
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
import com.example.weatherapp.databinding.RecentLocationViewBinding
import com.example.weatherapp.databinding.SearchFragmentBinding
import com.example.weatherapp.ui.MainActivityViewModel
import com.example.weatherapp.ui.utils.isNetworkAvailable
import com.example.weatherapp.utils.ResourceProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val firebaseAnalytics = Firebase.analytics

    private var viewGroup: ViewGroup? = null

    override fun onResume() {
        super.onResume()
        viewModel.isNetworkAvailable = this.isNetworkAvailable(context)

        if (gpsActivationLaunched) {
            goToCoordsFragment()
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
            requiresPermission = ::goToCoordsFragment
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

        viewModel.onSearchButtonPressed.observe(viewLifecycleOwner) {
            activityViewModel.unitSystem = viewModel.unitSystem
            firebaseAnalytics.logEvent("weather_search_button_pressed", null)
            setNavigationWithData()
        }

        viewModel.onLocationButtonPressed.observe(viewLifecycleOwner) {
            activityViewModel.unitSystem = viewModel.unitSystem
            binding.edtCity.text = null
            firebaseAnalytics.logEvent("weather_location_button_pressed", null)
            constructLocationPermissionRequest.launch()
        }
    }

    private fun setNavigationWithData() {
        findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToCurrentWeatherFragment(
           location = viewModel.cityName
        ))
    }

    private fun onGetLocationRationale(permissionRequest: PermissionRequest) {
        permissionRequest.proceed()
    }

    private fun goToCoordsFragment() {
        findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToCoordsFragment())
    }

    private fun onLocationPermissionDenied() {
        this.view?.let {
            Snackbar.make(it, getString(R.string.location_denied), Snackbar.LENGTH_LONG).show()
            activityViewModel.barVisible = false
        }
    }
}





