package com.kharchabook.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kharchabook.KharchaApp
import com.kharchabook.data.Category
import com.kharchabook.data.CategoryTotal
import com.kharchabook.data.Expense
import com.kharchabook.data.ExpenseRow
import com.kharchabook.util.CycleUtils
import com.kharchabook.util.ExpenseParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = (app as KharchaApp).repository
    private val settings = (app as KharchaApp).settings

    // ---------- Cycle ----------
    private val _cycleStart = MutableStateFlow(CycleUtils.cycleStart(System.currentTimeMillis(), settings.cycleStartDay))
    val cycleStart: StateFlow<Long> = _cycleStart

    val cycleRange: StateFlow<Pair<Long, Long>> = _cycleStart.map {
        it to CycleUtils.nextCycleStart(it, settings.cycleStartDay)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CycleUtils.cycleRange(System.currentTimeMillis(), settings.cycleStartDay))

    val cycleLabel: StateFlow<String> = cycleRange.map { (s, e) -> CycleUtils.cycleLabel(s, e) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun nextCycle() { _cycleStart.value = CycleUtils.nextCycleStart(_cycleStart.value, settings.cycleStartDay) }
    fun prevCycle() { _cycleStart.value = CycleUtils.prevCycleStart(_cycleStart.value, settings.cycleStartDay) }
    fun resetToCurrentCycle() { _cycleStart.value = CycleUtils.cycleStart(System.currentTimeMillis(), settings.cycleStartDay) }

    // ---------- Categories ----------
    val categories: StateFlow<List<Category>> =
        repo.categories().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addCategory(name: String, keywords: List<String>) {
        if (name.isBlank()) return
        viewModelScope.launch { repo.addCategory(name, keywords) }
    }

    fun updateCategory(c: Category) = viewModelScope.launch { repo.updateCategory(c) }

    fun deleteCategory(c: Category, onBlocked: () -> Unit) {
        viewModelScope.launch {
            val ok = repo.deleteCategory(c)
            if (!ok) onBlocked()
        }
    }

    // ---------- Totals ----------
    val todayTotal: StateFlow<Long> = flow {
        val now = System.currentTimeMillis()
        emitAll(repo.sum(CycleUtils.startOfDay(now), CycleUtils.endOfDayExclusive(now)))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0L)

    val cycleTotal: StateFlow<Long> = cycleRange.flatMapLatest { (s, e) -> repo.sum(s, e) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)

    val categoryTotals: StateFlow<List<CategoryTotal>> =
        cycleRange.flatMapLatest { (s, e) -> repo.categoryTotals(s, e) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // ---------- Lists ----------
    val cycleRows: StateFlow<List<ExpenseRow>> =
        cycleRange.flatMapLatest { (s, e) -> repo.rows(s, e) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val recentRows: StateFlow<List<ExpenseRow>> =
        repo.recent().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // ---------- Entry ----------
    data class Draft(val amount: Long?, val text: String, val suggestedCategory: Category?)

    fun previewDraft(input: String): Draft {
        val parsed = ExpenseParser.parse(input)
        val cats = categories.value
        val match = ExpenseParser.matchCategory(parsed.text, cats)
        return Draft(parsed.amount, parsed.text, match)
    }

    fun saveQuickEntry(input: String, overrideCategoryId: Long?, onError: (String) -> Unit, onDone: () -> Unit) {
        val parsed = ExpenseParser.parse(input)
        val amount = parsed.amount
        if (amount == null || amount <= 0) { onError("Enter an amount, e.g. 200 bread"); return }

        val cats = categories.value
        val cat = overrideCategoryId?.let { id -> cats.firstOrNull { it.id == id } }
            ?: ExpenseParser.matchCategory(parsed.text, cats)
            ?: cats.firstOrNull { it.name == "Miscellaneous" }
            ?: cats.firstOrNull()

        if (cat == null) { onError("Add a category first"); return }

        viewModelScope.launch {
            repo.addExpense(amount, cat.id, parsed.text, System.currentTimeMillis())
            onDone()
        }
    }

    fun saveFullEntry(amount: Long, categoryId: Long, note: String, timestamp: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.addExpense(amount, categoryId, note, timestamp)
            onDone()
        }
    }

    fun updateEntry(row: ExpenseRow, amount: Long, categoryId: Long, note: String, timestamp: Long) {
        viewModelScope.launch {
            repo.updateExpense(Expense(id = row.id, amount = amount, categoryId = categoryId, note = note, timestamp = timestamp))
        }
    }

    fun deleteEntry(row: ExpenseRow) = viewModelScope.launch { repo.deleteExpense(Expense(id = row.id, amount = row.amount, categoryId = row.categoryId, note = row.note, timestamp = row.timestamp)) }

    // ---------- Settings ----------
    val cycleStartDay: Int get() = settings.cycleStartDay
    val reminderEnabled: Boolean get() = settings.reminderEnabled
    val reminderHour: Int get() = settings.reminderHour
    val reminderMinute: Int get() = settings.reminderMinute

    fun setCycleStartDay(day: Int) {
        settings.cycleStartDay = day
        resetToCurrentCycle()
    }

    fun setReminder(enabled: Boolean, hour: Int, minute: Int, ctx: android.content.Context) {
        settings.reminderEnabled = enabled
        settings.reminderHour = hour
        settings.reminderMinute = minute
        if (enabled) com.kharchabook.reminder.Reminders.schedule(ctx, hour, minute)
        else com.kharchabook.reminder.Reminders.cancel(ctx)
    }
}
