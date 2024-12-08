package com.example.alarmreminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alarmreminder.data.Reminder
import com.example.alarmreminder.ui.ReminderListViewModel


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListScreen(
    onAddClick: () -> Unit,
    viewModel: ReminderListViewModel = viewModel()
) {
    val reminders by viewModel.reminders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Reminders") },
                actions = {
                    IconButton(onClick = { onAddClick() }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (reminders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No reminders yet. Click + to add one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reminders) { reminder ->
                    ReminderItem(
                        reminder = reminder,
                        onDelete = { viewModel.deleteReminder(reminder) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderItem(reminder: Reminder, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "ID: ${reminder.id}")
                Text(text = "Time: ${reminder.eventTime}")
                Text(text = "Notes: ${reminder.notes}")
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    }
}
