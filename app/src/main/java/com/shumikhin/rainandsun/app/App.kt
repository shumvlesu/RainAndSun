package com.shumikhin.rainandsun.app

import android.app.Application
import androidx.room.Room
import com.shumikhin.rainandsun.room.HistoryDao
import com.shumikhin.rainandsun.room.HistoryDataBase

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {
        private var appInstance: App? = null
        private var db: HistoryDataBase? = null
        private const val DB_NAME = "History.db"

        fun getHistoryDao(): HistoryDao {
            if (db == null) {
                synchronized(HistoryDataBase::class.java) {
                    if (db == null) {

                        if (appInstance == null) throw IllegalStateException("Application is null while creating DataBase")
                        //потокобезопасно создаём базу через метод Room.databaseBuilder
                        db = Room.databaseBuilder(
                            //три аргумента
                            appInstance!!.applicationContext, //Контекст приложения
                            HistoryDataBase::class.java, //База
                            DB_NAME) //Имя БД
                            .allowMainThreadQueries() //Метод allowMainThreadQueries позволяет делать запросы из основного потока
                            .build()

                    }
                }
            }
            return db!!.historyDao()
        }

    }
}