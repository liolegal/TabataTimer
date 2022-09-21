package com.example.tabatatimer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.tabatatimer.Constants.TIMER_CHANNEL_ID
import com.example.tabatatimer.services.TimerService

class TabataTimer : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TIMER_CHANNEL_ID,
                "Timer",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Used for the control timer from notification"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}