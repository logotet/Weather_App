package com.example.weatherapp.ui

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.example.weatherapp.models.measure.Measure
import com.example.weatherapp.ui.utils.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ObservableViewModel() {

    var measure: Measure = Measure.METRIC

    @get:Bindable
    var barVisible: Boolean = false
    set(value) {
        field = value
        notifyPropertyChanged(BR.barVisible)
    }
}