package com.example.alarmreminder.alarm

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class AlarmActivity : ComponentActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notes = intent.getStringExtra("notes") ?: "Your event is now!"


        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {

        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@AlarmActivity, alarmUri)
            setAudioStreamType(AudioManager.STREAM_ALARM)
            isLooping = true
            prepare()
            start()
        }

        setContent {
            AlarmScreen(
                notes = notes,
                onStop = {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                    finish()
                }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}

@Composable
fun AlarmScreen(notes: String, onStop: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = notes)
            Button(onClick = { onStop() }) {
                Text(text = "Stop Alarm")
            }
        }
    }
}
