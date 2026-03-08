package com.example.todolistapp.ui.screens.categories

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todolistapp.domain.model.Category
import com.example.todolistapp.ui.components.ConfirmDialog
import com.example.todolistapp.ui.components.EmptyState
import com.example.todolistapp.ui.theme.*
import com.example.todolistapp.util.IconUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = viewModel(factory = CategoriesViewModel.Factory)
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var deletingCategory by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Black,
                    titleContentColor = TextPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = LocalAccentColor.current,
                contentColor = Black,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add category")
            }
        },
        containerColor = Black
    ) { padding ->
        if (categories.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Category,
                title = "No categories yet",
                subtitle = "Tap + to create a category",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories, key = { it.id }) { category ->
                    CategoryCard(
                        category = category,
                        taskCountFlow = viewModel.getTaskCountForCategory(category.id),
                        onEdit = { editingCategory = category },
                        onDelete = { deletingCategory = category }
                    )
                }
            }
        }
    }

    // Add Category Dialog
    if (showAddDialog) {
        CategoryEditDialog(
            title = "New Category",
            onDismiss = { showAddDialog = false },
            onSave = { name, icon, color ->
                viewModel.addCategory(name, icon, color)
                showAddDialog = false
            }
        )
    }

    // Edit Category Dialog
    if (editingCategory != null) {
        CategoryEditDialog(
            title = "Edit Category",
            initialName = editingCategory!!.name,
            initialIcon = editingCategory!!.iconName,
            initialColor = editingCategory!!.colorHex,
            onDismiss = { editingCategory = null },
            onSave = { name, icon, color ->
                viewModel.updateCategory(editingCategory!!.copy(name = name, iconName = icon, colorHex = color))
                editingCategory = null
            }
        )
    }

    // Delete Confirmation
    if (deletingCategory != null) {
        ConfirmDialog(
            title = "Delete Category",
            message = "Delete \"${deletingCategory!!.name}\"? Tasks in this category will be moved to Personal.",
            onConfirm = {
                viewModel.deleteCategory(deletingCategory!!)
                deletingCategory = null
            },
            onDismiss = { deletingCategory = null }
        )
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    taskCountFlow: kotlinx.coroutines.flow.Flow<Int>,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val taskCount by taskCountFlow.collectAsStateWithLifecycle(initialValue = 0)
    val catColor = try {
        Color(android.graphics.Color.parseColor(category.colorHex))
    } catch (e: Exception) { ElectricCyan }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = catColor.copy(alpha = 0.15f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        IconUtils.getIcon(category.iconName),
                        contentDescription = null,
                        tint = catColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    "$taskCount tasks",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = DestructiveRed.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun CategoryEditDialog(
    title: String,
    initialName: String = "",
    initialIcon: String = "Category",
    initialColor: String = "#00E5FF",
    onDismiss: () -> Unit,
    onSave: (name: String, icon: String, color: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var selectedIcon by remember { mutableStateOf(initialIcon) }
    var selectedColor by remember { mutableStateOf(initialColor) }
    var nameError by remember { mutableStateOf(false) }

    val colorOptions = listOf("#00E5FF", "#BB86FC", "#FF9800", "#30D158", "#FFD600", "#FF453A", "#64B5F6", "#FF6B6B", "#E040FB", "#00BCD4", "#8BC34A", "#FF5722")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text(title, fontWeight = FontWeight.Bold, color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text("Category Name") },
                    isError = nameError,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LocalAccentColor.current,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = LocalAccentColor.current,
                        focusedLabelColor = LocalAccentColor.current,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Icon", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(IconUtils.availableIcons.entries.toList()) { (iconName, icon) ->
                        val isSelected = selectedIcon == iconName
                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedIcon = iconName },
                            color = if (isSelected) LocalAccentColor.current.copy(alpha = 0.2f) else DarkSurface,
                            shape = RoundedCornerShape(10.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, LocalAccentColor.current) else null
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    icon,
                                    contentDescription = iconName,
                                    tint = if (isSelected) LocalAccentColor.current else TextSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                Text("Color", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(colorOptions) { colorHex ->
                        val color = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: Exception) { ElectricCyan }
                        val isSelected = selectedColor == colorHex
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (isSelected) Modifier.border(2.dp, TextPrimary, CircleShape)
                                    else Modifier
                                )
                                .clickable { selectedColor = colorHex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = Black, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        onSave(name.trim(), selectedIcon, selectedColor)
                    }
                }
            ) {
                Text("Save", color = LocalAccentColor.current, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

