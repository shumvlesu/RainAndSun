package com.shumikhin.rainandsun.view.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.shumikhin.rainandsun.R
import com.shumikhin.rainandsun.databinding.FragmentMainBinding
import com.shumikhin.rainandsun.model.Weather
import com.shumikhin.rainandsun.view.details.DetailsFragment
import com.shumikhin.rainandsun.viewmodel.AppState
import com.shumikhin.rainandsun.viewmodel.MainViewModel

private const val IS_WORLD_KEY = "LIST_OF_TOWNS_KEY"

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    //private lateinit var viewModel: MainViewModel
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private var isDataSetRus: Boolean = true
    //private var isDataSetWorld: Boolean = false


    private val adapter = MainFragmentAdapter(object : OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
//            val manager = activity?.supportFragmentManager
//            if (manager != null) {
//                val bundle = Bundle()
//                bundle.putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
//                manager.beginTransaction()
//                    .add(R.id.container, DetailsFragment.newInstance(bundle))
//                    .addToBackStack("")
//                    .commitAllowingStateLoss()
//          оптимизируем
            activity?.supportFragmentManager?.apply {
                beginTransaction()
                    .add(R.id.container, DetailsFragment.newInstance(Bundle().apply {
                        putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                    }))
                    .addToBackStack("")
                    .commitAllowingStateLoss()
            }
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainFragmentRecyclerView.adapter = adapter
        binding.mainFragmentFAB.setOnClickListener { changeWeatherDataSet() }
        //viewModel = ViewModelProvider(this).get(MainViewModel::class.java) //сделана ленивая инициализация в стр.24 у переменной viewModel
        viewModel.getLiveData().observe(viewLifecycleOwner, Observer { renderData(it) })
        //viewModel.getWeatherFromLocalSourceRus()
        showListOfTowns()

    }

    //Пытаемся получить настройки из SharedPreferences
    private fun showListOfTowns() {
        activity?.let {
            if (it.getPreferences(Context.MODE_PRIVATE).getBoolean(IS_WORLD_KEY, false)) {//значения там нет, то возвращаем false по умолчанию
                changeWeatherDataSet()
            } else {
                viewModel.getWeatherFromLocalSourceRus()
            }
        }
    }


    override fun onDestroy() {
        adapter.removeListener()
        super.onDestroy()
    }

    private fun changeWeatherDataSet() =
        if (isDataSetRus) {
            viewModel.getWeatherFromLocalSourceWorld()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        } else {
            viewModel.getWeatherFromLocalSourceRus()
            binding.mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        }.also {
            isDataSetRus = !isDataSetRus
            saveListOfTowns()
        }

    //Сохраняем настройки в SharedPreference
    private fun saveListOfTowns() {
        activity?.let {
                with(it.getPreferences(Context.MODE_PRIVATE).edit()) { //Получить Preferences. Оставшиеся ключи кроме  MODE_PRIVATE устарели.
                putBoolean(IS_WORLD_KEY, !isDataSetRus) //Сохранить настройки
                apply() // Сохранение при помощи команды commit немедленное в основном потоке, apply — асинхронное
            }
        }
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
                //binding.mainFragmentLoadingLayout.visibility = View.GONE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Loading -> {
                //binding.mainFragmentLoadingLayout.visibility = View.VISIBLE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
            }
            is AppState.Error -> {
                //binding.mainFragmentLoadingLayout.visibility = View.GONE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                // Snackbar.make(
                //  binding.mainFragmentFAB,
                // getString(R.string.error),
                //  Snackbar.LENGTH_INDEFINITE
                // )
                // .setAction(getString(R.string.reload)) { viewModel.getWeatherFromLocalSourceRus() }
                // .show()

                //binding.mainFragmentLoadingLayout.showSnackBar(
                binding.mainFragmentRootView.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    { viewModel.getWeatherFromLocalSourceRus() })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //extension-функция расширяющия функционал класса View без его изменения
    private fun View.showSnackBar(
        text: String,
        actionText: String,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ) {
        Snackbar.make(this, text, length).setAction(actionText, action).show()
    }

    interface OnItemViewClickListener {
        fun onItemViewClick(weather: Weather)
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}


