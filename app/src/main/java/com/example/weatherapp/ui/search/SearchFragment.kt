package com.example.weatherapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.SearchFragmentBinding
import com.example.weatherapp.ui.MainActivityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.constructLocationPermissionRequest


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: SearchFragmentBinding

    private val viewModel: SearchViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.search_fragment, container, false)
        binding = SearchFragmentBinding.bind(view)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
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

        viewModel.onLocationButtonPressed.observe(viewLifecycleOwner) {
            activityViewModel.barVisible = true //TODO

            constructLocationPermissionRequest.launch()
        }

        viewModel.cityWeatherModel?.observe(viewLifecycleOwner) {
            activityViewModel.model = it

            val bundle = bundleOf("measure" to viewModel.measure.value)
            findNavController().navigate(R.id.action_searchFragment_to_currentWeatherFragment,
                bundle)
        }

        viewModel.sharedMeasure.observe(viewLifecycleOwner, Observer {
            activityViewModel.measure = it
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
        })
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





