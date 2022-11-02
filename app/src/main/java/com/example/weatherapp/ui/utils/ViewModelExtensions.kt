package com.example.weatherapp.ui.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

suspend fun ViewModel.onNetworkAvailability(
    isAvlb: Boolean,
    onAvailable: () -> Unit,
    onUnavailable: () -> Unit,
) {
    if (isAvlb) {
        onAvailable()
    } else {
        onUnavailable()
    }
}

fun <T> ViewModel.emitEventIn(eventFlow: MutableSharedFlow<T>, eventDataProducer: () -> T) {
    viewModelScope.launch {
        eventFlow.emit(eventDataProducer.invoke())
    }
}

fun <T> ViewModel.emitDataIn(dataFlow: MutableStateFlow<T>, dataProducer: () -> T) {
    viewModelScope.launch {
        dataFlow.emit(dataProducer.invoke())
    }
}