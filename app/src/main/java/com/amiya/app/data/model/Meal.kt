package com.amiya.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * 餐食记录 — 对应后端 MealResponse
 */
data class Meal(
    val id: Long,
    val date: String,
    val mealType: String,
    val note: String? = null,

    @SerializedName("totalCalories")
    val totalCalories: Int = 0,

    @SerializedName("totalProtein")
    val totalProtein: Int = 0,

    @SerializedName("totalCarbs")
    val totalCarbs: Int = 0,

    @SerializedName("totalFat")
    val totalFat: Int = 0,

    val entries: List<MealEntry> = emptyList()
)

data class MealEntry(
    val id: Long,
    val foodId: Long,
    val foodName: String? = null,

    @SerializedName("quantityG")
    val quantityG: Double = 0.0,

    val calories: Int = 0,
    val protein: Double = 0.0
)
