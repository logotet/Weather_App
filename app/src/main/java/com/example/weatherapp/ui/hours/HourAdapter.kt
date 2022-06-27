package com.example.weatherapp.ui.hours

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.LayoutHourBinding
import com.example.weatherapp.models.ui.HourWeatherModel
import com.example.weatherapp.models.measure.UnitSystem
import com.example.weatherapp.utils.ResourceProvider

class HourAdapter(
    var resourceProvider: ResourceProvider
) :
    RecyclerView.Adapter<HourAdapter.HourHolder>() {
    var hours: List<HourWeatherModel> = emptyList()
    var unitSystem: UnitSystem = UnitSystem.METRIC

    fun updateData(data: List<HourWeatherModel>?) {
        hours = data ?: emptyList()
        notifyDataSetChanged()
    }

    fun updateMeasureUnit(unitSystem: UnitSystem) {
        this.unitSystem = unitSystem
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourHolder {
        val binding = LayoutHourBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HourHolder(binding, unitSystem, resourceProvider)
    }

    override fun onBindViewHolder(holder: HourHolder, position: Int) {
        holder.bind(hours[position])
    }

    override fun getItemCount(): Int =
        hours.size

    class HourHolder(
        private val binding: LayoutHourBinding,
        unitSystem: UnitSystem,
        resourceProvider: ResourceProvider,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val hourViewModel = HourViewModel(unitSystem, resourceProvider)

        init {
            binding.viewmodel = hourViewModel
        }

        fun bind(hourWeatherModel: HourWeatherModel) {
            hourViewModel.hourModel = hourWeatherModel
            binding.hourTemperature.text = hourViewModel.hourTemperature
        }
    }
}