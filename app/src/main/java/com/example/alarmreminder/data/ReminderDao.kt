package com.example.alarmreminder.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder): Long

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    fun getReminderById(id: Int): Reminder?

    @Query("SELECT * FROM reminders")
    fun getAllReminders(): Flow<List<Reminder>>

    @Delete
    fun delete(reminder: Reminder)
}

