package com.example.weatherapp.ui.hours

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.HourViewBinding
import com.example.weatherapp.models.hourly.HourWeatherModel
import com.example.weatherapp.utils.Measure
import com.example.weatherapp.utils.ResourceProvider

class HourAdapter() :
    RecyclerView.Adapter<HourAdapter.HourHolder>() {
    var resourceProvider: ResourceProvider? = null
    var hours: List<HourWeatherModel> = emptyList()
    var measure: Measure = Measure.METRIC

    fun updateData(data: List<HourWeatherModel>?) {
        hours = data ?: emptyList()
        notifyDataSetChanged()
    }

    fun updateMeasureUnit(measure: Measure) {
        this.measure = measure
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourHolder {
        val binding = HourViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HourHolder(binding, measure, resourceProvider!!)
    }

    override fun onBindViewHolder(holder: HourHolder, position: Int) {
        holder.bind(hours[position])
    }

    override fun getItemCount(): Int =
        hours.size

    class HourHolder(
        private val binding: HourViewBinding,
        private val measure: Measure,
        private val resourceProvider: ResourceProvider,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val hourViewModel = HourViewModel(measure, resourceProvider)

        init {
            binding.viewmodel = hourViewModel
        }

        fun bind(hourWeatherModel: HourWeatherModel) {
            hourViewModel.hourModel = hourWeatherModel
            binding.hourTemperature.text = hourViewModel.hourTemperature
        }
    }
}