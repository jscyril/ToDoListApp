package com.example.todolistapp.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todolistapp.domain.model.Priority
import com.example.todolistapp.ui.components.ConfirmDialog
import com.example.todolistapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    val accentColorIndex by viewModel.accentColorIndex.collectAsStateWithLifecycle()
    val defaultPriority by viewModel.defaultPriority.collectAsStateWithLifecycle()

    var showDeleteCompletedDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Black,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Accent Color
            Text("Appearance", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Accent Color", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Choose your preferred accent color", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AccentColors.forEachIndexed { index, color ->
                            val isSelected = accentColorIndex == index
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .then(
                                        if (isSelected) Modifier.border(2.5.dp, TextPrimary, CircleShape)
                                        else Modifier
                                    )
                                    .clickable { viewModel.setAccentColor(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = Black, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Defaults
            Text("Defaults", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Default Priority", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Priority.entries.forEach { p ->
                            val pColor = when (p) {
                                Priority.LOW -> PriorityLow
                                Priority.MEDIUM -> PriorityMedium
                                Priority.HIGH -> PriorityHigh
                                Priority.URGENT -> PriorityUrgent
                            }
                            FilterChip(
                                selected = defaultPriority == p.level,
                                onClick = { viewModel.setDefaultPriority(p.level) },
                                label = { Text(p.label) },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = DarkSurface,
                                    labelColor = TextSecondary,
                                    selectedContainerColor = pColor.copy(alpha = 0.2f),
                                    selectedLabelColor = pColor
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = DarkBorder,
                                    selectedBorderColor = pColor.copy(alpha = 0.5f),
                                    enabled = true,
                                    selected = defaultPriority == p.level
                                )
                            )
                        }
                    }
                }
            }

            // Danger Zone
            Text("Danger Zone", style = MaterialTheme.typography.titleSmall, color = DestructiveRed)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DestructiveRed.copy(alpha = 0.05f)),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, DestructiveRed.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { showDeleteCompletedDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarningOrange.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningOrange)
                    ) {
                        Icon(Icons.Filled.CleaningServices, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete All Completed Tasks")
                    }

                    OutlinedButton(
                        onClick = { showDeleteAllDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DestructiveRed.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DestructiveRed)
                    ) {
                        Icon(Icons.Filled.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete All Tasks")
                    }
                }
            }

            // App Info
            Text("About", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ToDo List App", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text("Version 1.0", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Icon(Icons.Filled.Info, contentDescription = null, tint = TextTertiary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showDeleteCompletedDialog) {
        ConfirmDialog(
            title = "Delete Completed Tasks",
            message = "This will permanently delete all completed tasks. This action cannot be undone.",
            confirmText = "Delete All Completed",
            onConfirm = {
                viewModel.deleteAllCompletedTasks()
                showDeleteCompletedDialog = false
            },
            onDismiss = { showDeleteCompletedDialog = false }
        )
    }

    if (showDeleteAllDialog) {
        ConfirmDialog(
            title = "Delete ALL Tasks",
            message = "⚠️ This will permanently delete ALL tasks. This action cannot be undone!",
            confirmText = "Delete Everything",
            onConfirm = {
                viewModel.deleteAllTasks()
                showDeleteAllDialog = false
            },
            onDismiss = { showDeleteAllDialog = false }
        )
    }
}

