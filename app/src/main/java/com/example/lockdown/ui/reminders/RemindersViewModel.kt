package com.example.lockdown.ui.reminders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lockdown.LockdownApp
import com.example.lockdown.data.Reminder
import com.example.lockdown.reminders.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RemindersViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as LockdownApp).repository

    val reminders: StateFlow<List<Reminder>> = repository.reminderDao.observeActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(title: String, triggerAtMillis: Long, soundUri: String?) {
        viewModelScope.launch {
            val reminder = Reminder(title = title, triggerAtMillis = triggerAtMillis, soundUri = soundUri)
            val id = repository.reminderDao.insert(reminder)
            ReminderScheduler.schedule(getApplication(), reminder.copy(id = id))
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            ReminderScheduler.cancel(getApplication(), reminder.id)
            repository.reminderDao.delete(reminder)
        }
    }
}