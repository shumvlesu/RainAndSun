package com.shumikhin.rainandsun.repository

import com.shumikhin.rainandsun.model.Weather

interface LocalRepository {
    fun getAllHistory(): List<Weather>
    fun saveEntity(weather: Weather)
}