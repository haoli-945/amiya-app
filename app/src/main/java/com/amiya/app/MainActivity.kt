package com.amiya.app

import android.os.Bundle
import android.util.Log
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
import com.amiya.app.data.api.DailyStats
import com.amiya.app.data.model.Meal
import com.amiya.app.ui.screens.*
import com.amiya.app.ui.theme.AmiyaTheme
import java.time.LocalDate

private const val TAG = "MainActivity"

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
    val today = LocalDate.now().toString()
    val dailyStats = rememberDailyStats(today)
    val todayMeals = rememberTodayMeals(today)

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
                onConfirm = { foods -> navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun rememberDailyStats(date: String): DailyStats? {
    var stats by remember { mutableStateOf<DailyStats?>(null) }
    LaunchedEffect(date) {
        try {
            stats = ApiClient.api.getDailyStats(date).data
        } catch (e: Exception) {
            Log.e(TAG, "加载每日统计失败", e)
        }
    }
    return stats
}

@Composable
private fun rememberTodayMeals(date: String): List<Meal> {
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    LaunchedEffect(date) {
        try {
            meals = ApiClient.api.getMealsByDate(date).data ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "加载今日餐食失败", e)
        }
    }
    return meals
}
