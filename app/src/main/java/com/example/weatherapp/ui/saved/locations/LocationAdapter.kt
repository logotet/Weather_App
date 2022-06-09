package com.example.weatherapp.ui.saved.locations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.LocationRowBinding
import com.example.weatherapp.models.local.LocalWeatherModel
import com.example.weatherapp.utils.ResourceProvider

class LocationAdapter(
    private val resourceProvider: ResourceProvider,
    private val onSavedLocationClickedListener: OnSavedLocationClickedListener
) : RecyclerView.Adapter<LocationAdapter.LocationHolder>() {

    private var locations: List<LocalWeatherModel> = emptyList()

    fun updateData(data: List<LocalWeatherModel>?) {
        locations = data ?: emptyList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        val binding = LocationRowBinding.inflate(
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
        binding: LocationRowBinding,
        resourceProvider: ResourceProvider,
        onSavedLocationClickedListener: OnSavedLocationClickedListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private val locationViewModel: LocationRowViewModel = LocationRowViewModel(resourceProvider, onSavedLocationClickedListener)

        init {
            binding.viewmodel = locationViewModel
        }

        fun bind(locationRow: LocalWeatherModel) {
            locationViewModel.location = locationRow
        }
    }
}