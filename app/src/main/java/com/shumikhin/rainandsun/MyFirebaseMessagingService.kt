package com.shumikhin.rainandsun

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val PUSH_KEY_TITLE = "title"
        private const val PUSH_KEY_MESSAGE = "message"
        private const val CHANNEL_ID = "channel_id"
        private const val NOTIFICATION_ID = 37
    }

    //Это самый важный метод. Вызывается каждый раз, когда приходит уведомление
    //и приложение открыто
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val remoteMessageData = remoteMessage.data
        if (remoteMessageData.isNotEmpty()) {
            //Данные приходят в строковом формате по аналогии с JSON.
            //Если данные пришли, то мы их переводим в Map через функцию-расширение из стандартной
            //библиотеки. Дальше, как при работе с данными с обычного сервера, мы должны их распарсить в
            //соответствии с контрактом. Нам нужно знать ключи в каждой паре ключ-значение. Ключи и значения
            //вы прописываете при отправке push-уведомления из своего кабинета в Firebase. Если
            //значения пришли и они не пустые, можно показать уведомление.
            handleDataMessage(remoteMessageData.toMap())
        }
    }

    //Тут собственно парсим
    private fun handleDataMessage(data: Map<String, String>) {
        val title = data[PUSH_KEY_TITLE]
        val message = data[PUSH_KEY_MESSAGE]
        if (!title.isNullOrBlank() && !message.isNullOrBlank()) {
            showNotification(title, message)
        }
    }

    private fun showNotification(title: String, message: String) {
        //Обратите внимание, что помимо контекста
        //NotificationCompat.Builder принимает ID канала. Это нужно для устройств версии 8 и выше.
        //Более старыми девайсами этот параметр игнорируется. С этой же целью мы проверяем версию
        //операционной системы. Если она выше 26 (версия O), создаём канал. Проверка необходима, потому
        //что в старых версиях SDK такого класса нет.
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.weather_icon_512px)
            setContentTitle(title)
            setContentText(message)
            priority = NotificationCompat.PRIORITY_DEFAULT //приоритет (нужен для устройств версии 7.1 и ниже) или важность (для устройств версии 8 и выше).
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        // Вызываем метод notify у NotificationManager, и система отобразит уведомление.
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        notificationManager:
        NotificationManager
    ) {
        val name = "Channel name"
        val descriptionText = "Channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    // Метод onNewToken, получает токен, который нужен серверу для рассылки индивидуальных уведомлений. Этот метод
    // вызывается единожды в начале работы приложения. Он может быть вызван повторно, только если
    // переустановить приложение, потому что токен у смартфона всегда только один.
    override fun onNewToken(token: String) {
        //Отправить токен на сервер
    }

}