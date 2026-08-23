package com.amiya.app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 单例 — 管理 API 连接
 */
object ApiClient {

    // ⚠️ 后端地址，根据实际情况修改
    private const val BASE_URL = "http://10.0.2.2:8080/"  // 模拟器用 10.0.2.2
    // 真机调试时改成你电脑的 IP，如 "http://192.168.1.100:8080/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)

    /**
     * 更新后端地址（真机调试时调用）
     */
    fun updateBaseUrl(newBaseUrl: String) {
        // 需要重建 Retrofit 实例
        // 实际项目中可以用 OkHttp 的动态 URL 方案
    }
}
