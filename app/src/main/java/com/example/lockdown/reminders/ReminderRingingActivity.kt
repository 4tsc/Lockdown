package com.example.lockdown.reminders

import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lockdown.LockdownApp
import com.example.lockdown.ui.theme.LockdownTheme

class ReminderRingingActivity : ComponentActivity() {
    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1)
        val repository = (application as LockdownApp).repository

        setContent {
            var title by remember { mutableStateOf("Recordatorio") }

            LaunchedEffect(reminderId) {
                val reminder = repository.reminderDao.getById(reminderId)
                if (reminder != null) {
                    title = reminder.title
                    val soundUri = Uri.parse("android.resource://$packageName/${com.example.lockdown.R.raw.heby}")
                    ringtone = RingtoneManager.getRingtone(this@ReminderRingingActivity, soundUri)?.apply {
                        audioAttributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) isLooping = true
                        play()
                    }
                }
            }

            LockdownTheme {
                Surface(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("⏰", style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.height(16.dp))
                        Text(title, style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(32.dp))
                        Button(onClick = { stopAndFinish() }) { Text("Detener") }
                    }
                }
            }
        }
    }

    private fun stopAndFinish() {
        ringtone?.stop()
        finish()
    }

    override fun onDestroy() {
        ringtone?.stop()
        super.onDestroy()
    }
}