package com.shumikhin.rainandsun.view.googlemaps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.shumikhin.rainandsun.R
import com.shumikhin.rainandsun.app.AppState
import com.shumikhin.rainandsun.databinding.FragmentGoogleMapsMainBinding
import com.shumikhin.rainandsun.viewmodel.DetailsViewModel
import kotlinx.android.synthetic.main.fragment_google_maps_main.*
import java.io.IOException

class GoogleMapsFragment : Fragment() {

    private var _binding: FragmentGoogleMapsMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private val markers: ArrayList<Marker> = arrayListOf()
    private val viewModel: DetailsViewModel by lazy { ViewModelProvider(this).get(DetailsViewModel::class.java) }


    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        val initialPlace = LatLng(52.52000659999999, 13.404953999999975)
        //Ставим маркер
        googleMap.addMarker(
            MarkerOptions().position(initialPlace).title(getString(R.string.marker_start))
        )
        //Пермещаемся к маркеру
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(initialPlace))//CameraUpdateFactory.* - Методы как перемещаем камеру

        //Назначаем слушатель
        googleMap.setOnMapLongClickListener { latLng ->
            getAddressAsync(latLng)
            addMarkerToArray(latLng)
            drawLine()
        }

        activateMyLocation(googleMap)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoogleMapsMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        initSearchByAddress()

    }

    private fun initSearchByAddress() {
        binding.buttonSearch.setOnClickListener {
            val geoCoder = Geocoder(it.context)
            val searchText = searchAddress.text.toString()
            Thread {
                try {
                    val addresses = geoCoder.getFromLocationName(
                        searchText,
                        1
                    )//максимальное количество адресов которое можно вернуть
                    if (addresses.size > 0) {
                        goToAddress(addresses, it, searchText)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    private fun goToAddress(
        addresses: MutableList<Address>,
        view: View,
        searchText: String
    ) {
        val location = LatLng(
            addresses[0].latitude,
            addresses[0].longitude
        )
        view.post {
            setMarker(location, searchText, R.drawable.ic_map_marker, 0)
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    15f
                )
            )
        }
    }

    //По ширине и долготе мы можем получить адресс того что там находится (Геокодирование (Geocoder))
    private fun getAddressAsync(location: LatLng) {
        context?.let {
            val geoCoder = Geocoder(it)
            Thread {
                try {
                    //Получаем адресс
                    val addresses =
                        geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    //Помещаем его в тек строку адреса вьюхи в гл потоке. одной строкой
                    textAddress.post { textAddress.text = addresses[0].getAddressLine(0) }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }


    //Создаем маркер с текстом и картинкой
    private fun addMarkerToArray(location: LatLng) {
        viewModel.detailsLiveData.observe(viewLifecycleOwner, Observer { renderData(it, location) })
        viewModel.getWeatherFromRemoteSource(location.latitude, location.longitude)

//        val marker = setMarker(location, markers.size.toString(), R.drawable.ic_map_pin)
//        markers.add(marker)
    }

    private fun renderData(appState: AppState, location: LatLng) {
        when (appState) {
            is AppState.Success -> {
                val temperature = appState.weatherData[0].temperature
                Toast.makeText(context, "Температура - $temperature", Toast.LENGTH_LONG).show()
                val marker =
                    setMarker(location, markers.size.toString(), R.drawable.ic_map_pin, temperature)
                markers.add(marker)
            }
            is AppState.Loading -> {

            }
            is AppState.Error -> {

            }
        }
    }


    private fun setMarker(
        location: LatLng,
        searchText: String,
        resourceId: Int,
        temperature: Int?
    ): Marker {
        return map.addMarker(
            MarkerOptions()
                .position(location)
                //.title(searchText)
                .title("$searchText $temperature")
                .icon(BitmapDescriptorFactory.fromResource(resourceId))
        )
    }


    //Рисуем линию между маркерами
    private fun drawLine() {
        val last: Int = markers.size - 1
        if (last >= 1) {
            val previous: LatLng = markers[last - 1].position
            val current: LatLng = markers[last].position
            map.addPolyline(
                PolylineOptions()
                    .add(previous, current)
                    .color(Color.RED)
                    .width(5f)
            )
        }
    }

    private fun activateMyLocation(googleMap: GoogleMap) {
        context?.let {
            val isPermissionGranted = ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            googleMap.isMyLocationEnabled = isPermissionGranted
            //разрешаем показать кнопочку моего положения
            googleMap.uiSettings.isMyLocationButtonEnabled = isPermissionGranted
        }
        //Получить разрешение, если его нет
    }

    companion object {
        fun newInstance() = GoogleMapsFragment()
    }
}
