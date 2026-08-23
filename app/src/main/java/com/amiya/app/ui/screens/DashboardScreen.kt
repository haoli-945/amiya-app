package com.amiya.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amiya.app.data.model.DailyStats
import com.amiya.app.data.model.Meal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dailyStats: DailyStats?,
    todayMeals: List<Meal>,
    onNavigateToCamera: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🐰 阿米娅") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCamera,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "拍照录入")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 营养概览卡片
            item {
                NutritionOverviewCard(dailyStats)
            }

            // 今日餐食标题
            item {
                Text(
                    "今日餐食",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // 餐食列表
            if (todayMeals.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            "今天还没有记录哦～ 点击右下角拍照录入 📸",
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                items(todayMeals) { meal ->
                    MealCard(meal)
                }
            }
        }
    }
}

@Composable
fun NutritionOverviewCard(stats: DailyStats?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "今日营养",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutrientItem("🔥 热量", "${stats?.totalCalories ?: 0}", "kcal")
                NutrientItem("🥩 蛋白质", "${stats?.totalProtein ?: 0}", "g")
                NutrientItem("🍚 碳水", "${stats?.totalCarbs ?: 0}", "g")
                NutrientItem("🧈 脂肪", "${stats?.totalFat ?: 0}", "g")
            }
        }
    }
}

@Composable
fun NutrientItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(unit, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun MealCard(meal: Meal) {
    val mealIcon = when (meal.mealType) {
        "BREAKFAST" -> "🌅"
        "LUNCH" -> "☀️"
        "DINNER" -> "🌙"
        else -> "🍪"
    }
    val mealName = when (meal.mealType) {
        "BREAKFAST" -> "早餐"
        "LUNCH" -> "午餐"
        "DINNER" -> "晚餐"
        "SNACK" -> "加餐"
        else -> meal.mealType
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$mealIcon $mealName",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "${meal.totalCalories} kcal",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            meal.entries.forEach { entry ->
                Text(
                    "  ${entry.foodName ?: "未知食物"} ${entry.quantityG.toInt()}g",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
