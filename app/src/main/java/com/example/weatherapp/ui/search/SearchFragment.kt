package com.example.weatherapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.SearchFragmentBinding
import com.example.weatherapp.models.utils.mapLocalToCurrentModel
import com.example.weatherapp.ui.MainActivityViewModel
import com.example.weatherapp.utils.ResourceProvider
import com.example.weatherapp.utils.formatTemperature
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val firebaseAnalytics = Firebase.analytics

    private var viewGroup: ViewGroup? = null

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
            viewModel.locations.collectLatest { recentLocations ->
                if(recentLocations.isNotEmpty()){
                    binding.txtRecentlySearched.visibility = VISIBLE
                }
                for (location in recentLocations) {
                    val locationView =
                        LayoutInflater.from(context)
                            .inflate(R.layout.recent_location_view, viewGroup, false)
                    locationView.findViewById<TextView>(R.id.txt_recent_location_name).text =
                        location.name
                    binding.recentlyViewed.addView(locationView)
                    locationView.setOnClickListener {
                        activityViewModel.model = location.mapLocalToCurrentModel()
                        findNavController().navigate(R.id.currentWeatherFragment)
                    }
                }
            }
        }

        viewModel.onLocationButtonPressed.observe(viewLifecycleOwner) {
            firebaseAnalytics.logEvent("weather_location_button_pressed", null)
            activityViewModel.barVisible = true
            constructLocationPermissionRequest.launch()
        }

        viewModel.onSearchButtonPressed.observe(viewLifecycleOwner) {
            firebaseAnalytics.logEvent("weather_search_button_pressed", null)
        }

        viewModel.cityWeatherModel.observe(viewLifecycleOwner) {
            activityViewModel.model = it
        }

        viewModel.sharedMeasure.observe(viewLifecycleOwner, Observer {
            activityViewModel.measure = it
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(view, it.toString(), Snackbar.LENGTH_LONG).show()
        })

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
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnCompleteListener {
            viewModel.getCoordWeather(it.result)
        }
    }

    private fun onLocationPermissionDenied() {
        this.view?.let {
            Snackbar.make(it, getString(R.string.location_denied), Snackbar.LENGTH_LONG).show()
        }
    }
}





