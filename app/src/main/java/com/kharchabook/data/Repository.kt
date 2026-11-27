package com.kharchabook.data

import kotlinx.coroutines.flow.Flow

class Repository(
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao
) {
    fun categories(): Flow<List<Category>> = categoryDao.observeAll()
    suspend fun categoriesNow(): List<Category> = categoryDao.getAll()

    suspend fun addCategory(name: String, keywords: List<String> = emptyList()): Long {
        val order = (categoryDao.getAll().maxOfOrNull { it.sortOrder } ?: 0) + 1
        return categoryDao.insert(Category(name = name.trim(), keywords = keywords, sortOrder = order))
    }

    suspend fun updateCategory(category: Category) = categoryDao.update(category)

    suspend fun deleteCategory(category: Category): Boolean {
        if (categoryDao.expenseCount(category.id) > 0) return false
        categoryDao.delete(category)
        return true
    }

    suspend fun addExpense(amount: Long, categoryId: Long, note: String, timestamp: Long): Long =
        expenseDao.insert(Expense(amount = amount, categoryId = categoryId, note = note, timestamp = timestamp))

    suspend fun updateExpense(expense: Expense) = expenseDao.update(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.delete(expense)

    fun rows(start: Long, end: Long): Flow<List<ExpenseRow>> = expenseDao.observeRows(start, end)
    fun recent(): Flow<List<ExpenseRow>> = expenseDao.observeRecent()
    fun sum(start: Long, end: Long): Flow<Long> = expenseDao.observeSum(start, end)
    fun categoryTotals(start: Long, end: Long): Flow<List<CategoryTotal>> =
        expenseDao.observeCategoryTotals(start, end)

    suspend fun seedIfEmpty() {
        if (categoryDao.count() == 0) {
            Seed.defaultCategories.forEach { categoryDao.insert(it) }
        }
    }
}
