package com.shumikhin.rainandsun.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shumikhin.rainandsun.app.AppState
import com.shumikhin.rainandsun.repository.MainRepository
import com.shumikhin.rainandsun.repository.MainRepositoryImpl
import com.shumikhin.rainandsun.app.AppState.Error
import java.lang.Thread.sleep

class MainViewModel(
    private val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData(),
    private val mainRepositoryImpl: MainRepository = MainRepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve
    fun getWeatherFromLocalSourceRus() = getDataFromLocalSource(isRussian = true)
    fun getWeatherFromLocalSourceWorld() = getDataFromLocalSource(isRussian = false)

    // Запрос осуществляется асинхронно в отдельном потоке. Как только
    //поток просыпается, мы передаём в нашу LiveData какие-то данные через метод postValue. Если
    //данные передаются в основном потоке, используйте метод setValue.
    private fun getDataFromLocalSource(isRussian: Boolean) {
        //Ставим статус в загрузку.
        liveDataToObserve.value = AppState.Loading
        Thread {
            //sleep(1000)
            //val randomResult = (0..1).random()
            //if (randomResult == 1) {
                //Если данные были получены, состояние меняется на Success.
                liveDataToObserve.postValue(
                    AppState.Success(
                        if (isRussian)
                            mainRepositoryImpl.getWeatherFromLocalStorageRus() else mainRepositoryImpl.getWeatherFromLocalStorageWorld()
                    )
                )
            //} else liveDataToObserve.postValue(Error(Throwable()))

        }.start()
    }

}