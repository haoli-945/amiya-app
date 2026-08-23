package com.amiya.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * 食物数据模型 — 对应后端 FoodResponse
 */
data class Food(
    val id: Long,
    val name: String,

    @SerializedName("nameEn")
    val nameEn: String? = null,

    @SerializedName("caloriesPer100g")
    val caloriesPer100g: Int = 0,

    @SerializedName("proteinPer100g")
    val proteinPer100g: Double = 0.0,

    @SerializedName("carbsPer100g")
    val carbsPer100g: Double = 0.0,

    @SerializedName("fatPer100g")
    val fatPer100g: Double = 0.0,

    val category: String? = null,

    @SerializedName("isCommon")
    val isCommon: Boolean = false
) {
    /**
     * 根据克数计算实际热量
     */
    fun calcCalories(quantityG: Double): Int {
        return (caloriesPer100g * quantityG / 100.0).toInt()
    }

    /**
     * 根据克数计算实际蛋白质
     */
    fun calcProtein(quantityG: Double): Double {
        return proteinPer100g * quantityG / 100.0
    }
}
