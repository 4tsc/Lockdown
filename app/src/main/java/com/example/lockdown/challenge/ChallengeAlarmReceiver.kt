package com.example.lockdown.challenge

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChallengeAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val stepId = intent.getLongExtra(EXTRA_STEP_ID, -1)
        if (stepId == -1L) return
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    ACTION_NOTIFY -> showNotification(context, stepId)
                    ACTION_DEADLINE -> ChallengeScheduler.onStepMissedIfPending(context, stepId)
                    ACTION_RESPOND -> ChallengeScheduler.onStepResponded(context, stepId)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, stepId: Long) {
        ChallengeNotifications.ensureChannel(context)

        val respondIntent = Intent(context, ChallengeAlarmReceiver::class.java).apply {
            action = ACTION_RESPOND
            putExtra(EXTRA_STEP_ID, stepId)
        }
        val respondPendingIntent = PendingIntent.getBroadcast(
            context, (stepId.toInt() * 10 + 3), respondIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ChallengeNotifications.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentTitle("Confirma que sigues aquí")
            .setContentText("Toca \"Ya volví\" para seguir con el reto de desbloqueo.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .addAction(0, "Ya volví", respondPendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(stepId.toInt(), notification)
    }
}