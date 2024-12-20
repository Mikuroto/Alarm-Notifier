package com.example.alarmreminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.alarmreminder.alarm.AlarmReceiver
import com.example.alarmreminder.notification.NotificationReceiver
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleReminders(context: Context, reminderId: Int, eventTimeMillis: Long, notes: String) {
        // eventTimeMillis - time of action
        // reminderId - unique id from database
        // schedule codes:
        // 6 hours: code = 1
        // 1 hour: code = 2
        // 20 min: code = 3
        // alarm: code = 4

        val sixHoursBefore = eventTimeMillis - TimeUnit.HOURS.toMillis(6)
        val oneHourBefore = eventTimeMillis - TimeUnit.HOURS.toMillis(1)
        val twentyMinBefore = eventTimeMillis - TimeUnit.MINUTES.toMillis(20)

        scheduleNotification(context, reminderId, 1, sixHoursBefore, "Upcoming Event", "Your event is in 6 hours. Notes: $notes")
        scheduleNotification(context, reminderId, 2, oneHourBefore, "Upcoming Event", "Your event is in 1 hour. Notes: $notes")
        scheduleNotification(context, reminderId, 3, twentyMinBefore, "Upcoming Event", "Your event is in 20 minutes. Notes: $notes")

        scheduleAlarm(context, reminderId, 4, eventTimeMillis, notes)
    }

    private fun scheduleNotification(context: Context, reminderId: Int, code: Int, triggerTime: Long, title: String, message: String) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("reminderId", reminderId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            generateRequestCode(reminderId, code),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    private fun scheduleAlarm(context: Context, reminderId: Int, code: Int, triggerTime: Long, notes: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("notes", notes)
            putExtra("reminderId", reminderId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            generateRequestCode(reminderId, code),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    fun cancelReminders(context: Context, reminderId: Int) {
        // Canselling all pending intents
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // To cancel need to create a PendingIntent with the same requestCode and Intent
        cancelPendingIntent(context, alarmManager, reminderId, 1, NotificationReceiver::class.java)
        cancelPendingIntent(context, alarmManager, reminderId, 2, NotificationReceiver::class.java)
        cancelPendingIntent(context, alarmManager, reminderId, 3, NotificationReceiver::class.java)
        cancelPendingIntent(context, alarmManager, reminderId, 4, AlarmReceiver::class.java)
    }

    private fun cancelPendingIntent(context: Context, alarmManager: AlarmManager, reminderId: Int, code: Int, receiverClass: Class<*>) {
        val intent = Intent(context, receiverClass)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            generateRequestCode(reminderId, code),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun generateRequestCode(reminderId: Int, code: Int): Int {
        return reminderId * 10 + code
    }
}
