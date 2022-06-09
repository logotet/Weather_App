package com.example.weatherapp.ui.saved

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.interactors.localcalls.GetFavoriteLocations
import com.example.weatherapp.interactors.localcalls.GetLocationByName
import com.example.weatherapp.models.current.CurrentWeatherModel
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.models.utils.mapLocalToCurrentModel
import com.example.weatherapp.ui.ObservableViewModel
import com.example.weatherapp.ui.saved.locations.OnSavedLocationClickedListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedLocationsFragmentViewModel @Inject constructor(
    private val getFavoriteLocations: GetFavoriteLocations,
    private val getLocationByName: GetLocationByName,
) : ObservableViewModel(), OnSavedLocationClickedListener {

    private var _locations = MutableLiveData<List<LocalWeatherModel>>()
    val locations: LiveData<List<LocalWeatherModel>>
        get() = _locations

    private var _selectedLocation = MutableSharedFlow<CurrentWeatherModel>()
    val selectedLocation: SharedFlow<CurrentWeatherModel>
    get() = _selectedLocation.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _locations.value = getFavoriteLocations.getFavoriteLocations()
        }
    }

    override fun onSavedLocationClicked(cityName: String) {
        viewModelScope.launch {
            getLocationByName.getCity(cityName).collect {model ->
                model?.let {
                    _selectedLocation.emit(it.mapLocalToCurrentModel())
                }
            }
        }
    }

}