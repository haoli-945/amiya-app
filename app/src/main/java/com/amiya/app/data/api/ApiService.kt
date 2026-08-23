package com.amiya.app.data.api

import com.amiya.app.data.model.Food
import com.amiya.app.data.model.Meal
import retrofit2.http.*

/**
 * Retrofit API 接口 — 对应后端 Controller
 */
interface ApiService {

    // ======================== 食物 ========================

    @GET("api/v1/foods/common")
    suspend fun getCommonFoods(): ApiResponse<List<Food>>

    @GET("api/v1/foods/search")
    suspend fun searchFoods(@Query("keyword") keyword: String): ApiResponse<List<Food>>

    @POST("api/v1/foods/recognize")
    suspend fun recognizeFood(@Body request: RecognizeRequest): ApiResponse<List<RecognizedFood>>

    // ======================== 餐食 ========================

    @GET("api/v1/meals")
    suspend fun getMealsByDate(@Query("date") date: String): ApiResponse<List<Meal>>

    @POST("api/v1/meals")
    suspend fun recordMeal(@Body request: MealRecordRequest): ApiResponse<Meal>

    @GET("api/v1/meals/daily-stats")
    suspend fun getDailyStats(@Query("date") date: String): ApiResponse<DailyStats>
}

// ======================== 通用响应包装 ========================

data class ApiResponse<T>(
    val code: Int,
    val msg: String,
    val data: T?
)

// ======================== 请求/响应 DTO ========================

data class RecognizeRequest(val image: String)

data class RecognizedFood(
    val name: String,
    val foodId: Long?,
    val matched: Boolean,
    val estimatedGrams: Int,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)

data class MealRecordRequest(
    val date: String,
    val mealType: String,
    val note: String? = null,
    val entries: List<Entry>
) {
    data class Entry(val foodId: Long, val quantityG: Double)
}

data class DailyStats(
    val date: String,
    val totalCalories: Int = 0,
    val totalProtein: Int = 0,
    val totalCarbs: Int = 0,
    val totalFat: Int = 0
)
