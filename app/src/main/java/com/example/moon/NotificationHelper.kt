package com.example.moon



import android.app.*
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationHelper(val context: Context) {
    private val CHANNEL_ID = "moon_alerts"

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "Alertas Agrícolas",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Avisos para podas y siembras" }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun sendAlert(title: String, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_today)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(1, notification)
    }
}