package com.example.lockdown.challenge

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.lockdown.BuildConfig
import com.example.lockdown.LockdownApp
import com.example.lockdown.data.ChallengeState
import com.example.lockdown.data.ChallengeStep
import com.example.lockdown.data.StepStatus
import com.example.lockdown.data.UnlockChallenge
import com.example.lockdown.util.Prefs
import kotlin.random.Random

const val EXTRA_STEP_ID = "extra_step_id"
const val EXTRA_CHALLENGE_ID = "extra_challenge_id"
const val ACTION_NOTIFY = "com.example.lockdown.action.CHALLENGE_NOTIFY"
const val ACTION_DEADLINE = "com.example.lockdown.action.CHALLENGE_DEADLINE"
const val ACTION_RESPOND = "com.example.lockdown.action.CHALLENGE_RESPOND"

private const val TAG = "ChallengeScheduler"

object ChallengeScheduler {

    private const val MIN_STEPS = 7
    private const val MAX_STEPS = 8

    // En debug, ventanas cortas para poder probar sin esperar horas reales.
    private val MIN_DURATION_MS = if (BuildConfig.DEBUG) 2 * 60 * 1000L else 8 * 60 * 60 * 1000L
    private val MAX_DURATION_MS = if (BuildConfig.DEBUG) 4 * 60 * 1000L else 12 * 60 * 60 * 1000L
    private val RESPONSE_WINDOW_MS = if (BuildConfig.DEBUG) 30 * 1000L else 10 * 60 * 1000L

    suspend fun startChallenge(context: Context): UnlockChallenge {
        val repository = (context.applicationContext as LockdownApp).repository
        repository.unlockChallengeDao.getActive()?.let { return it }

        val totalSteps = Random.nextInt(MIN_STEPS, MAX_STEPS + 1)
        val duration = Random.nextLong(MIN_DURATION_MS, MAX_DURATION_MS + 1)
        val startElapsed = SystemClock.elapsedRealtime()
        val nowWallClock = System.currentTimeMillis()

        val challenge = UnlockChallenge(
            state = ChallengeState.ACTIVE,
            startElapsedRealtime = startElapsed,
            endElapsedRealtime = startElapsed + duration,
            totalSteps = totalSteps,
            completedSteps = 0,
            createdAtWallClock = nowWallClock
        )
        val challengeId = repository.unlockChallengeDao.insert(challenge)

        val bucketSize = duration / totalSteps
        val steps = (0 until totalSteps).map { i ->
            val offset = i * bucketSize + Random.nextLong(0, bucketSize)
            val scheduled = startElapsed + offset
            ChallengeStep(
                challengeId = challengeId,
                stepIndex = i,
                scheduledElapsedRealtime = scheduled,
                responseDeadlineElapsedRealtime = scheduled + RESPONSE_WINDOW_MS,
                status = StepStatus.PENDING
            )
        }
        repository.challengeStepDao.insertAll(steps)

        repository.challengeStepDao.getStepsForChallenge(challengeId).forEach {
            scheduleStepAlarms(context, it)
        }

        return challenge.copy(id = challengeId)
    }

    fun scheduleStepAlarms(context: Context, step: ChallengeStep) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            val notifyIntent = Intent(context, ChallengeAlarmReceiver::class.java).apply {
                action = ACTION_NOTIFY
                putExtra(EXTRA_STEP_ID, step.id)
                putExtra(EXTRA_CHALLENGE_ID, step.challengeId)
            }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, step.scheduledElapsedRealtime,
                PendingIntent.getBroadcast(
                    context, (step.id.toInt() * 10 + 1), notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )

