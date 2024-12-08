package com.example.alarmreminder.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.alarmreminder.data.DatabaseModule
import com.example.alarmreminder.data.Reminder
import com.example.alarmreminder.notification.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderScreen(
    onReminderSaved: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { DatabaseModule.getDatabase(context) }
    val dao = db.reminderDao()

    var notes by remember { mutableStateOf("") }
    val calendar = remember { Calendar.getInstance() }

    var chosenDate by remember { mutableStateOf("") }
    var chosenTime by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            chosenDate = "$dayOfMonth/${monthOfYear+1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            chosenTime = "${hourOfDay.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Reminder") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { datePickerDialog.show() }) {
                Text(text = if (chosenDate.isEmpty()) "Choose Date" else "Date: $chosenDate")
            }

            Button(onClick = { timePickerDialog.show() }) {
                Text(text = if (chosenTime.isEmpty()) "Choose Time" else "Time: $chosenTime")
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notes") }
            )

            Button(
                onClick = {
                    val eventTime = calendar.timeInMillis
                    coroutineScope.launch(Dispatchers.IO) {
                        val id = dao.insert(Reminder(eventTime = eventTime, notes = notes))
                        // Планируем уведомления и будильник
                        ReminderScheduler.scheduleReminders(context, eventTime, notes)
                        onReminderSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Reminder")
            }
        }
    }
}
