# 阿米娅 App Kotlin 编码规范

> 基于蚂蚁 A1 级编码规范，适配 Kotlin + Android 开发

## 核心原则（和后端一致）

1. **防御性编程** — 公开方法必须参数校验
2. **单一职责** — 类不超过 300 行，方法不超过 30 行
3. **类型安全** — 优先使用 sealed class，避免 nullable 滥用
4. **不可变优先** — 优先 val，只在必要时用 var

## 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 包名 | 全小写 | `com.amiya.app.data.api` |
| 类名 | UpperCamelCase | `FoodRepository` |
| 函数名 | lowerCamelCase | `loadDailyStats()` |
| 常量 | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| 布尔变量 | is/has/can 前缀 | `isCommon` |
| Compose 函数 | UpperCamelCase | `DashboardScreen()` |
| 扩展函数 | lowerCamelCase | `String.isEmail()` |

## Compose UI 规范

```kotlin
// ✅ 每个 Screen 拆分为独立的 @Composable 函数
@Composable
fun DashboardScreen(...) {
    Column {
        NutritionOverviewCard(stats)
        MealListSection(meals)
    }
}

// ✅ 组件函数名用名词，事件回调用 on 前缀
@Composable
fun MealCard(meal: Meal, onMealClick: (Meal) -> Unit)

// ❌ 禁止在 Composable 中写超过 30 行的函数
```

## 异常处理规范

```kotlin
// ✅ 保留异常链
try {
    api.getFood(id)
} catch (e: IOException) {
    Log.e(TAG, "网络请求失败: foodId=$id", e)
    throw ApiException("网络错误", e)
} catch (e: Exception) {
    Log.e(TAG, "未知错误", e)
    throw ApiException("操作失败", e)
}

// ❌ 禁止吞掉异常
catch (e: Exception) { }  // 绝对不行
```

## 日志规范

```kotlin
// ✅ TAG 统一定义
companion object {
    private const val TAG = "FoodApi"
}

// ✅ 关键参数用占位符
Log.d(TAG, "搜索食物: keyword=$keyword, count=${results.size}")

// ❌ 禁止字符串拼接
Log.d(TAG, "搜索食物: " + keyword)  // 性能差
```

## 架构分层

```
ui/          ← 界面层（Compose + ViewModel）
  screens/   ← 页面组件（≤30行/函数）
  components/← 可复用组件
  theme/     ← 主题
data/        ← 数据层
  api/       ← Retrofit 接口
  model/     ← 数据模型
  repository/← 仓储实现（复杂项目用）
util/        ← 工具类
```

## 方法长度红线

- **普通函数** ≤ 30 行
- **Composable 函数** ≤ 50 行（UI 代码天然较长）
- **超过必须拆分** — 提取子组件或工具函数
