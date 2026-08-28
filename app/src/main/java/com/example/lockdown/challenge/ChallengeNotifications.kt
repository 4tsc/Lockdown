package com.example.lockdown.challenge

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object ChallengeNotifications {
    const val CHANNEL_ID = "unlock_challenge"

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Reto de desbloqueo", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones que debes responder para desbloquear las apps."
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }
    }
}