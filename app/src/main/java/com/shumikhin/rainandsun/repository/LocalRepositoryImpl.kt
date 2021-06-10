package com.shumikhin.rainandsun.repository

import com.shumikhin.rainandsun.model.Weather
import com.shumikhin.rainandsun.room.HistoryDao
import com.shumikhin.rainandsun.utils.convertHistoryEntityToWeather
import com.shumikhin.rainandsun.utils.convertWeatherToEntity

class LocalRepositoryImpl(private val localDataSource: HistoryDao) :
    LocalRepository {
    override fun getAllHistory(): List<Weather> {
        return convertHistoryEntityToWeather(localDataSource.all())
    }
    override fun saveEntity(weather: Weather) {
        localDataSource.insert(convertWeatherToEntity(weather))
    }
}
