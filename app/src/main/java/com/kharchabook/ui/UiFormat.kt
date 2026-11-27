package com.kharchabook.ui

import java.text.NumberFormat
import java.util.Locale

private val nf: NumberFormat = NumberFormat.getIntegerInstance(Locale.US)

fun pkr(amount: Long): String = "PKR " + nf.format(amount)
