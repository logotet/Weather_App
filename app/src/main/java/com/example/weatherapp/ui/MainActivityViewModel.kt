package com.example.weatherapp.ui

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.interactors.localcalls.locations.DeleteCacheMemory
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.ui.utils.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val deleteCacheMemory: DeleteCacheMemory,
) : ObservableViewModel() {

    var unitSystem: UnitSystem = UnitSystem.METRIC

    @get:Bindable
    var barVisible: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.barVisible)
        }

    init {
        //TODO use lifeccycle observer
        deleteUnsavedData()
    }

    fun deleteUnsavedData() {
        viewModelScope.launch {
            deleteCacheMemory.deleteCache()
        }
    }
}