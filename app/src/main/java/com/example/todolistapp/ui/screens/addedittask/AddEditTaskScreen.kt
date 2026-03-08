package com.example.todolistapp.ui.screens.addedittask

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todolistapp.domain.model.Priority
import com.example.todolistapp.ui.theme.*
import com.example.todolistapp.util.DateUtils
import com.example.todolistapp.util.IconUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditTaskViewModel = viewModel(factory = AddEditTaskViewModel.Factory)
) {
    val title by viewModel.title.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val priority by viewModel.priority.collectAsStateWithLifecycle()
    val categoryId by viewModel.categoryId.collectAsStateWithLifecycle()
    val dueDate by viewModel.dueDate.collectAsStateWithLifecycle()
    val dueTime by viewModel.dueTime.collectAsStateWithLifecycle()
    val titleError by viewModel.titleError.collectAsStateWithLifecycle()
    val taskSaved by viewModel.taskSaved.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(taskSaved) {
        if (taskSaved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (viewModel.isEditing) "Edit Task" else "New Task",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Task Title *") },
                isError = titleError,
                supportingText = if (titleError) {{ Text("Title is required", color = DestructiveRed) }} else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LocalAccentColor.current,
                    unfocusedBorderColor = DarkBorder,
                    cursorColor = LocalAccentColor.current,
                    focusedLabelColor = LocalAccentColor.current,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    errorBorderColor = DestructiveRed,
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.titleMedium
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LocalAccentColor.current,
                    unfocusedBorderColor = DarkBorder,
                    cursorColor = LocalAccentColor.current,
                    focusedLabelColor = LocalAccentColor.current,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard
                ),
                maxLines = 5
            )

            // Priority
            Text("Priority", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.entries.forEach { p ->
                    val color = when (p) {
                        Priority.LOW -> PriorityLow
                        Priority.MEDIUM -> PriorityMedium
                        Priority.HIGH -> PriorityHigh
                        Priority.URGENT -> PriorityUrgent
                    }
                    FilterChip(
                        selected = priority == p,
                        onClick = { viewModel.onPriorityChange(p) },
                        label = { Text(p.label, fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = DarkCard,
                            labelColor = TextSecondary,
                            selectedContainerColor = color.copy(alpha = 0.2f),
                            selectedLabelColor = color
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = DarkBorder,
                            selectedBorderColor = color.copy(alpha = 0.5f),
                            enabled = true,
                            selected = priority == p
                        )
                    )
                }
            }

            // Category
            Text("Category", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val catColor = try {
                        Color(android.graphics.Color.parseColor(cat.colorHex))
                    } catch (e: Exception) { ElectricCyan }
                    FilterChip(
                        selected = categoryId == cat.id,
                        onClick = { viewModel.onCategoryChange(cat.id) },
                        label = { Text(cat.name, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                IconUtils.getIcon(cat.iconName),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (categoryId == cat.id) catColor else TextSecondary
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = DarkCard,
                            labelColor = TextSecondary,
                            selectedContainerColor = catColor.copy(alpha = 0.15f),
                            selectedLabelColor = catColor,
                            selectedLeadingIconColor = catColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = DarkBorder,
                            selectedBorderColor = catColor.copy(alpha = 0.3f),
                            enabled = true,
                            selected = categoryId == cat.id
                        )
                    )
                }
            }

            // Due Date & Time
            Text("Due Date & Time", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date picker button
                Surface(
                    modifier = Modifier.weight(1f).clickable { showDatePicker = true },
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = LocalAccentColor.current, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (dueDate != null) DateUtils.formatDate(dueDate!!) else "Set date",
                            color = if (dueDate != null) TextPrimary else TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Time picker button
                Surface(
                    modifier = Modifier.weight(1f).clickable { showTimePicker = true },
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.AccessTime, contentDescription = null, tint = LocalAccentColor.current, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (dueTime != null) DateUtils.formatTime(dueTime!!) else "Set time",
                            color = if (dueTime != null) TextPrimary else TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Clear date/time
            if (dueDate != null || dueTime != null) {
                TextButton(
                    onClick = {
                        viewModel.onDueDateChange(null)
                        viewModel.onDueTimeChange(null)
                    }
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear date & time", color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = viewModel::saveTask,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalAccentColor.current,
                    contentColor = Black
                )
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (viewModel.isEditing) "Update Task" else "Save Task",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDueDateChange(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }
                ) { Text("OK", color = LocalAccentColor.current) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = DarkCard)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = DarkCard,
                    titleContentColor = TextPrimary,
                    headlineContentColor = TextPrimary,
                    weekdayContentColor = TextSecondary,
                    dayContentColor = TextPrimary,
                    selectedDayContainerColor = LocalAccentColor.current,
                    selectedDayContentColor = Black,
                    todayContentColor = LocalAccentColor.current,
                    todayDateBorderColor = LocalAccentColor.current
                )
            )
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = DarkCard,
            confirmButton = {
                TextButton(
                    onClick = {
                        val cal = java.util.Calendar.getInstance()
                        cal.set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                        cal.set(java.util.Calendar.MINUTE, timePickerState.minute)
                        cal.set(java.util.Calendar.SECOND, 0)
                        cal.set(java.util.Calendar.MILLISECOND, 0)
                        viewModel.onDueTimeChange(cal.timeInMillis)
                        showTimePicker = false
                    }
                ) { Text("OK", color = LocalAccentColor.current) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = DarkSurface,
                        selectorColor = LocalAccentColor.current,
                        containerColor = DarkCard,
                        clockDialSelectedContentColor = Black,
                        clockDialUnselectedContentColor = TextPrimary,
                        periodSelectorBorderColor = DarkBorder,
                        timeSelectorSelectedContainerColor = LocalAccentColor.current.copy(alpha = 0.2f),
                        timeSelectorUnselectedContainerColor = DarkSurface,
                        timeSelectorSelectedContentColor = LocalAccentColor.current,
                        timeSelectorUnselectedContentColor = TextPrimary
                    )
                )
            }
        )
    }
}

