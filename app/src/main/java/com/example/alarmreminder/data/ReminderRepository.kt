package com.example.alarmreminder.data

class ReminderRepository(private val dao: ReminderDao) {

    fun getAllReminders() = dao.getAllReminders()

    suspend fun deleteReminder(reminder: Reminder) {
        dao.delete(reminder)
    }

    suspend fun insertReminder(reminder: Reminder): Long {
        return dao.insert(reminder)
    }

    suspend fun getReminderById(id: Int): Reminder? {
        return dao.getReminderById(id)
    }

    suspend fun updateReminder(reminder: Reminder) {
        dao.update(reminder)
    }
}
