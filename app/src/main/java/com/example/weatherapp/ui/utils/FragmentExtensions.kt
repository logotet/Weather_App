package com.example.weatherapp.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
fun Fragment.isNetworkAvailable(context: Context?): Boolean {
    val connectivityManager = context?.getSystemService(ConnectivityManager::class.java)
    return connectivityManager?.activeNetwork != null
}

fun <T> Fragment.collectEvent(sharedFlow: SharedFlow<T>, collectAction: (T) -> Unit) =
    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        sharedFlow.collectLatest {
            collectAction.invoke(it)
        }
    }
fun <T> Fragment.collectData(stateFlow: StateFlow<T>, onEmitAction: (T) -> Unit) =
    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        stateFlow.collectLatest {
            onEmitAction.invoke(it)
        }
    }

fun ViewModel.onNetworkAvailability(
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