package com.example.weatherapp.ui.saved.locations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.LayoutLocationRowBinding
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.utils.ResourceProvider

class LocationAdapter(
    private val resourceProvider: ResourceProvider,
    private val onSavedLocationClickedListener: OnSavedLocationClickedListener
) : RecyclerView.Adapter<LocationAdapter.LocationHolder>() {

    private var locations: List<LocalWeatherModel> = emptyList()
    var unitSystem: UnitSystem? = null

    fun updateData(data: List<LocalWeatherModel>?) {
        locations = data ?: emptyList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        val binding = LayoutLocationRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationHolder(binding, resourceProvider, onSavedLocationClickedListener)
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder.bind(locations[position])
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    inner class LocationHolder(
        binding: LayoutLocationRowBinding,
        resourceProvider: ResourceProvider,
        onSavedLocationClickedListener: OnSavedLocationClickedListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private val locationViewModel: LocationRowViewModel = LocationRowViewModel(resourceProvider, onSavedLocationClickedListener)

        init {
            locationViewModel.unitSystem = unitSystem
            binding.viewmodel = locationViewModel
        }

        fun bind(locationRow: LocalWeatherModel) {
            locationViewModel.location = locationRow
        }
    }
}