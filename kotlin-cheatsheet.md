# Kotlin 速成手册 — Java 程序员专属

> 你有 Java 基础，Kotlin 只需要学"差异点"。

---

## 1. 变量

```kotlin
val name = "阿米娅"        // val = final（不可变）
var age = 26               // var = 可变
val weight: Double = 65.5  // 显式类型
```

## 2. 函数

```kotlin
fun greet(name: String): String = "Hi $name"  // 字符串模板

// 默认参数（Java 做不到）
fun log(message: String, level: String = "INFO") = println("[$level] $message")
log("启动成功")           // INFO
log("出错", "ERROR")      // ERROR
```

## 3. 空安全（最大杀手锏）

```kotlin
var name: String = "阿米娅"   // 非空
var nick: String? = null      // 可空

val len = nick?.length ?: 0   // null 时返回 0，不崩溃
```

## 4. 数据类（一行顶 Java 50行）

```kotlin
data class Food(val id: Long, val name: String, val calories: Int)
val egg = Food(1, "鸡蛋", 144)
val egg2 = egg.copy(calories = 150)  // 复制并修改
```

## 5. 集合操作（最爽的部分）

```kotlin
val foods = listOf(egg, chicken, rice)
foods.filter { it.calories > 100 }.map { it.name }  // [鸡蛋, 鸡腿]
foods.sumOf { it.calories }                          // 总热量
foods.sortedBy { it.calories }                       // 按热量排序
```

## 6. when 表达式（替代 switch）

```kotlin
when (mealType) {
    "BREAKFAST" -> "早餐"
    "LUNCH"     -> "午餐"
    "DINNER"    -> "晚餐"
    else        -> "未知"
}
```

## 7. 协程（替代线程/回调）

```kotlin
suspend fun fetchFood(): Food = withContext(Dispatchers.IO) {
    api.getFood(1)  // 挂起不阻塞UI
}
```

## 速记口诀

| Java | Kotlin |
|------|--------|
| `new` | 直接调用 |
| `final` | `val` |
| `null` 检查 | `?.` `?:` |
| `getter` | 属性 `.name` |
| `switch` | `when` |
| `stream()` | `filter{}.map{}` |
| `CompletableFuture` | `suspend` 协程 |
