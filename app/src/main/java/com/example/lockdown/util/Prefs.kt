package com.example.lockdown.util

import android.content.Context

object Prefs {
    private const val NAME = "enfoque_prefs"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"

    private const val KEY_UNLOCKED_EPOCH_DAY = "unlocked_epoch_day"

    private fun prefs(context: Context) = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun isOnboardingDone(context: Context) = prefs(context).getBoolean(KEY_ONBOARDING_DONE, false)

    fun setOnboardingDone(context: Context, done: Boolean) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING_DONE, done).apply()
    }

    fun setUnlockedToday(context: Context) {
        val today = java.time.LocalDate.now().toEpochDay()
        prefs(context).edit().putLong(KEY_UNLOCKED_EPOCH_DAY, today).apply()
    }

    fun isUnlockedToday(context: Context): Boolean {
        val stored = prefs(context).getLong(KEY_UNLOCKED_EPOCH_DAY, -1)
        return stored == java.time.LocalDate.now().toEpochDay()
    }
}