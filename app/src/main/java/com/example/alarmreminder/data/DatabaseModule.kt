package com.example.alarmreminder.data

import android.content.Context
import androidx.room.Room

object DatabaseModule {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "alarm_reminder_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
