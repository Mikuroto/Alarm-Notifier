package com.example.alarmreminder.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarmreminder.data.DatabaseModule
import com.example.alarmreminder.data.Reminder
import com.example.alarmreminder.data.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class EditReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DatabaseModule.getDatabase(application).reminderDao()
    private val repository = ReminderRepository(dao)

    private val _notes = MutableStateFlow("")
    val notes = _notes.asStateFlow()

    private val _eventTime = MutableStateFlow<Long?>(null)
    val eventTime = _eventTime.asStateFlow()

    private var currentReminderId: Int? = null

    fun loadReminder(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val reminder = repository.getReminderById(id)
            if (reminder != null) {
                _notes.value = reminder.notes
                _eventTime.value = reminder.eventTime
                currentReminderId = reminder.id
            }
        }
    }

    fun updateNotes(newNotes: String) {
        _notes.value = newNotes
    }

    fun updateEventTime(timeMillis: Long) {
        _eventTime.value = timeMillis
    }

    suspend fun saveChanges(onSaved: () -> Unit) {
        val id = currentReminderId
        val time = eventTime.value
        val n = notes.value
        if (id != null && time != null) {
            withContext(Dispatchers.IO) {
                repository.updateReminder(Reminder(id = id, eventTime = time, notes = n))
            }
            // returning from IO to main thread before onSaved()
            withContext(Dispatchers.Main) {
                onSaved()
            }
        }
    }


}
