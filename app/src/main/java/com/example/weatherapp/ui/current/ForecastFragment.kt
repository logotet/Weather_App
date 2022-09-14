package com.example.weatherapp.ui.current

import android.os.Bundle
import android.view.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.R
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.ui.MainActivityViewModel
import com.example.weatherapp.ui.utils.isNetworkAvailable
import com.example.weatherapp.ui.utils.setDrawable
import com.example.weatherapp.utils.ResourceProvider
import com.example.weatherapp.utils.moveToLocation
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForecastFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: ForecastViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private val args: ForecastFragmentArgs by navArgs()

    private lateinit var unitSystem: UnitSystem

    private var saved: Boolean? = null

    @Inject
    lateinit var resourceProvider: ResourceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
//                ForecastScreen()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        unitSystem = activityViewModel.unitSystem
        viewModel.setupData(args.cityName, unitSystem)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        lifecycleScope.launch {
            viewModel.coords.collectLatest {
                it?.let {
                    googleMap.moveToLocation(it.lat, it.lon)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.current_weather_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val saveItem = menu.findItem(R.id.action_save)
        if (!this.isNetworkAvailable()) {
            saveItem.isVisible = false
        }
        toggleSavedIcon(saveItem)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                saved?.let {
                    if (!it) {
                        item.setDrawable(context, R.drawable.ic_heart_full)
                        viewModel.saveLocationToFavorites()
                        Snackbar.make(
                            requireActivity().window.decorView,
                            "Location saved to favorites",
                            Snackbar.LENGTH_LONG
                        ).show()
                    } else {
                        item.setDrawable(context, R.drawable.ic_heart_empty)
                        viewModel.removeLocationFromFavorites()
                        Snackbar.make(
                            requireActivity().window.decorView,
                            "Location removed from favorites",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        return true
    }

    private fun toggleSavedIcon(saveItem: MenuItem) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.locationName.collect { name ->
                saved = if (name != null) {
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