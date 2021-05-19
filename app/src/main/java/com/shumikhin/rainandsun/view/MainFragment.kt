package com.shumikhin.rainandsun.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.shumikhin.rainandsun.R
import com.shumikhin.rainandsun.R.string.*
import com.shumikhin.rainandsun.databinding.MainFragmentBinding
import com.shumikhin.rainandsun.model.Weather
import com.shumikhin.rainandsun.viewmodel.AppState
import com.shumikhin.rainandsun.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //return inflater.inflate(R.layout.main_fragment, container, false)
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Обнуляйте _binding в onDestroyView чтобы избежать утечек и нежелаемого поведения.
        // В Активити ничего похожего делать не нужно.
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        //С помощью метода observe() мы можем подписаться на изменения в LiveData и получать
        //обновлённые данные каждый раз, когда вызван один из методов для передачи данных в эту LiveData
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer { renderData(it) })

        //Назначаю слушатель при свайпе вниз
        binding.swipeRefreshLayout.setOnRefreshListener() {
            viewModel.getWeatherFromRemoteSource()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        //Первый запрос за погодой
        viewModel.getWeatherFromLocalSource()
    }

    // В качестве аргумента renderData
    //принимает объект, возвращаемый LiveData. Далее мы вызываем у созданной ViewModel метод
    //getData, который возвращает нам LiveData, и вызываем у LiveData метод observe, который и
    //передаём в жизненный цикл вместе с Observer’ом. Теперь, если данные, которые хранит LiveData,
    //изменятся, Observer сразу об этом узнает и вызовет метод renderData, в который передаст новые
    //данные.
    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                val weatherData = appState.weatherData
                binding.loadingLayout.visibility = View.GONE
                setData(weatherData)
            }
            is AppState.Loading -> {
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.loadingLayout.visibility = View.GONE
                Snackbar.make(binding.mainView, error, Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(reload)) { viewModel.getWeatherFromLocalSource() }
                    .show()
            }
        }
    }

    private fun setData(weatherData: Weather) {
        binding.cityName.text = weatherData.city.city
        binding.cityCoordinates.text = String.format(
            getString(R.string.city_coordinates),
            weatherData.city.lat.toString(),
            weatherData.city.lon.toString()
        )
        binding.temperatureValue.text = weatherData.temperature.toString()
        binding.feelsLikeValue.text = weatherData.feelsLike.toString()
    }

}