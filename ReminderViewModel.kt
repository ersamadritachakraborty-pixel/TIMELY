package com.samadrita.timely

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.samadrita.timely.alarm.AlarmScheduler
import com.samadrita.timely.data.Reminder
import com.samadrita.timely.data.ReminderDatabase
import com.samadrita.timely.data.ReminderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ReminderRepository
    val allReminders: StateFlow<List<Reminder>>

    init {
        val dao = ReminderDatabase.getDatabase(application).reminderDao()
        repository = ReminderRepository(dao)
        allReminders = repository.allReminders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addReminder(reminder: Reminder) = viewModelScope.launch {
        val newId = repository.insert(reminder).toInt()
        AlarmScheduler.schedule(getApplication(), reminder.copy(id = newId))
    }

    fun updateReminder(reminder: Reminder) = viewModelScope.launch {
        repository.update(reminder)
        AlarmScheduler.schedule(getApplication(), reminder)
    }

    fun deleteReminder(reminder: Reminder) = viewModelScope.launch {
        AlarmScheduler.cancel(getApplication(), reminder)
        repository.delete(reminder)
    }

    fun toggleCompleted(reminder: Reminder) = viewModelScope.launch {
        repository.setCompleted(reminder.id, !reminder.isCompleted)
        if (!reminder.isCompleted) {
            AlarmScheduler.cancel(getApplication(), reminder)
        }
    }
}
