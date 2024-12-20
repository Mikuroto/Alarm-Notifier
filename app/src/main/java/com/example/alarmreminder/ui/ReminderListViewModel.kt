package com.example.alarmreminder.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarmreminder.data.DatabaseModule
import com.example.alarmreminder.data.Reminder
import com.example.alarmreminder.data.ReminderRepository
import com.example.alarmreminder.notification.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReminderListViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DatabaseModule.getDatabase(application).reminderDao()
    private val repository = ReminderRepository(dao)

    val reminders = repository.getAllReminders().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            // canseling notifications and alarm
            ReminderScheduler.cancelReminders(getApplication(), reminder.id)
            // now deleting from database
            repository.deleteReminder(reminder)
        }
    }
}
