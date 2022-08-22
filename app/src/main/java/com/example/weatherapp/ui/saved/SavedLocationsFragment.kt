package com.example.weatherapp.ui.saved

import android.os.Bundle
import android.view.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.ui.MainActivityViewModel
import com.example.weatherapp.ui.utils.isNetworkAvailable
import com.example.weatherapp.utils.ResourceProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SavedLocationsFragment : Fragment() {
    private val viewModel: SavedLocationsFragmentViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

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
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SavedLocationsScreen(viewModel) { name ->
                    findNavController().navigate(
                        SavedLocationsFragmentDirections.actionSavedLocationsFragmentToCurrentWeatherFragment(
                            cityName = name
                        )
                    )
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_favorites)
        menuItem.isVisible = false
    }
}
