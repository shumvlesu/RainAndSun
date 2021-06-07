package com.shumikhin.rainandsun.repository

import com.shumikhin.rainandsun.model.WeatherDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

//https://api.weather.yandex.ru/v2/informers/
//Этим интерфейсом мы описываем конкретный запрос на сервер — запрос на данные погоды с
//сервера Яндекса. Он формируется простым методом с аннотациями: указан endpoint ссылки
//(v2/informers), заголовок (@Header), и два параметра (@Query) запроса передаются в метод в
//качестве аргументов. Возвращает метод уже готовый класс с ответом от сервера (WeatherDTO).

interface WeatherAPI {
    @GET("v2/informers")
    fun getWeather(
        @Header("X-Yandex-API-Key") token: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Call<WeatherDTO>
}