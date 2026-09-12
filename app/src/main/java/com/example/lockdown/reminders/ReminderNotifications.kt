package com.example.lockdown.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import com.example.lockdown.R

object ReminderNotifications {
    const val CHANNEL_ID = "reminder_ring_v2"

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.heby}")
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val channel = NotificationChannel(
                CHANNEL_ID, "Recordatorios", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisos de recordatorios."
                setSound(soundUri, audioAttributes)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }
    }
}