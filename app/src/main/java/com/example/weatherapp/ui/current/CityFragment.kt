package com.example.weatherapp.ui.current

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.databinding.CityWeatherFragmentBinding
import com.example.weatherapp.ui.MainActivityViewModel
import com.example.weatherapp.ui.hours.HourAdapter
import com.example.weatherapp.models.Measure
import com.example.weatherapp.ui.utils.setDrawable
import com.example.weatherapp.utils.ResourceProvider
import com.example.weatherapp.utils.moveToLocation
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class CityFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: CityFragmentViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var binding: CityWeatherFragmentBinding? = null

    private lateinit var measure: Measure

    private var saved: Boolean? = null

    @Inject
    lateinit var resourceProvider: ResourceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        measure = Measure.getMeasure(arguments?.get("measure") as? String)
        viewModel.setUpData(activityViewModel.model, measure)
    }

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
        //TODO see how to remove this check when room data is loaded
        viewModel.checkSavedLocation(activityViewModel.model!!.name)

        val hourAdapter = HourAdapter(resourceProvider)

        viewModel.hours.observe(viewLifecycleOwner) {
            hourAdapter.updateData(it)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Snackbar.make(view, it.toString(), Snackbar.LENGTH_LONG).show()
        }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.current_weather_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val saveItem = menu.findItem(R.id.action_save)
        toggleSavedIcon(saveItem)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                saved?.let {
                    if (!it) {
                        item.setDrawable(context, R.drawable.ic_heart_full)
                        viewModel.insertLocationAsSaved()
                        Snackbar.make(requireActivity().window.decorView,
                            "Location saved to favorites",
                            Snackbar.LENGTH_LONG).show()
                    } else {
                        item.setDrawable(context, R.drawable.ic_heart_empty)
                        viewModel.removeLocationFromFavorites()
                        Snackbar.make(requireActivity().window.decorView,
                            "Location removed from favorites",
                            Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
        return true
    }

    private fun toggleSavedIcon(saveItem: MenuItem) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.cityLocalModel.collect { dataModel ->
                saved = if (dataModel != null) {
                    saveItem.setDrawable(context, R.drawable.ic_heart_full)
                    true
                } else {
                    saveItem.setDrawable(context, R.drawable.ic_heart_empty)
                    false
                }
            }
        }
    }
}