            val deadlineIntent = Intent(context, ChallengeAlarmReceiver::class.java).apply {
                action = ACTION_DEADLINE
                putExtra(EXTRA_STEP_ID, step.id)
                putExtra(EXTRA_CHALLENGE_ID, step.challengeId)
            }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP, step.responseDeadlineElapsedRealtime,
                PendingIntent.getBroadcast(
                    context, (step.id.toInt() * 10 + 2), deadlineIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Sin permiso de alarmas exactas, no se pudo programar", e)
        }
    }

    private fun cancelStepAlarms(context: Context, step: ChallengeStep) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context, (step.id.toInt() * 10 + 1),
                Intent(context, ChallengeAlarmReceiver::class.java).apply { action = ACTION_NOTIFY },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context, (step.id.toInt() * 10 + 2),
                Intent(context, ChallengeAlarmReceiver::class.java).apply { action = ACTION_DEADLINE },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    suspend fun onStepResponded(context: Context, stepId: Long) {
        val repository = (context.applicationContext as LockdownApp).repository
        val step = repository.challengeStepDao.getById(stepId) ?: return
        if (step.status != StepStatus.PENDING) return

        repository.challengeStepDao.setStatus(stepId, StepStatus.RESPONDED)
        cancelStepAlarms(context, step)
        repository.unlockChallengeDao.incrementCompleted(step.challengeId)

        val challenge = repository.unlockChallengeDao.getActive()
        if (challenge != null && challenge.completedSteps + 1 >= challenge.totalSteps) {
            repository.unlockChallengeDao.setState(challenge.id, ChallengeState.COMPLETED)
            Prefs.setUnlockedToday(context)
            notifyResult(context, success = true)
        }
    }

    suspend fun onStepMissedIfPending(context: Context, stepId: Long) {
        val repository = (context.applicationContext as LockdownApp).repository
        val step = repository.challengeStepDao.getById(stepId) ?: return
        if (step.status != StepStatus.PENDING) return

        repository.challengeStepDao.setStatus(stepId, StepStatus.MISSED)
        val challenge = repository.unlockChallengeDao.getActive() ?: return
        repository.unlockChallengeDao.setState(challenge.id, ChallengeState.FAILED)

        repository.challengeStepDao.getStepsForChallenge(challenge.id)
            .filter { it.status == StepStatus.PENDING }
            .forEach { cancelStepAlarms(context, it) }

        notifyResult(context, success = false)
    }

    // elapsedRealtime se reinicia con cada boot; reconstruimos los horarios
    // pendientes usando el ancla de reloj de pared guardada al crear el reto.
    suspend fun rescheduleAfterBoot(context: Context) {
        val repository = (context.applicationContext as LockdownApp).repository
        val challenge = repository.unlockChallengeDao.getActive() ?: return

        val bridge = challenge.createdAtWallClock - challenge.startElapsedRealtime
        val nowElapsed = SystemClock.elapsedRealtime()
        val nowWallClock = System.currentTimeMillis()

        repository.challengeStepDao.getStepsForChallenge(challenge.id)
            .filter { it.status == StepStatus.PENDING }
            .forEach { step ->
                val deadlineTarget = bridge + step.responseDeadlineElapsedRealtime
                if (deadlineTarget <= nowWallClock) {
                    onStepMissedIfPending(context, step.id)
                    return@forEach
                }
                val notifyTarget = bridge + step.scheduledElapsedRealtime
                val notifyDelay = (notifyTarget - nowWallClock).coerceAtLeast(0)
                val deadlineDelay = deadlineTarget - nowWallClock

                scheduleStepAlarms(
                    context,
                    step.copy(
                        scheduledElapsedRealtime = nowElapsed + notifyDelay,
                        responseDeadlineElapsedRealtime = nowElapsed + deadlineDelay
                    )
                )
            }
    }

    @SuppressLint("MissingPermission")
    private fun notifyResult(context: Context, success: Boolean) {
        ChallengeNotifications.ensureChannel(context)
        val notification = NotificationCompat.Builder(context, ChallengeNotifications.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentTitle(if (success) "Reto completado" else "Reto fallido")
            .setContentText(
                if (success) "Todo desbloqueado por el resto del día."
                else "Se perdió una confirmación. Las apps siguen bloqueadas."
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(999999, notification)
    }
}