package com.shumikhin.rainandsun.utils

import com.shumikhin.rainandsun.model.*
import com.shumikhin.rainandsun.room.HistoryEntity

//Метод convertDtoToModel занимается преобразованием нашего Data transfer object в
//понятный для AppState формат
fun convertDtoToModel(weatherDTO: WeatherDTO): List<Weather> {
    val fact: FactDTO = weatherDTO.fact!!
    return listOf(
        Weather(
            getDefaultCity(),
            fact.temp!!,
            fact.feels_like!!,
            fact.condition!!,
            fact.icon
        )
    )
}

fun convertHistoryEntityToWeather(entityList: List<HistoryEntity>): List<Weather> {
    return entityList.map {
        Weather(City(it.city, 0.0, 0.0), it.temperature, 0, it.condition)
    }
}

fun convertWeatherToEntity(weather: Weather): HistoryEntity {
    return HistoryEntity( 0, weather.city.city, weather.temperature, weather.condition)
}
