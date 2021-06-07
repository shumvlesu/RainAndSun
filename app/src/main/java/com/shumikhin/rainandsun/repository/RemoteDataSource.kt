package com.shumikhin.rainandsun.repository

import com.shumikhin.rainandsun.BuildConfig
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

//Это класс, где происходит запрос на сервер. Это наш источник данных. Тут мы создаём
//инстанс OkHttp, формируем запрос и отправляем его. Результаты запроса мы будем обрабатывать во
//ViewModel — там будет находиться наш callback.
private const val REQUEST_API_KEY = "X-Yandex-API-Key"

class RemoteDataSource {
    fun getWeatherDetails(requestLink: String, callback: Callback) {
        val builder: Request.Builder = Request.Builder().apply {
            header(REQUEST_API_KEY, BuildConfig.WEATHER_API_KEY)
            url(requestLink)
        }
        OkHttpClient().newCall(builder.build()).enqueue(callback)
    }
}