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
import com.example.alarmreminder.notification.ReminderScheduler
import com.example.alarmreminder.ui.EditReminderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderScreen(
    reminderId: Int,
    onReminderUpdated: () -> Unit,
    viewModel: EditReminderViewModel = viewModel()
) {
    val context = LocalContext.current
    val notes by viewModel.notes.collectAsState()
    val eventTime by viewModel.eventTime.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val calendar = remember { Calendar.getInstance() }

    LaunchedEffect(reminderId) {
        viewModel.loadReminder(reminderId)
    }

    LaunchedEffect(eventTime) {
        if (eventTime != null) {
            calendar.timeInMillis = eventTime!!
        }
    }

    var chosenDate by remember { mutableStateOf("") }
    var chosenTime by remember { mutableStateOf("") }

    LaunchedEffect(eventTime) {
        if (eventTime != null) {
            val c = Calendar.getInstance().apply { timeInMillis = eventTime!! }
            chosenDate = "${c.get(Calendar.DAY_OF_MONTH)}/${c.get(Calendar.MONTH)+1}/${c.get(Calendar.YEAR)}"
            chosenTime = "${c.get(Calendar.HOUR_OF_DAY).toString().padStart(2,'0')}:${c.get(Calendar.MINUTE).toString().padStart(2,'0')}"
        }
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            chosenDate = "$dayOfMonth/${monthOfYear+1}/$year"
            viewModel.updateEventTime(calendar.timeInMillis)
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
            chosenTime = "${hourOfDay.toString().padStart(2,'0')}:${minute.toString().padStart(2,'0')}"
            viewModel.updateEventTime(calendar.timeInMillis)
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
            // Пока eventTime не загрузилось, покажем прогресс
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
                    onValueChange = { viewModel.updateNotes(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Notes") }
                )

                Button(
                    onClick = {
                        val et = eventTime
                        if (et != null) {
                            coroutineScope.launch {

                                viewModel.saveChanges {
                                    // onSaved called in Main thread in EditReminderViewModel
                                    onReminderUpdated()
                                }
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
