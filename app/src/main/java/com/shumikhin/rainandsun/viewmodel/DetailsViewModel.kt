package com.shumikhin.rainandsun.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shumikhin.rainandsun.app.App.Companion.getHistoryDao
import com.shumikhin.rainandsun.app.AppState
import com.shumikhin.rainandsun.model.Weather

import com.shumikhin.rainandsun.model.WeatherDTO
import com.shumikhin.rainandsun.repository.*
import com.shumikhin.rainandsun.utils.convertDtoToModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.IOException

private const val SERVER_ERROR = "Ошибка сервера"
private const val REQUEST_ERROR = "Ошибка запроса на сервер"
private const val CORRUPTED_DATA = "Неполные данные"

//Наша ViewModel формируется по тому же принципу, что и
//MainViewModel: создаём LiveData для передачи данных, репозиторий для получения данных и два
//метода. Первый метод возвращает LiveData, чтобы на неё можно было подписаться. Второй метод
//осуществляет запрос на сервер через репозиторий

class DetailsViewModel(
    val detailsLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val detailsRepositoryImpl: DetailsRepository = DetailsRepositoryImpl(RemoteDataSource()),
    private val historyRepository: LocalRepository = LocalRepositoryImpl(getHistoryDao())
) : ViewModel() {

    //fun getLiveData() = detailsLiveData
    fun getWeatherFromRemoteSource(lat: Double, lon: Double) {
        detailsLiveData.value = AppState.Loading
        detailsRepositoryImpl.getWeatherDetailsFromServer(lat, lon, callBack)
    }

    fun saveCityToDB(weather: Weather) {
        historyRepository.saveEntity(weather)
    }

    private val callBack = object : Callback<WeatherDTO> {

        @Throws(IOException::class)
        override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {

            val serverResponse: WeatherDTO? = response.body()

            detailsLiveData.postValue(
                if (response.isSuccessful && serverResponse != null) {
                    checkResponse(serverResponse)
                } else {
                    AppState.Error(Throwable(SERVER_ERROR))
                }
            )

        }

        //Вызывается только тогда по каким то причинам не получилось связаться с сервером
        //трафик закончился,"прилег" сервак, инета нет и т.п.
        override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
            detailsLiveData.postValue(
                AppState.Error(Throwable(t?.message ?: REQUEST_ERROR))
            )
        }

        private fun checkResponse(serverResponse: WeatherDTO): AppState {
            val fact = serverResponse.fact
            return if (fact?.temp == null || fact.feels_like == null || fact.condition.isNullOrEmpty()) {
                AppState.Error(Throwable(CORRUPTED_DATA))
            } else {
                AppState.Success(convertDtoToModel(serverResponse))
            }
        }

    }


}