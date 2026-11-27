package com.kharchabook.util

import com.kharchabook.data.Category

object ExpenseParser {

    private val NUMBER = Regex("""\d{1,9}""")

    data class Parsed(val amount: Long?, val text: String)

    fun parse(input: String): Parsed {
        val m = NUMBER.find(input)
        val amount = m?.value?.toLongOrNull()
        val text = if (m != null) input.removeRange(m.range).trim() else input.trim()
        return Parsed(amount, text)
    }

    fun matchCategory(text: String, categories: List<Category>): Category? {
        val q = text.lowercase().trim()
        if (q.isEmpty()) return null
        var best: Category? = null
        var bestLen = 0
        for (c in categories) {
            for (kw in listOf(c.name) + c.keywords) {
                val k = kw.lowercase().trim()
                if (k.isEmpty()) continue
                if (q.contains(k) && k.length > bestLen) {
                    best = c
                    bestLen = k.length
                }
            }
        }
        return best
    }
}
