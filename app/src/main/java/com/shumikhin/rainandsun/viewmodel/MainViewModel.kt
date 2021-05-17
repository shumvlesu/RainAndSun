package com.shumikhin.rainandsun.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.Thread.sleep

class MainViewModel(private val liveDataToObserve: MutableLiveData<Any> = MutableLiveData()) : ViewModel() {
    fun getData(): LiveData<Any> {
        getDataFromLocalSource()
        return liveDataToObserve
    }

    // Запрос осуществляется асинхронно в отдельном потоке. Как только
    //поток просыпается, мы передаём в нашу LiveData какие-то данные через метод postValue. Если
    //данные передаются в основном потоке, используйте метод setValue.
    private fun getDataFromLocalSource() {
        Thread {
            sleep(1000)
            liveDataToObserve.postValue(Any())
        }.start()
    }

}