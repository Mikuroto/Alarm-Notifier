package com.example.alarmreminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.concurrent.TimeUnit
import com.example.alarmreminder.alarm.AlarmReceiver


object ReminderScheduler {

    fun scheduleReminders(context: Context, eventTimeMillis: Long, notes: String) {

        val sixHoursBefore = eventTimeMillis - TimeUnit.HOURS.toMillis(6)

        val oneHourBefore = eventTimeMillis - TimeUnit.HOURS.toMillis(1)

        val twentyMinBefore = eventTimeMillis - TimeUnit.MINUTES.toMillis(20)


        scheduleNotification(context, sixHoursBefore, "Upcoming Event", "Your event is in 6 hours. Notes: $notes")
        scheduleNotification(context, oneHourBefore, "Upcoming Event", "Your event is in 1 hour. Notes: $notes")
        scheduleNotification(context, twentyMinBefore, "Upcoming Event", "Your event is in 20 minutes. Notes: $notes")


        scheduleAlarm(context, eventTimeMillis, notes)
    }

    private fun scheduleNotification(context: Context, triggerTime: Long, title: String, message: String) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            triggerTime.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    private fun scheduleAlarm(context: Context, triggerTime: Long, notes: String) {

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("notes", notes)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (triggerTime + 1).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}
