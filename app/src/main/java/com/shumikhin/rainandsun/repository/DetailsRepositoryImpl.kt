package com.shumikhin.rainandsun.repository

import okhttp3.Callback

//В репозиторий мы передаём источник данных — таким образом репозиторий получает данные извне.
class DetailsRepositoryImpl(private val remoteDataSource: RemoteDataSource) : DetailsRepository {
    override fun getWeatherDetailsFromServer(requestLink: String, callback: Callback) {
        remoteDataSource.getWeatherDetails(requestLink, callback)
    }
}