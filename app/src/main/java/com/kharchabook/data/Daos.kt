package com.kharchabook.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, name ASC")
    fun observeAll(): Flow<List<Category>>

    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, name ASC")
    suspend fun getAll(): List<Category>

    @Insert
    suspend fun insert(category: Category): Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT COUNT(*) FROM expenses WHERE categoryId = :categoryId")
    suspend fun expenseCount(categoryId: Long): Int

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query(
        """
        SELECT e.id AS id, e.amount AS amount, e.categoryId AS categoryId,
               c.name AS categoryName, e.note AS note, e.timestamp AS timestamp
        FROM expenses e JOIN categories c ON c.id = e.categoryId
        WHERE e.timestamp >= :start AND e.timestamp < :end
        ORDER BY e.timestamp DESC, e.id DESC
        """
    )
    fun observeRows(start: Long, end: Long): Flow<List<ExpenseRow>>

    @Query(
        """
        SELECT e.id AS id, e.amount AS amount, e.categoryId AS categoryId,
               c.name AS categoryName, e.note AS note, e.timestamp AS timestamp
        FROM expenses e JOIN categories c ON c.id = e.categoryId
        ORDER BY e.timestamp DESC, e.id DESC
        LIMIT 200
        """
    )
    fun observeRecent(): Flow<List<ExpenseRow>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE timestamp >= :start AND timestamp < :end")
    fun observeSum(start: Long, end: Long): Flow<Long>

    @Query(
        """
        SELECT e.categoryId AS categoryId, c.name AS categoryName,
               SUM(e.amount) AS total
        FROM expenses e JOIN categories c ON c.id = e.categoryId
        WHERE e.timestamp >= :start AND e.timestamp < :end
        GROUP BY e.categoryId, c.name
        ORDER BY total DESC
        """
    )
    fun observeCategoryTotals(start: Long, end: Long): Flow<List<CategoryTotal>>
}
