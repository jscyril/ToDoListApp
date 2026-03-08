package com.example.todolistapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todolistapp.data.local.datastore.SettingsDataStore
import com.example.todolistapp.navigation.NavGraph
import com.example.todolistapp.ui.theme.ToDoListAppTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()

        val settingsDataStore = TodoApp.instance.settingsRepository as SettingsDataStore

        setContent {
            val accentColorIndex by settingsDataStore.getAccentColorIndex()
                .collectAsStateWithLifecycle(initialValue = 0)
            val onboardingCompleted by settingsDataStore.getOnboardingCompleted()
                .collectAsStateWithLifecycle(initialValue = true)

            ToDoListAppTheme(accentColorIndex = accentColorIndex) {
                NavGraph(
                    showOnboarding = !onboardingCompleted,
                    accentColorIndex = accentColorIndex
                )
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}