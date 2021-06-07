package com.shumikhin.rainandsun.repository

import com.shumikhin.rainandsun.model.WeatherDTO

//В репозиторий мы передаём источник данных — таким образом репозиторий получает данные извне.
class DetailsRepositoryImpl(private val remoteDataSource: RemoteDataSource) : DetailsRepository {
    override fun getWeatherDetailsFromServer(lat: Double,
                                             lon: Double,
                                             callback: retrofit2.Callback<WeatherDTO>
    ) {
        remoteDataSource.getWeatherDetails(lat, lon, callback)
    }
}