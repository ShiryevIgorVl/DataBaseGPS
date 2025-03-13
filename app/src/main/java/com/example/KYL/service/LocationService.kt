package com.example.KYL.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.KYL.R
import com.example.KYL.activity.MainActivity


class LocationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Mytag", "onCreate: ON")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Mytag", "onDestroy: ON")
    }

    private fun startNotification() {
        val nChannel = NotificationChannel(
            CHANNEL_ID,
            "Сервис геолокации",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val nManager = getSystemService(NotificationManager::class.java) as NotificationManager
        nManager.createNotificationChannel(nChannel)

        val nIntent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            this,
            10,
            nIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        ).setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("GPS работает")
            .setContentIntent(pIntent).build()

        startForeground(99, notification)
    }

    companion object {
        const val CHANNEL_ID = "channel_1"
    }
}