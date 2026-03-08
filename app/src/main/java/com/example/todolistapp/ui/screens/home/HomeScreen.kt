package com.example.todolistapp.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todolistapp.domain.model.*
import com.example.todolistapp.ui.components.*
import com.example.todolistapp.ui.theme.*
import com.example.todolistapp.util.DateUtils
import com.example.todolistapp.util.IconUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToEditTask: (Long) -> Unit,
    onNavigateToTaskDetail: (Long) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val filterType by viewModel.filterType.collectAsStateWithLifecycle()
    val filterCategoryId by viewModel.filterCategoryId.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val totalCount by viewModel.totalCount.collectAsStateWithLifecycle()
    val completedTodayCount by viewModel.completedTodayCount.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingCount.collectAsStateWithLifecycle()
    val overdueCount by viewModel.overdueCount.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showSortSheet by remember { mutableStateOf(false) }
    var showTaskActionsSheet by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    val categoryMap = remember(categories) { categories.associateBy { it.id } }

    // FAB animation
    val fabScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "fabScale"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = LocalAccentColor.current,
                contentColor = Black,
                shape = CircleShape,
                modifier = Modifier.scale(fabScale)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add task")
            }
        },
        containerColor = Black
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Greeting Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = DateUtils.getGreeting(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = DateUtils.formatTodayDate(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }

            // Stats Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatChip("Total", totalCount, LocalAccentColor.current, Modifier.weight(1f))
                    StatChip("Done", completedTodayCount, SuccessGreen, Modifier.weight(1f))
                    StatChip("Pending", pendingCount, WarningOrange, Modifier.weight(1f))
                    StatChip("Overdue", overdueCount, DestructiveRed, Modifier.weight(1f))
                }
            }

            // Search Bar
            item {
                AnimatedVisibility(
                    visible = isSearchActive,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = DarkCard,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = viewModel::onSearchQueryChange,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 12.dp),
                                textStyle = TextStyle(color = TextPrimary, fontSize = 16.sp),
                                cursorBrush = SolidColor(LocalAccentColor.current),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text("Search tasks...", color = TextTertiary, fontSize = 16.sp)
                                    }
                                    innerTextField()
                                }
                            )
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.onSearchQueryChange("") },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear", tint = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Search + Sort buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.toggleSearch() }) {
                        Icon(
                            if (isSearchActive) Icons.Filled.SearchOff else Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = if (isSearchActive) LocalAccentColor.current else TextSecondary
                        )
                    }
                    IconButton(onClick = { showSortSheet = true }) {
                        Icon(
                            Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort & Filter",
                            tint = TextSecondary
                        )
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        FilterType.ALL to "All",
                        FilterType.TODAY to "Today",
                        FilterType.UPCOMING to "Upcoming",
                        FilterType.OVERDUE to "Overdue",
                        FilterType.COMPLETED to "Completed"
                    )
                    items(filters) { (type, label) ->
                        FilterChipItem(
                            label = label,
                            selected = filterType == type && (type != FilterType.BY_CATEGORY),
                            onClick = { viewModel.setFilter(type) }
                        )
                    }
                    items(categories) { category ->
                        val catColor = try {
                            Color(android.graphics.Color.parseColor(category.colorHex))
                        } catch (e: Exception) { ElectricCyan }
                        FilterChipItem(
                            label = category.name,
                            selected = filterType == FilterType.BY_CATEGORY && filterCategoryId == category.id,
                            onClick = { viewModel.setFilter(FilterType.BY_CATEGORY, category.id) },
                            color = catColor
                        )
                    }
                }
            }

            // Tasks List
            if (tasks.isEmpty()) {
                item {
                    val emptyMsg = when {
                        searchQuery.isNotBlank() -> "No results found"
                        filterType == FilterType.COMPLETED -> "No completed tasks yet"
                        filterType == FilterType.OVERDUE -> "No overdue tasks — great job! 🎉"
                        filterType == FilterType.TODAY -> "No tasks due today"
                        else -> "Nothing here yet!"
                    }
                    val emptyIcon = when {
                        searchQuery.isNotBlank() -> Icons.Filled.SearchOff
                        else -> Icons.Filled.Inbox
                    }
                    EmptyState(
                        icon = emptyIcon,
                        title = emptyMsg,
                        subtitle = if (searchQuery.isNotBlank()) "Try a different search term" else "Tap + to add your first task ✨"
                    )
                }
            }

            itemsIndexed(
                items = tasks,
                key = { _, task -> task.id }
            ) { index, task ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { value ->
                        if (value == SwipeToDismissBoxValue.EndToStart) {
                            val deletedTask = task
                            viewModel.deleteTask(task)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Task deleted",
                                    actionLabel = "UNDO",
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.insertTask(deletedTask)
                                }
                            }
                            true
                        } else false
                    }
                )

                // Staggered entrance animation
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(index * 50L)
                    visible = true
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(300))
                ) {
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DestructiveRed),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                    modifier = Modifier.padding(end = 24.dp)
                                )
                            }
                        },
                        enableDismissFromStartToEnd = false,
                        enableDismissFromEndToStart = true
                    ) {
                        TaskCard(
                            task = task,
                            category = categoryMap[task.categoryId],
                            onCheckedChange = { viewModel.toggleTaskCompletion(task) },
                            onClick = { onNavigateToTaskDetail(task.id) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }

    // Sort/Filter Bottom Sheet
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            containerColor = DarkCard
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Sort By",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                SortOrder.entries.forEach { order ->
                    val label = when (order) {
                        SortOrder.DATE_CREATED -> "Date Created"
                        SortOrder.DUE_DATE -> "Due Date"
                        SortOrder.PRIORITY -> "Priority (High to Low)"
                        SortOrder.ALPHABETICAL -> "Alphabetical"
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (sortOrder == order) LocalAccentColor.current.copy(alpha = 0.1f) else Color.Transparent)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sortOrder == order,
                            onClick = {
                                viewModel.setSortOrder(order)
                                showSortSheet = false
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = LocalAccentColor.current,
                                unselectedColor = TextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(label, color = TextPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Task Actions Bottom Sheet
    if (showTaskActionsSheet && selectedTask != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showTaskActionsSheet = false
                selectedTask = null
            },
            containerColor = DarkCard
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    selectedTask!!.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = {
                        onNavigateToEditTask(selectedTask!!.id)
                        showTaskActionsSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = TextPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Edit Task", color = TextPrimary, modifier = Modifier.weight(1f))
                }
                TextButton(
                    onClick = {
                        viewModel.deleteTask(selectedTask!!)
                        showTaskActionsSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = DestructiveRed)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Delete Task", color = DestructiveRed, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatChip(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color = LocalAccentColor.current
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 13.sp) },
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = DarkCard,
            labelColor = TextSecondary,
            selectedContainerColor = color.copy(alpha = 0.15f),
            selectedLabelColor = color
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = DarkBorder,
            selectedBorderColor = color.copy(alpha = 0.3f),
            enabled = true,
            selected = selected
        )
    )
}

