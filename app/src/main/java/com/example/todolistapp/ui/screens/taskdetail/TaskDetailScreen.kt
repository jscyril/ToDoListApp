package com.example.todolistapp.ui.screens.taskdetail

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todolistapp.domain.model.Priority
import com.example.todolistapp.ui.components.ConfirmDialog
import com.example.todolistapp.ui.theme.*
import com.example.todolistapp.util.DateUtils
import com.example.todolistapp.util.IconUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TaskDetailViewModel = viewModel(factory = TaskDetailViewModel.Factory)
) {
    val task by viewModel.task.collectAsStateWithLifecycle()
    val category by viewModel.category.collectAsStateWithLifecycle()
    val taskDeleted by viewModel.taskDeleted.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPrioritySheet by remember { mutableStateOf(false) }

    LaunchedEffect(taskDeleted) {
        if (taskDeleted) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    task?.let { t ->
                        IconButton(onClick = { onNavigateToEdit(t.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = LocalAccentColor.current)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = DestructiveRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Black,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                )
            )
        },
        containerColor = Black
    ) { padding ->
        task?.let { t ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status & Priority Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Completion toggle
                    Surface(
                        onClick = { viewModel.toggleCompletion() },
                        shape = RoundedCornerShape(12.dp),
                        color = if (t.isCompleted) SuccessGreen.copy(alpha = 0.15f) else DarkCard,
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp,
                            if (t.isCompleted) SuccessGreen.copy(alpha = 0.3f) else DarkBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                if (t.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (t.isCompleted) SuccessGreen else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                if (t.isCompleted) "Completed" else "Mark Complete",
                                color = if (t.isCompleted) SuccessGreen else TextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Priority badge
                    val priorityColor = when (t.priority) {
                        Priority.LOW -> PriorityLow
                        Priority.MEDIUM -> PriorityMedium
                        Priority.HIGH -> PriorityHigh
                        Priority.URGENT -> PriorityUrgent
                    }
                    Surface(
                        onClick = { showPrioritySheet = true },
                        shape = RoundedCornerShape(12.dp),
                        color = priorityColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, priorityColor.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(priorityColor)
                            )
                            Text(
                                t.priority.label,
                                color = priorityColor,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Title
                Text(
                    text = t.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textDecoration = if (t.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )

                // Description
                if (t.description.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkCard,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder)
                    ) {
                        Text(
                            text = t.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Category
                if (category != null) {
                    val catColor = try {
                        Color(android.graphics.Color.parseColor(category!!.colorHex))
                    } catch (e: Exception) { ElectricCyan }
                    DetailRow(
                        icon = IconUtils.getIcon(category!!.iconName),
                        label = "Category",
                        value = category!!.name,
                        color = catColor
                    )
                }

                // Due Date
                if (t.dueDate != null) {
                    val isOverdue = DateUtils.isOverdue(t.dueDate, t.dueTime) && !t.isCompleted
                    val dateColor = if (isOverdue) DestructiveRed else LocalAccentColor.current
                    DetailRow(
                        icon = Icons.Filled.CalendarToday,
                        label = "Due Date",
                        value = DateUtils.formatDateTime(t.dueDate, t.dueTime),
                        color = dateColor
                    )
                }

                // Timestamps
                HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
                Text(
                    "Created: ${DateUtils.formatFullDate(t.createdAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
                Text(
                    "Last updated: ${DateUtils.formatFullDate(t.updatedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LocalAccentColor.current)
            }
        }
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Delete Task",
            message = "Are you sure you want to delete this task? This action cannot be undone.",
            onConfirm = {
                viewModel.deleteTask()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showPrioritySheet) {
        ModalBottomSheet(
            onDismissRequest = { showPrioritySheet = false },
            containerColor = DarkCard
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Change Priority", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                Priority.entries.forEach { p ->
                    val color = when (p) {
                        Priority.LOW -> PriorityLow
                        Priority.MEDIUM -> PriorityMedium
                        Priority.HIGH -> PriorityHigh
                        Priority.URGENT -> PriorityUrgent
                    }
                    TextButton(
                        onClick = {
                            viewModel.changePriority(p)
                            showPrioritySheet = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(p.label, color = TextPrimary, modifier = Modifier.weight(1f))
                        if (task?.priority == p) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = LocalAccentColor.current)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, color.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.7f))
                Text(value, style = MaterialTheme.typography.bodyMedium, color = color)
            }
        }
    }
}

