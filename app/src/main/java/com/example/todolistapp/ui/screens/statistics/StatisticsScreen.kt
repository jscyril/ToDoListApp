package com.example.todolistapp.ui.screens.statistics

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todolistapp.domain.model.Priority
import com.example.todolistapp.ui.theme.*
import com.example.todolistapp.util.IconUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = viewModel(factory = StatisticsViewModel.Factory)
) {
    val totalTasks by viewModel.totalTasks.collectAsStateWithLifecycle()
    val completedTasks by viewModel.completedTasks.collectAsStateWithLifecycle()
    val pendingTasks by viewModel.pendingTasks.collectAsStateWithLifecycle()
    val overdueTasks by viewModel.overdueTasks.collectAsStateWithLifecycle()
    val completedThisWeek by viewModel.completedThisWeek.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Completion Rate Circle
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Completion Rate",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    val completionRate = if (totalTasks > 0) completedTasks.toFloat() / totalTasks.toFloat() else 0f
                    val animatedProgress by animateFloatAsState(
                        targetValue = completionRate,
                        animationSpec = tween(1000, easing = FastOutSlowInEasing),
                        label = "progress"
                    )

                    Box(contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(160.dp)) {
                            val strokeWidth = 12.dp.toPx()
                            drawCircle(
                                color = DarkGrey,
                                radius = (size.minDimension - strokeWidth) / 2,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = ElectricCyan,
                                startAngle = -90f,
                                sweepAngle = animatedProgress * 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                size = Size(size.width - strokeWidth, size.height - strokeWidth)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${(animatedProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text("completed", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }

            // Overview Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Total", totalTasks, LocalAccentColor.current, Modifier.weight(1f))
                StatCard("Done", completedTasks, SuccessGreen, Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Pending", pendingTasks, WarningOrange, Modifier.weight(1f))
                StatCard("Overdue", overdueTasks, DestructiveRed, Modifier.weight(1f))
            }

            // Completed This Week
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Completed This Week", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Text(
                            completedThisWeek.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Tasks by Category
            Text("By Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            categories.forEach { category ->
                val count by viewModel.getTaskCountForCategory(category.id).collectAsStateWithLifecycle(initialValue = 0)
                val catColor = try {
                    Color(android.graphics.Color.parseColor(category.colorHex))
                } catch (e: Exception) { ElectricCyan }

                val barProgress by animateFloatAsState(
                    targetValue = if (totalTasks > 0) count.toFloat() / totalTasks.toFloat() else 0f,
                    animationSpec = tween(800), label = "bar"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        IconUtils.getIcon(category.iconName),
                        contentDescription = null,
                        tint = catColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        category.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        modifier = Modifier.width(80.dp)
                    )
                    Box(
                        modifier = Modifier.weight(1f).height(12.dp).clip(RoundedCornerShape(6.dp)).background(DarkGrey)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(barProgress)
                                .clip(RoundedCornerShape(6.dp))
                                .background(catColor)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(count.toString(), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            // Tasks by Priority
            Text("By Priority", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Priority.entries.forEach { priority ->
                val count by viewModel.getTaskCountForPriority(priority.level).collectAsStateWithLifecycle(initialValue = 0)
                val pColor = when (priority) {
                    Priority.LOW -> PriorityLow
                    Priority.MEDIUM -> PriorityMedium
                    Priority.HIGH -> PriorityHigh
                    Priority.URGENT -> PriorityUrgent
                }

                val barProgress by animateFloatAsState(
                    targetValue = if (totalTasks > 0) count.toFloat() / totalTasks.toFloat() else 0f,
                    animationSpec = tween(800), label = "pbar"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(pColor))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        priority.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        modifier = Modifier.width(80.dp)
                    )
                    Box(
                        modifier = Modifier.weight(1f).height(12.dp).clip(RoundedCornerShape(6.dp)).background(DarkGrey)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(barProgress)
                                .clip(RoundedCornerShape(6.dp))
                                .background(pColor)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(count.toString(), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun StatCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = tween(800),
        label = "count"
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                animatedCount.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

