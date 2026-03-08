package com.example.todolistapp.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconUtils {
    val availableIcons: Map<String, ImageVector> = mapOf(
        "Person" to Icons.Filled.Person,
        "Work" to Icons.Filled.Work,
        "ShoppingCart" to Icons.Filled.ShoppingCart,
        "FavoriteBorder" to Icons.Filled.FavoriteBorder,
        "AccountBalance" to Icons.Filled.AccountBalance,
        "Lightbulb" to Icons.Filled.Lightbulb,
        "Home" to Icons.Filled.Home,
        "School" to Icons.Filled.School,
        "Star" to Icons.Filled.Star,
        "Bookmark" to Icons.Filled.Bookmark,
        "Build" to Icons.Filled.Build,
        "Call" to Icons.Filled.Call,
        "Camera" to Icons.Filled.CameraAlt,
        "DirectionsCar" to Icons.Filled.DirectionsCar,
        "Email" to Icons.Filled.Email,
        "Flight" to Icons.Filled.Flight,
        "Fitness" to Icons.Filled.FitnessCenter,
        "LocalDining" to Icons.Filled.LocalDining,
        "MusicNote" to Icons.Filled.MusicNote,
        "Pets" to Icons.Filled.Pets,
        "Sports" to Icons.Filled.SportsEsports,
        "Palette" to Icons.Filled.Palette,
        "Code" to Icons.Filled.Code,
        "LocalGroceryStore" to Icons.Filled.LocalGroceryStore,
        "Celebration" to Icons.Filled.Celebration,
        "MenuBook" to Icons.AutoMirrored.Filled.MenuBook,
        "Bolt" to Icons.Filled.Bolt,
        "Eco" to Icons.Filled.Eco,
        "Rocket" to Icons.Filled.RocketLaunch,
        "Category" to Icons.Filled.Category
    )

    fun getIcon(name: String): ImageVector {
        return availableIcons[name] ?: Icons.Filled.Category
    }
}

