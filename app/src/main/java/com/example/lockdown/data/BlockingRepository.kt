package com.example.lockdown.data

import android.content.Context

class BlockingRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    val blockRuleDao = db.blockRuleDao()
    val unlockChallengeDao = db.unlockChallengeDao()
    val challengeStepDao = db.challengeStepDao()
}