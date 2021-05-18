package com.shumikhin.rainandsun.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shumikhin.rainandsun.model.Repository
import com.shumikhin.rainandsun.model.RepositoryImpl
import java.lang.Thread.sleep

class MainViewModel(
    private val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    private val repositoryImpl: Repository = RepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve
    fun getWeatherFromLocalSource() = getDataFromLocalSource()
    fun getWeatherFromRemoteSource() = getDataFromLocalSource()


    // Запрос осуществляется асинхронно в отдельном потоке. Как только
    //поток просыпается, мы передаём в нашу LiveData какие-то данные через метод postValue. Если
    //данные передаются в основном потоке, используйте метод setValue.
    private fun getDataFromLocalSource() {
        //Ставим статус в загрузку.
        liveDataToObserve.value = AppState.Loading
        Thread {
            sleep(1000)
            //Если данные были получены, состояние меняется на Success.
            liveDataToObserve.postValue(AppState.Success(repositoryImpl.getWeatherFromLocalStorage()))
        }.start()
    }

}