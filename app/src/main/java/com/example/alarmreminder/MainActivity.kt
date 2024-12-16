package com.example.alarmreminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import com.example.alarmreminder.ui.theme.AlarmReminderTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val POST_NOTIFICATION_REQ_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        NotificationUtils.createNotificationChannel(this)

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
                            // if id incoorect
                            LaunchedEffect(Unit) {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}
