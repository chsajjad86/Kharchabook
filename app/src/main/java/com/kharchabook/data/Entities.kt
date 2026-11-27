package com.kharchabook.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val keywords: List<String> = emptyList(),
    val sortOrder: Int = 0
)

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("categoryId"), Index("timestamp")]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Long,
    val categoryId: Long,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class ExpenseRow(
    val id: Long,
    val amount: Long,
    val categoryId: Long,
    val categoryName: String,
    val note: String,
    val timestamp: Long
)

data class CategoryTotal(
    val categoryId: Long,
    val categoryName: String,
    val total: Long
)
