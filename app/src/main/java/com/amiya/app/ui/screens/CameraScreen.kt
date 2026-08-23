package com.amiya.app.ui.screens

import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Fontweight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.amiya.app.data.api.ApiClient
import com.amiya.app.data.api.RecognizedFood
import com.amiya.app.data.api.RecognizeRequest
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onBack: () -> Unit,
    onConfirm: (List<RecognizedFood>) -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var recognizedFoods by remember { mutableStateOf<List<RecognizedFood>>(emptyList()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 拍照 launcher
    val photoFile = remember {
        File(context.cacheDir, "food_photo_${System.currentTimeMillis()}.jpg")
    }
    val photoUri = remember {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = photoUri
            // 调用真实 API 识别
            scope.launch {
                isAnalyzing = true
                errorMsg = null
                try {
                    val bytes = context.contentResolver.openInputStream(photoUri)?.readBytes()
                    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    val response = ApiClient.api.recognizeFood(RecognizeRequest(base64))
                    recognizedFoods = response.data ?: emptyList()
                } catch (e: Exception) {
                    errorMsg = "识别失败: ${e.message}"
                }
                isAnalyzing = false
            }
        }
    }

    // 相册选择 launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            scope.launch {
                isAnalyzing = true
                errorMsg = null
                try {
                    val bytes = context.contentResolver.openInputStream(it)?.readBytes()
                    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    val response = ApiClient.api.recognizeFood(RecognizeRequest(base64))
                    recognizedFoods = response.data ?: emptyList()
                } catch (e: Exception) {
                    errorMsg = "识别失败: ${e.message}"
                }
                isAnalyzing = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("拍照录入") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imageUri == null) {
                // 还没拍照
                Spacer(modifier = Modifier.height(48.dp))
                Text("拍一张你的餐食照片", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "AI 会自动识别食物和估算份量",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { cameraLauncher.launch(photoUri) },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("拍照")
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("从相册选择")
                }

            } else {
                // 已拍照 — 显示照片 + 识别结果
                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "餐食照片",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (isAnalyzing) {
                    CircularProgressIndicator()
                    Text("正在识别食物...")
                } else if (errorMsg != null) {
                    Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { imageUri = null; errorMsg = null }) {
                        Text("重新拍照")
                    }
                } else if (recognizedFoods.isNotEmpty()) {
                    Text("识别结果", style = MaterialTheme.typography.titleMedium)
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(recognizedFoods) { food ->
                            RecognizedFoodItem(food)
                        }
                    }
                    Button(
                        onClick = { onConfirm(recognizedFoods) },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Icon(Icons.Default.Check, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("确认记录 (${recognizedFoods.sumOf { it.calories }} kcal)")
                    }
                    TextButton(onClick = { imageUri = null; recognizedFoods = emptyList() }) {
                        Text("重新拍照")
                    }
                }
            }
        }
    }
}

@Composable
fun RecognizedFoodItem(food: RecognizedFood) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(food.name, fontWeight = FontWeight.Bold)
                Text(
                    "${food.estimatedGrams}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!food.matched) {
                    Text("未匹配", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
            Text(
                "${food.calories} kcal",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
