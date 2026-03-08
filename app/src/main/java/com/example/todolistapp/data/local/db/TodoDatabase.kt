package com.example.todolistapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todolistapp.data.local.dao.CategoryDao
import com.example.todolistapp.data.local.dao.TaskDao
import com.example.todolistapp.data.local.entity.CategoryEntity
import com.example.todolistapp.data.local.entity.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TaskEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao

    class Callback(
        private val categoryDaoProvider: () -> CategoryDao
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val categoryDao = categoryDaoProvider()
                categoryDao.insertAll(
                    listOf(
                        CategoryEntity(id = 1, name = "Personal", iconName = "Person", colorHex = "#00E5FF"),
                        CategoryEntity(id = 2, name = "Work", iconName = "Work", colorHex = "#BB86FC"),
                        CategoryEntity(id = 3, name = "Shopping", iconName = "ShoppingCart", colorHex = "#FF9800"),
                        CategoryEntity(id = 4, name = "Health", iconName = "FavoriteBorder", colorHex = "#30D158"),
                        CategoryEntity(id = 5, name = "Finance", iconName = "AccountBalance", colorHex = "#FFD600"),
                        CategoryEntity(id = 6, name = "Ideas", iconName = "Lightbulb", colorHex = "#FF453A")
                    )
                )
            }
        }
    }
}
