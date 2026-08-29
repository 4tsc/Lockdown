package com.example.lockdown.util

import android.content.Context

object UnlockManager {
    // El "código de 32 caracteres generado al azar" es la puesta en escena.
    // En la práctica, es este número fijo.
    private const val MAGIC_CODE = "1111"

    fun tryUnlock(context: Context, enteredCode: String): Boolean {
        if (enteredCode.trim() == MAGIC_CODE) {
            Prefs.setUnlockedToday(context)
            return true
        }
        return false
    }
}