package com.shumikhin.rainandsun.repository

import com.shumikhin.rainandsun.model.Weather
import com.shumikhin.rainandsun.model.getRussianCities
import com.shumikhin.rainandsun.model.getWorldCities

class MainRepositoryImpl : MainRepository {

    override fun getWeatherFromServer() = Weather()

    override fun getWeatherFromLocalStorageRus() = getRussianCities()

    override fun getWeatherFromLocalStorageWorld() = getWorldCities()

}
