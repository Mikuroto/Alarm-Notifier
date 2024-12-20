package com.example.alarmreminder.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarmreminder.data.DatabaseModule
import com.example.alarmreminder.data.Reminder
import com.example.alarmreminder.data.ReminderRepository
import com.example.alarmreminder.notification.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DatabaseModule.getDatabase(application).reminderDao()
    private val repository = ReminderRepository(dao)

    private var _notes: String = ""
    private var _eventTime: Long? = null
    var currentReminderId: Int? = null
        private set

    fun loadReminder(id: Int) {
        // starting coroutine, to use suspend function getReminderById
        viewModelScope.launch(Dispatchers.IO) {
            val reminder = repository.getReminderById(id)
            if (reminder != null) {
                _notes = reminder.notes
                _eventTime = reminder.eventTime
                currentReminderId = reminder.id
            }
        }
    }

    fun updateNotes(newNotes: String) {
        _notes = newNotes
    }

    fun updateEventTime(timeMillis: Long) {
        _eventTime = timeMillis
    }

    fun getNotes() = _notes
    fun getEventTime() = _eventTime

    suspend fun saveChanges(onSaved: () -> Unit) {
        val id = currentReminderId
        val time = _eventTime
        val n = _notes
        if (id != null && time != null) {
            // in the beginning cancelling old notifications
            withContext(Dispatchers.IO) {
                ReminderScheduler.cancelReminders(getApplication(), id)
                repository.updateReminder(Reminder(id = id, eventTime = time, notes = n))
            }
            withContext(Dispatchers.Main) {
                onSaved()
            }
        } else {
            withContext(Dispatchers.Main) {
                onSaved()
            }
        }
    }
}
