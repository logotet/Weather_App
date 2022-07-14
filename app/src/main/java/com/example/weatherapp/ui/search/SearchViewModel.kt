package com.example.weatherapp.ui.search

import androidx.databinding.Bindable
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.interactors.localcalls.citynames.GetRecentCityNames
import com.example.weatherapp.models.local.City
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.ui.utils.ObservableViewModel
import com.example.weatherapp.utils.ResourceProvider
import com.example.weatherapp.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getRecentCityNames: GetRecentCityNames,
    private val resourceProvider: ResourceProvider,
) : ObservableViewModel(), OnRecentClickListener {

    @Bindable
    var cityName: String? = null

    @Bindable
    var unitSystem: UnitSystem = UnitSystem.METRIC
        set(value) {
            field = value
            notifyChange()
        }

    @get:Bindable
    val unitSystemTitle: String
        get() {
            return when (unitSystem) {
                UnitSystem.METRIC -> resourceProvider.getString(R.string.metric)
                UnitSystem.STANDARD -> resourceProvider.getString(R.string.standard)
                UnitSystem.IMPERIAL -> resourceProvider.getString(R.string.imperial)
            }
        }

    private var _locations = MutableStateFlow(emptyList<LocalWeatherModel>())
    val locations: StateFlow<List<LocalWeatherModel>> = _locations

    private var _cityNames = MutableStateFlow(emptyList<City>())
    val cityNames: StateFlow<List<City>> = _cityNames

    private var _onLocationButtonPressed = SingleLiveEvent<Unit>()
    val onLocationButtonPressed: SingleLiveEvent<Unit>
        get() = _onLocationButtonPressed

    private var _onSearchButtonPressed = SingleLiveEvent<Unit>()
    val onSearchButtonPressed: SingleLiveEvent<Unit>
        get() = _onSearchButtonPressed

    var isNetworkAvailable: Boolean = true

    init {
        getRecentCityNames()
    }

    fun onCurrentLocationPressed() {
        _onLocationButtonPressed.call()
    }

    fun onSearchPressed() {
        _onSearchButtonPressed.call()
    }

    private fun getRecentCityNames() {
        viewModelScope.launch {
            getRecentCityNames.getRecentCityNames().collect {
                _cityNames.value = it
            }
        }
    }

    override fun onItemClicked(city: String) {
        cityName = city
        onSearchPressed()
    }
}