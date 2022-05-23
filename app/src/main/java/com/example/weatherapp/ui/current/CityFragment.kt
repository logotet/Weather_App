package com.example.weatherapp.ui.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.weatherapp.R
import com.example.weatherapp.databinding.CityWeatherFragmentBinding
import com.example.weatherapp.ui.MainActivityViewModel
import com.example.weatherapp.ui.hours.HourAdapter
import com.example.weatherapp.utils.AppConstants
import com.example.weatherapp.utils.ResourceProvider
import com.example.weatherapp.utils.moveToLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CityFragment : Fragment(), OnMapReadyCallback {
    private val cityViewModel: CityFragmentViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var binding: CityWeatherFragmentBinding

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private var hourAdapter: HourAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.city_weather_fragment, container, false)
        binding = DataBindingUtil.bind(view)!!
        binding.viewModel = cityViewModel
        binding.lifecycleOwner = this
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hourAdapter = HourAdapter()
        hourAdapter!!.resourceProvider = resourceProvider
        binding.hoursRecView.adapter = hourAdapter
        cityViewModel.measure = activityViewModel.measure
        cityViewModel.cityWeatherModel = activityViewModel.model

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val lat = cityViewModel.cityWeatherModel!!.lat
        val lon = cityViewModel.cityWeatherModel!!.lon
        googleMap.moveToLocation(lat, lon)
    }
}