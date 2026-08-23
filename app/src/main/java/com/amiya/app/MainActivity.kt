package com.amiya.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amiya.app.data.api.ApiClient
import com.amiya.app.data.model.DailyStats
import com.amiya.app.data.model.Meal
import com.amiya.app.ui.screens.*
import com.amiya.app.ui.theme.AmiyaTheme
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmiyaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AmiyaApp()
                }
            }
        }
    }
}

@Composable
fun AmiyaApp() {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    // 今日数据
    val today = LocalDate.now().toString()
    var dailyStats by remember { mutableStateOf<DailyStats?>(null) }
    var todayMeals by remember { mutableStateOf<List<Meal>>(emptyList()) }

    // 加载今日数据
    LaunchedEffect(today) {
        try {
            dailyStats = ApiClient.api.getDailyStats(today)
            todayMeals = ApiClient.api.getMealsByDate(today)
        } catch (e: Exception) {
            // 网络错误时使用空数据
            dailyStats = DailyStats(today, 0, 0, 0, 0, 0)
            todayMeals = emptyList()
        }
    }

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                dailyStats = dailyStats,
                todayMeals = todayMeals,
                onNavigateToCamera = { navController.navigate("camera") }
            )
        }

        composable("camera") {
            CameraScreen(
                onBack = { navController.popBackStack() },
                onConfirm = { recognizedFoods ->
                    // TODO: 调用 API 入库
                    coroutineScope.launch {
                        // 转换为 API 请求并提交
                        // recognizedFoods → MealRecordRequest → ApiClient.api.recordMeal(...)
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
