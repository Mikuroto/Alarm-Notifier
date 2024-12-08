package com.example.alarmreminder.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Reminder::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}
