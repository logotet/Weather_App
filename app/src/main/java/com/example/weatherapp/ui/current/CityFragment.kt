package com.example.weatherapp.ui.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.weatherapp.R
import com.example.weatherapp.databinding.CityWeatherFragmentBinding
import com.example.weatherapp.ui.MainActivityViewModel
import com.example.weatherapp.ui.hours.HourAdapter
import com.example.weatherapp.models.Measure
import com.example.weatherapp.utils.ResourceProvider
import com.example.weatherapp.utils.moveToLocation
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CityFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: CityFragmentViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var binding: CityWeatherFragmentBinding? = null

    @Inject
    lateinit var resourceProvider: ResourceProvider


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.city_weather_fragment, container, false)
        binding = DataBindingUtil.bind(view)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hourAdapter = HourAdapter(resourceProvider)

        val measure = Measure.getMeasure(arguments?.get("measure") as? String)
        viewModel.setUpData(activityViewModel.model, measure)

        viewModel.hours.observe(viewLifecycleOwner, Observer {
            hourAdapter.updateData(it)
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(view, it.toString(), Snackbar.LENGTH_LONG).show()
        })

        hourAdapter.updateMeasureUnit(measure)
        hourAdapter.resourceProvider = resourceProvider
        binding?.hoursRecView?.adapter = hourAdapter

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        activityViewModel.barVisible = false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        activityViewModel.model?.let {
            val lat = it.lat
            val lon = it.lon
            googleMap.moveToLocation(lat, lon)
        }
    }
}