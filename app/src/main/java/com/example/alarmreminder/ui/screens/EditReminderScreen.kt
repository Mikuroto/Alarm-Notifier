package com.example.alarmreminder.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarmreminder.data.Reminder
import com.example.alarmreminder.notification.ReminderScheduler
import com.example.alarmreminder.ui.EditReminderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderScreen(
    reminderId: Int,
    onReminderUpdated: () -> Unit,
    viewModel: EditReminderViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // loading notification
    LaunchedEffect(reminderId) {
        viewModel.loadReminder(reminderId)
    }

    val eventTime = viewModel.getEventTime()
    var chosenDate by remember { mutableStateOf("") }
    var chosenTime by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf(viewModel.getNotes()) }

    val calendar = remember { Calendar.getInstance() }

    LaunchedEffect(eventTime) {
        if (eventTime != null) {
            calendar.timeInMillis = eventTime
            chosenDate = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)+1}/${calendar.get(Calendar.YEAR)}"
            chosenTime = "${calendar.get(Calendar.HOUR_OF_DAY).toString().padStart(2,'0')}:${calendar.get(Calendar.MINUTE).toString().padStart(2,'0')}"
        }
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            viewModel.updateEventTime(calendar.timeInMillis)
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
            viewModel.updateEventTime(calendar.timeInMillis)
            chosenTime = "${hourOfDay.toString().padStart(2,'0')}:${minute.toString().padStart(2,'0')}"
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Reminder") }
            )
        }
    ) { innerPadding ->
        if (eventTime == null) {
            // while eventTime is not loaded
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
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
                    onValueChange = {
                        notes = it
                        viewModel.updateNotes(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Notes") }
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.saveChanges {
                                // saved succesfully planning new notifications and alarm
                                val updatedTime = viewModel.getEventTime()
                                val updatedId = viewModel.currentReminderId
                                val updatedNotes = viewModel.getNotes()

                                if (updatedId != null && updatedTime != null) {
                                    // planning new notifications
                                    ReminderScheduler.scheduleReminders(context, updatedId, updatedTime, updatedNotes)
                                }

                                onReminderUpdated()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}
