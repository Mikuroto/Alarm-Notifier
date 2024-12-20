package com.example.alarmreminder.ui

import android.app.Application
import android.database.Cursor
import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarmreminder.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RingtoneItem(val name: String, val uri: Uri)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = SettingsRepository(application)

    private val _ringtones = MutableStateFlow<List<RingtoneItem>>(emptyList())
    val ringtones = _ringtones.asStateFlow()

    private val _selectedUri = MutableStateFlow<Uri?>(null)
    val selectedUri = _selectedUri.asStateFlow()

    init {
        loadRingtones()
        _selectedUri.value = repo.getAlarmSoundUri()
    }

    private fun loadRingtones() {
        viewModelScope.launch {
            val list = mutableListOf<RingtoneItem>()
            val context = getApplication<Application>().applicationContext
            val manager = RingtoneManager(context)
            manager.setType(RingtoneManager.TYPE_ALARM)

            val cursor: Cursor? = manager.cursor
            if (cursor != null && cursor.moveToFirst()) {
                val titleColumn = RingtoneManager.TITLE_COLUMN_INDEX
                val uriColumn = RingtoneManager.URI_COLUMN_INDEX
                val idColumn = RingtoneManager.ID_COLUMN_INDEX
                do {
                    val title = cursor.getString(titleColumn)
                    val uriStr = manager.getRingtoneUri(cursor.position).toString()
                    list.add(RingtoneItem(title, Uri.parse(uriStr)))
                } while (cursor.moveToNext())
            }
            cursor?.close()
            _ringtones.value = list
        }
    }

    fun selectRingtone(uri: Uri) {
        _selectedUri.value = uri
        repo.setAlarmSoundUri(uri)
    }
}
