package com.shumikhin.rainandsun.repository

import okhttp3.Callback

//Этот интерфейс будет обозначать работу с данными на экране DetailsFragment. Тут всего один
//метод, который принимает в качестве аргументов строку для запроса на сервер и callback для OkHttp.
interface DetailsRepository {
    fun getWeatherDetailsFromServer(requestLink: String, callback: Callback)
}