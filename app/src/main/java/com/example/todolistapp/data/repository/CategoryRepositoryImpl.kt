package com.example.todolistapp.data.repository

import com.example.todolistapp.data.local.dao.CategoryDao
import com.example.todolistapp.data.local.entity.CategoryEntity
import com.example.todolistapp.domain.model.Category
import com.example.todolistapp.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> =
        categoryDao.getAllCategories().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getCategoryById(id: Long): Category? =
        categoryDao.getCategoryById(id)?.toDomain()

    override suspend fun insertCategory(category: Category): Long =
        categoryDao.insertCategory(category.toEntity())

    override suspend fun updateCategory(category: Category) =
        categoryDao.updateCategory(category.toEntity())

    override suspend fun deleteCategory(category: Category) =
        categoryDao.deleteCategory(category.toEntity())

    private fun CategoryEntity.toDomain() = Category(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex
    )

    private fun Category.toEntity() = CategoryEntity(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex
    )
}
