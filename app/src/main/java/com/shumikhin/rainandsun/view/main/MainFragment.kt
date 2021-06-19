package com.shumikhin.rainandsun.view.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.shumikhin.rainandsun.R
import com.shumikhin.rainandsun.app.AppState
import com.shumikhin.rainandsun.databinding.FragmentMainBinding
import com.shumikhin.rainandsun.model.City
import com.shumikhin.rainandsun.model.Weather
import com.shumikhin.rainandsun.view.details.DetailsFragment
import com.shumikhin.rainandsun.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.IOException

private const val IS_WORLD_KEY = "LIST_OF_TOWNS_KEY"
private const val REQUEST_CODE = 23
private const val REFRESH_PERIOD = 60000L
private const val MINIMAL_DISTANCE = 100f

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    //private lateinit var viewModel: MainViewModel
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private var isDataSetRus: Boolean = true
    //private var isDataSetWorld: Boolean = false


//    private val adapter = MainFragmentAdapter(object : OnItemViewClickListener {
//        override fun onItemViewClick(weather: Weather) {
////            val manager = activity?.supportFragmentManager
////            if (manager != null) {
////                val bundle = Bundle()
////                bundle.putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
////                manager.beginTransaction()
////                    .add(R.id.container, DetailsFragment.newInstance(bundle))
////                    .addToBackStack("")
////                    .commitAllowingStateLoss()
////          оптимизируем
//            activity?.supportFragmentManager?.apply {
//                beginTransaction()
//                    .add(R.id.container, DetailsFragment.newInstance(Bundle().apply {
//                        putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
//                    }))
//                    .addToBackStack("")
//                    .commitAllowingStateLoss()
//            }
//        }
//    })


    private val adapter = MainFragmentAdapter(object : OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
            openDetailsFragment(weather)
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
        binding.mainFragmentFABLocation.setOnClickListener { checkPermission() }
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

    private fun checkPermission() {
        activity?.let {
            when {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                    getLocation()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    showRationaleDialog()
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }

    //Если пользователь уже отказывал в разрешении, то отображаем диалоговое окно с объяснением,
    //прежде чем запрашивать доступ
    private fun showRationaleDialog() {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_rationale_title))
                .setMessage(getString(R.string.dialog_rationale_meaasge))
                .setPositiveButton(getString(R.string.dialog_rationale_give_access)) { _, _ -> requestPermission() }
                .setNegativeButton(getString(R.string.dialog_rationale_decline)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    //Если доступа нет, то запрашиваем
    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_CODE)
    }

    //слушаем результат
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        checkPermissionsResult(requestCode, grantResults)
    }

    private fun checkPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {

                var grantedPermissions = 0

                if ((grantResults.isNotEmpty())) {

                    for (i in grantResults) {
                        if (i == PackageManager.PERMISSION_GRANTED) {grantedPermissions++}
                    }

                    if (grantResults.size == grantedPermissions) {
                        getLocation()
                    } else {
                        showDialog(
                            getString(R.string.dialog_title_no_gps),
                            getString(R.string.dialog_message_no_gps)
                        )
                    }

                } else {
                    showDialog(
                        getString(R.string.dialog_title_no_gps),
                        getString(R.string.dialog_message_no_gps)
                    )
                }

                return
            }
        }
    }

    //Если разрешения нет или разрешения не выданы пользователем, то отображаем диалоговое окно. В
    //нём уведомляем пользователя, что, если он хочет получать погоду по координатам, то нужно дать
    //разрешение на доступ к GPS. Сам диалог отображается в универсальном методе, с помощью
    //которого мы будем вызывать и другие диалоги
    private fun showDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }


    private fun getLocation() {
        activity?.let { context ->
            if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Получить менеджер геолокаций
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val provider = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    provider?.let {
                        // Будем получать геоположение через каждые 60 секунд или каждые 100 метров
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            REFRESH_PERIOD,
                            MINIMAL_DISTANCE,
                            onLocationListener
                        )
                    }
                } else {
                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location == null) {
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_location_unknown)
                        )
                    } else {
                        getAddressAsync(context, location)
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_known_location)
                        )
                    }
                }
            } else {
                showRationaleDialog()
            }
        }
    }

    //мы передаём слушателя, который и будет получать новые координаты
    //У listener’а нужно переопределить четыре метода:
    //1. onLocationChanged вызывается, когда приходят новые данные о местоположении.
    //2. onStatusChanged вызывается при изменении статуса: Available или Unavailable. На
    //Android Q и выше он всегда будет возвращать Available.
    //3. onProviderEnabled вызывается, если пользователь включил GPS.
    //4. onProviderDisabled вызывается, если пользователь выключил GPS. Вызывается сразу, если GPS был отключён изначально.
    private val onLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            context?.let {getAddressAsync(it, location)}
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    //С помощью класса Android Geocoder мы можем получить адрес по координатам. Этот класс
    //запрашивает данные у серверов Google по интернету, поэтому нам нужно не только разрешение на
    //выход в интернет в манифесте, но и отдельный поток для такого запроса. Передаём широту, долготу и
    //желаемое количество адресов по заданным координатам. По координатам может прийти больше
    //одного адреса. Также рекомендуем обратить внимание на такие методы Geocoder’а, как
    //getFromLocationName, которые позволяют найти адрес по названию или имени места.
    private fun getAddressAsync(context: Context, location: Location) {
        val geoCoder = Geocoder(context)
        Thread {
            try {
                val addresses = geoCoder.getFromLocation(location.latitude,location.longitude,1)
                mainFragmentFAB.post {
                    showAddressDialog(addresses[0].getAddressLine(0), location)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    //Полученный адрес выводим в диалоговом окне, где спрашиваем, нужно ли получить погоду по этому
    //адресу. Если погоду нужно получить, то открываем DetailsFragment, куда передаём адрес и
    //координаты, а DetailsFragment обращается на сервер Яндекса как обычно
    private fun showAddressDialog(address: String, location: Location) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_address_title))
                .setMessage(address)
                .setPositiveButton(getString(R.string.dialog_address_get_weather)) { _, _ -> openDetailsFragment(
                        Weather(
                            City(
                                address,
                                location.latitude,
                                location.longitude
                            )
                        )
                    )
                }
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss()}
                .create()
                .show()
        }
    }

    private fun openDetailsFragment(weather: Weather) {
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .add(
                    R.id.container, DetailsFragment.newInstance(Bundle().apply {
                        putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                    })
                )
                .addToBackStack("")
                .commitAllowingStateLoss()
        }
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


