package com.example.weatherapp.ui.saved

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentSavedLocationsBinding
import com.example.weatherapp.ui.saved.locations.LocationAdapter
import com.example.weatherapp.ui.utils.isNetworkAvailable
import com.example.weatherapp.utils.ResourceProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SavedLocationsFragment : Fragment(){
    private val viewModel: SavedLocationsFragmentViewModel by viewModels()

    private var binding: FragmentSavedLocationsBinding? = null

    @Inject
    lateinit var resourceProvider: ResourceProvider

    override fun onResume() {
        super.onResume()
        viewModel.isNetworkAvailable = isNetworkAvailable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saved_locations, container, false)
        binding = DataBindingUtil.bind(view)
        binding?.viewmodel = viewModel
        binding?.lifecycleOwner = this
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationAdapter = LocationAdapter(resourceProvider, viewModel)

        viewModel.loadData()

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.locations.collect {
                locationAdapter.updateData(it)
            }
        }

        binding?.locationsRecView?.adapter = locationAdapter

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.selectedLocation.collectLatest{
                findNavController().navigate(SavedLocationsFragmentDirections.actionSavedLocationsFragmentToCurrentWeatherFragment(
                    cityName = it
                ))
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_favorites)
        menuItem.isVisible = false
    }
}