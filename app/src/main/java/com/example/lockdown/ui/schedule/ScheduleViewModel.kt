package com.example.lockdown.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lockdown.LockdownApp
import com.example.lockdown.data.BlockRule
import com.example.lockdown.data.InstalledApp
import com.example.lockdown.data.InstalledAppsProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as LockdownApp).repository

    val installedApps: List<InstalledApp> by lazy {
        InstalledAppsProvider.getLaunchableApps(application)
    }

    private val _selectedDay = MutableStateFlow(LocalDate.now().dayOfWeek.value)
    val selectedDay: StateFlow<Int> = _selectedDay

    val rulesForSelectedDay: StateFlow<List<BlockRule>> = _selectedDay
        .flatMapLatest { day -> repository.blockRuleDao.observeForDay(day) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDay(day: Int) {
        _selectedDay.value = day
    }

    fun toggleApp(packageName: String, enabled: Boolean) {
        viewModelScope.launch {
            val existing = rulesForSelectedDay.value.find { it.packageName == packageName }
            if (existing != null) {
                repository.blockRuleDao.update(existing.copy(isEnabled = enabled))
            } else if (enabled) {
                repository.blockRuleDao.insert(
                    BlockRule(
                        packageName = packageName,
                        dayOfWeek = _selectedDay.value,
                        startMinute = 9 * 60,
                        endMinute = 17 * 60,
                        isEnabled = true
                    )
                )
            }
        }
    }

    fun updateTimeRange(rule: BlockRule, startMinute: Int, endMinute: Int) {
        viewModelScope.launch {
            repository.blockRuleDao.update(rule.copy(startMinute = startMinute, endMinute = endMinute))
        }
    }
}