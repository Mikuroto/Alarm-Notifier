package com.example.alarmreminder.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notes = intent.getStringExtra("notes") ?: "No notes"
        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("notes", notes)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(alarmIntent)
    }
}
