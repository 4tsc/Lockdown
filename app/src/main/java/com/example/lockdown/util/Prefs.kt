package com.example.lockdown.util

import android.content.Context
import java.time.LocalDate

object Prefs {
    private const val NAME = "enfoque_prefs" // si en tu proyecto el nombre real es otro, no lo cambies
    private const val KEY_ONBOARDING_DONE = "onboarding_done"
    private const val KEY_SETUP_LOCKED = "setup_locked"
    private const val KEY_UNLOCKED_EPOCH_DAY = "unlocked_epoch_day"

    private fun prefs(context: Context) = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun isOnboardingDone(context: Context) = prefs(context).getBoolean(KEY_ONBOARDING_DONE, false)
    fun setOnboardingDone(context: Context, done: Boolean) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING_DONE, done).apply()
    }

    fun isSetupLocked(context: Context) = prefs(context).getBoolean(KEY_SETUP_LOCKED, false)
    fun setSetupLocked(context: Context) {
        prefs(context).edit().putBoolean(KEY_SETUP_LOCKED, true).apply()
    }

    fun setUnlockedToday(context: Context) {
        prefs(context).edit().putLong(KEY_UNLOCKED_EPOCH_DAY, LocalDate.now().toEpochDay()).apply()
    }

    fun isUnlockedToday(context: Context): Boolean {
        val stored = prefs(context).getLong(KEY_UNLOCKED_EPOCH_DAY, -1)
        return stored == LocalDate.now().toEpochDay()
    }
}