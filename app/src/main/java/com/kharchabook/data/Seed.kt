package com.kharchabook.data

object Seed {

    val defaultCategories: List<Category> = listOf(
        Category(name = "Grocery", keywords = listOf(
            "grocery", "sabzi", "vegetable", "fruit", "milk", "doodh", "egg", "rice",
            "chawal", "atta", "flour", "oil", "ghee", "sugar", "cheeni", "daal", "dal",
            "chicken", "gosht", "meat", "saman"
        ), sortOrder = 1),

        Category(name = "Meal", keywords = listOf(
            "meal", "lunch", "dinner", "breakfast", "nashta", "burger", "pizza",
            "biryani", "karahi", "shawarma", "restaurant", "cafe", "chai", "tea",
            "coffee", "juice", "samosa", "paratha"
        ), sortOrder = 2),

        Category(name = "Bread", keywords = listOf(
            "bread", "double roti", "roti", "bun", "naan", "bakery", "rusk", "kulcha"
        ), sortOrder = 3),

        Category(name = "Transport", keywords = listOf(
            "transport", "petrol", "fuel", "cng", "rickshaw", "taxi", "careem",
            "indrive", "bus", "fare", "bike", "car", "parking", "toll", "uber"
        ), sortOrder = 4),

        Category(name = "Bills", keywords = listOf(
            "bill", "bijli", "electricity", "gas", "sui gas", "water", "internet",
            "wifi", "mobile", "phone", "recharge", "load", "rent", "kiraya"
        ), sortOrder = 5),

        Category(name = "Household", keywords = listOf(
            "household", "kitchen", "utensil", "soap", "surf", "detergent",
            "shampoo", "cleaning", "broom", "tissue", "wash"
        ), sortOrder = 6),

        Category(name = "Health", keywords = listOf(
            "health", "medicine", "dawa", "doctor", "hospital", "clinic",
            "pharmacy", "medical", "tablet"
        ), sortOrder = 7),

        Category(name = "Education", keywords = listOf(
            "fee", "school", "book", "copy", "stationery", "tuition", "academy"
        ), sortOrder = 8),

        Category(name = "Miscellaneous", keywords = listOf(
            "misc", "other", "kharcha", "random", "gift"
        ), sortOrder = 9)
    )
}
