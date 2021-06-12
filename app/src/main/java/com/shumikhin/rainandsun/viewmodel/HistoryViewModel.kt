package com.shumikhin.rainandsun.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shumikhin.rainandsun.app.App.Companion.getHistoryDao
import com.shumikhin.rainandsun.app.AppState
import com.shumikhin.rainandsun.repository.LocalRepositoryImpl

class HistoryViewModel(
    val historyLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val historyRepositoryImpl: LocalRepositoryImpl = LocalRepositoryImpl(getHistoryDao())
) : ViewModel() {

    fun getAllHistory() {
        historyLiveData.value = AppState.Loading
        historyLiveData.value = AppState.Success(historyRepositoryImpl.getAllHistory())
    }
}
