package com.example.alarmreminder.data

import android.content.Context
import android.net.Uri

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    fun getAlarmSoundUri(): Uri? {
        val uriString = prefs.getString(KEY_ALARM_SOUND_URI, null)
        return if (uriString != null) Uri.parse(uriString) else null
    }

    fun setAlarmSoundUri(uri: Uri) {
        prefs.edit().putString(KEY_ALARM_SOUND_URI, uri.toString()).apply()
    }

    companion object {
        private const val KEY_ALARM_SOUND_URI = "alarm_sound_uri"
    }
}
