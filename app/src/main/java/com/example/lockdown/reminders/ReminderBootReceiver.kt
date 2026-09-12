package com.example.lockdown.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.lockdown.LockdownApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pendingResult = goAsync()
        val repository = (context.applicationContext as LockdownApp).repository
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val now = System.currentTimeMillis()
                repository.reminderDao.getAllActive()
                    .filter { it.triggerAtMillis > now }
                    .forEach { ReminderScheduler.schedule(context, it) }
            } finally {
                pendingResult.finish()
            }
        }
    }
}