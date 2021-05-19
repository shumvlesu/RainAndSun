package com.shumikhin.rainandsun.viewmodel

import com.shumikhin.rainandsun.model.Weather
import java.lang.Error

sealed class AppState {
    data class Success(val weatherData: Weather) : AppState()
    data class Error(val error: Throwable) : AppState()
    object Loading : AppState()
}
