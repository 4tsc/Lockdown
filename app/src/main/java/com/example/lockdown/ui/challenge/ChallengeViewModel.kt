package com.example.lockdown.ui.challenge

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lockdown.LockdownApp
import com.example.lockdown.challenge.ChallengeScheduler
import com.example.lockdown.data.UnlockChallenge
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChallengeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as LockdownApp).repository

    val activeChallenge: StateFlow<UnlockChallenge?> = repository.unlockChallengeDao
        .observeActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun startChallenge() {
        viewModelScope.launch { ChallengeScheduler.startChallenge(getApplication()) }
    }
}