package com.shumikhin.rainandsun.repository

import com.google.gson.GsonBuilder
import com.shumikhin.rainandsun.BuildConfig
import com.shumikhin.rainandsun.model.WeatherDTO
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Это класс, где происходит запрос на сервер. Это наш источник данных. Тут мы создаём
//Retrofit запрос и отправляем его. Результаты запроса мы будем обрабатывать во
//ViewModel — там будет находиться наш callback.
//достаточно передать долготу
//и широту. Третьим аргументом выступает callback, но уже из библиотеки Retrofit, где в качестве
//дженерика указывается тип возвращаемых данных

class RemoteDataSource {
    private val weatherApi = Retrofit.Builder()
        .baseUrl("https://api.weather.yandex.ru/")
        .addConverterFactory(
            GsonConverterFactory.create(GsonBuilder().setLenient().create())
        )
        .build().create(WeatherAPI::class.java)

    fun getWeatherDetails(lat: Double, lon: Double, callback: Callback<WeatherDTO>) {
        // Если надо выполнить запрос синхронно —
        //например, когда мы уже находимся в отдельном потоке, — вместо enqueue() надо вызвать метод
        //execute(). Этот метод вернёт объект типа Response с таким же методом body(), как и в коде.
        weatherApi.getWeather(BuildConfig.WEATHER_API_KEY, lat, lon).enqueue(callback)
    }

}
