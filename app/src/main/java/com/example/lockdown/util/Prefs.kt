package com.example.lockdown.util

import android.content.Context

object Prefs {
    private const val NAME = "enfoque_prefs"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"

    private fun prefs(context: Context) = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun isOnboardingDone(context: Context) = prefs(context).getBoolean(KEY_ONBOARDING_DONE, false)

    fun setOnboardingDone(context: Context, done: Boolean) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING_DONE, done).apply()
    }
}