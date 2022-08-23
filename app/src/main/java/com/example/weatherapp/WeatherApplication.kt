package com.example.weatherapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.weatherapp.data.pushnotifications.AppFirebaseMessageService.Companion.CHANNEL_ID
import com.example.weatherapp.data.pushnotifications.AppFirebaseMessageService.Companion.CHANNEL_NAME
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeatherApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        subscribeToFCM()
        createNotificationChannel()
    }

    //TODO extract it to a user preference function
    private fun subscribeToFCM() {
        Firebase.messaging.subscribeToTopic("AndroidTopic")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d(ContentValues.TAG, msg)
//                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
    }

    private fun createNotificationChannel() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }
}