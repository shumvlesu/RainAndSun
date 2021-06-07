package com.shumikhin.rainandsun.utils

import com.shumikhin.rainandsun.model.FactDTO
import com.shumikhin.rainandsun.model.Weather
import com.shumikhin.rainandsun.model.WeatherDTO
import com.shumikhin.rainandsun.model.getDefaultCity

//Метод convertDtoToModel занимается преобразованием нашего Data transfer object в
//понятный для AppState формат
fun convertDtoToModel(weatherDTO: WeatherDTO): List<Weather> {
    val fact: FactDTO = weatherDTO.fact!!
    return listOf(Weather(getDefaultCity(), fact.temp!!, fact.feels_like!!, fact.condition!!))
}