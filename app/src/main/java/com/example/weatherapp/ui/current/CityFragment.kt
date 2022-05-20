package com.example.weatherapp.ui.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.weatherapp.R
import com.example.weatherapp.databinding.CityWeatherFragmentBinding
import com.example.weatherapp.ui.MainActivityViewModel
import com.example.weatherapp.ui.hours.HourAdapter
import com.example.weatherapp.utils.ResourceProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CityFragment : Fragment() {
    private val cityViewModel: CityFragmentViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var binding: CityWeatherFragmentBinding
    @Inject lateinit var resourceProvider: ResourceProvider
    private var hourAdapter: HourAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.city_weather_fragment, container, false)
        binding = DataBindingUtil.bind(view)!!
        binding.viewModel = cityViewModel
        binding.lifecycleOwner = this
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hourAdapter = HourAdapter()
        hourAdapter!!.resourceProvider = resourceProvider
        binding.hoursRecView.adapter = hourAdapter
        cityViewModel.measure = activityViewModel.measure
        cityViewModel.cityWeatherModel = activityViewModel.model
    }
}