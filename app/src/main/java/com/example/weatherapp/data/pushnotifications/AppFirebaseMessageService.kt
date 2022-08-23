package com.example.weatherapp.data.pushnotifications

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.weatherapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class AppFirebaseMessageService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_NAME = "channel"
        const val CHANNEL_ID = "channel_id"
        const val CITY_NAME_NAV_ARG = "cityName"
        const val CITY_KEY = "city"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val args = Bundle()
        args.putString(CITY_NAME_NAV_ARG, message.data[CITY_KEY])

        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.currentWeatherFragment)
            .setArguments(args)
            .createPendingIntent()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000))
            .setOnlyAlertOnce(true)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()
        notificationManager.notify(notificationId, notification)
    }
}