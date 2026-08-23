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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amiya.app.data.api.DailyStats
import com.amiya.app.data.model.Meal

private const val DAILY_CALORIE_GOAL = 2000
private const val DAILY_PROTEIN_GOAL = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dailyStats: DailyStats?,
    todayMeals: List<Meal>,
    onNavigateToCamera: () -> Unit
) {
    Scaffold(
        topBar = { DashboardTopBar() },
        floatingActionButton = { CameraFAB(onNavigateToCamera) }
    ) { padding ->
        DashboardContent(padding, dailyStats, todayMeals)
    }
}

// ======================== 组件拆分 ========================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar() {
    TopAppBar(
        title = { Text("🐰 阿米娅") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun CameraFAB(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick, containerColor = MaterialTheme.colorScheme.primary) {
        Icon(Icons.Default.CameraAlt, contentDescription = "拍照录入")
    }
}

@Composable
private fun DashboardContent(padding: PaddingValues, stats: DailyStats?, meals: List<Meal>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { NutritionOverviewCard(stats) }
        item { SectionTitle("今日餐食") }
        if (meals.isEmpty()) {
            item { EmptyMealHint() }
        } else {
            items(meals) { meal -> MealCard(meal) }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun NutritionOverviewCard(stats: DailyStats?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("今日营养", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                NutrientItem("🔥 热量", "${stats?.totalCalories ?: 0}", "kcal", DAILY_CALORIE_GOAL)
                NutrientItem("🥩 蛋白质", "${stats?.totalProtein ?: 0}", "g", DAILY_PROTEIN_GOAL)
                NutrientItem("🍚 碳水", "${stats?.totalCarbs ?: 0}", "g", 0)
                NutrientItem("🧈 脂肪", "${stats?.totalFat ?: 0}", "g", 0)
            }
        }
    }
}

@Composable
private fun NutrientItem(label: String, value: String, unit: String, goal: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(unit, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun EmptyMealHint() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            "今天还没有记录哦～ 点击右下角拍照录入 📸",
            modifier = Modifier.padding(24.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun MealCard(meal: Meal) {
    val icon = getMealIcon(meal.mealType)
    val name = getMealName(meal.mealType)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            MealHeader(icon, name, meal.totalCalories)
            Spacer(modifier = Modifier.height(8.dp))
            meal.entries.forEach { entry ->
                Text("  ${entry.foodName ?: "未知食物"} ${entry.quantityG.toInt()}g")
            }
        }
    }
}

@Composable
private fun MealHeader(icon: String, name: String, calories: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$icon $name", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text("$calories kcal", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}

// ======================== 工具函数 ========================

private fun getMealIcon(type: String): String = when (type) {
    "BREAKFAST" -> "🌅"
    "LUNCH" -> "☀️"
    "DINNER" -> "🌙"
    else -> "🍪"
}

private fun getMealName(type: String): String = when (type) {
    "BREAKFAST" -> "早餐"
    "LUNCH" -> "午餐"
    "DINNER" -> "晚餐"
    "SNACK" -> "加餐"
    else -> type
}
