package com.example.alarmreminder

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.alarmreminder.notification.NotificationUtils
import com.example.alarmreminder.ui.screens.AddReminderScreen
import com.example.alarmreminder.ui.screens.EditReminderScreen
import com.example.alarmreminder.ui.screens.ReminderListScreen
import com.example.alarmreminder.ui.screens.SettingsScreen
import com.example.alarmreminder.ui.theme.AlarmReminderTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val POST_NOTIFICATION_REQ_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // check and request permission for exact alarm for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        // creating notifications channel
        NotificationUtils.createNotificationChannel(this)

        // requesting permission for notifications for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), POST_NOTIFICATION_REQ_CODE)
            }
        }

        setContent {
            AlarmReminderTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "list") {
                    composable("list") {
                        ReminderListScreen(
                            onAddClick = {
                                navController.navigate("add", navOptions {
                                    launchSingleTop = true
                                })
                            },
                            onEditClick = { id ->
                                navController.navigate("edit/$id", navOptions {
                                    launchSingleTop = true
                                })
                            },
                            onSettingsClick = {
                                navController.navigate("settings", navOptions {
                                    launchSingleTop = true
                                })
                            }
                        )
                    }
                    composable("add") {
                        AddReminderScreen(
                            onReminderSaved = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("edit/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                        if (id != null) {
                            EditReminderScreen(
                                reminderId = id,
                                onReminderUpdated = {
                                    navController.popBackStack()
                                }
                            )
                        } else {
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }
                    composable("settings") {
                        SettingsScreen(
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
