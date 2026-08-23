package com.amiya.app.ui.screens

import android.net.Uri
import android.util.Base64
import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.amiya.app.data.api.ApiClient
import com.amiya.app.data.api.RecognizedFood
import com.amiya.app.data.api.RecognizeRequest
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "CameraScreen"

/**
 * 拍照识别页面
 *
 * 流程：拍照 → 显示照片 → AI识别 → 展示识别结果 → 确认入库
 */
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

    val photoUri = remember { createPhotoUri(context) }
    val cameraLauncher = rememberCameraLauncher(photoUri) { uri ->
        imageUri = uri
        recognizeImage(scope, context, uri) { foods, error ->
            recognizedFoods = foods
            errorMsg = error
            isAnalyzing = false
        }
        isAnalyzing = true
    }
    val galleryLauncher = rememberGalleryLauncher { uri ->
        imageUri = uri
        recognizeImage(scope, context, uri) { foods, error ->
            recognizedFoods = foods
            errorMsg = error
            isAnalyzing = false
        }
        isAnalyzing = true
    }

    Scaffold(
        topBar = { CameraTopBar(onBack) }
    ) { padding ->
        CameraContent(
            modifier = Modifier.padding(padding),
            imageUri = imageUri,
            isAnalyzing = isAnalyzing,
            errorMsg = errorMsg,
            recognizedFoods = recognizedFoods,
            onTakePhoto = { cameraLauncher.launch(photoUri) },
            onPickGallery = { galleryLauncher.launch("image/*") },
            onConfirm = onConfirm,
            onReset = {
                imageUri = null
                recognizedFoods = emptyList()
                errorMsg = null
            }
        )
    }
}

// ======================== 组件拆分 ========================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("拍照录入") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "返回")
            }
        }
    )
}

@Composable
private fun CameraContent(
    modifier: Modifier = Modifier,
    imageUri: Uri?,
    isAnalyzing: Boolean,
    errorMsg: String?,
    recognizedFoods: List<RecognizedFood>,
    onTakePhoto: () -> Unit,
    onPickGallery: () -> Unit,
    onConfirm: (List<RecognizedFood>) -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imageUri == null) {
            EmptyStateSection(onTakePhoto, onPickGallery)
        } else {
            ResultSection(imageUri, isAnalyzing, errorMsg, recognizedFoods, onConfirm, onReset)
        }
    }
}

@Composable
private fun EmptyStateSection(onTakePhoto: () -> Unit, onPickGallery: () -> Unit) {
    Spacer(modifier = Modifier.height(48.dp))
    Text("拍一张你的餐食照片", style = MaterialTheme.typography.headlineSmall)
    Text(
        "AI 会自动识别食物和估算份量",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(32.dp))

    Button(onClick = onTakePhoto, modifier = Modifier.fillMaxWidth().height(56.dp)) {
        Icon(Icons.Default.CameraAlt, null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("拍照")
    }
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedButton(onClick = onPickGallery, modifier = Modifier.fillMaxWidth().height(56.dp)) {
        Icon(Icons.Default.PhotoLibrary, null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("从相册选择")
    }
}

@Composable
private fun ResultSection(
    imageUri: Uri,
    isAnalyzing: Boolean,
    errorMsg: String?,
    recognizedFoods: List<RecognizedFood>,
    onConfirm: (List<RecognizedFood>) -> Unit,
    onReset: () -> Unit
) {
    PhotoPreview(imageUri)
    Spacer(modifier = Modifier.height(16.dp))

    when {
        isAnalyzing -> LoadingState()
        errorMsg != null -> ErrorState(errorMsg, onReset)
        recognizedFoods.isNotEmpty() -> RecognizedResultSection(recognizedFoods, onConfirm, onReset)
    }
}

@Composable
private fun PhotoPreview(uri: Uri) {
    Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = "餐食照片",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun LoadingState() {
    CircularProgressIndicator()
    Text("正在识别食物...")
}

@Composable
private fun ErrorState(message: String, onReset: () -> Unit) {
    Text(message, color = MaterialTheme.colorScheme.error)
    TextButton(onClick = onReset) { Text("重新拍照") }
}

@Composable
private fun RecognizedResultSection(
    foods: List<RecognizedFood>,
    onConfirm: (List<RecognizedFood>) -> Unit,
    onReset: () -> Unit
) {
    Text("识别结果", style = MaterialTheme.typography.titleMedium)
    LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(foods) { food -> RecognizedFoodItem(food) }
    }
    Button(
        onClick = { onConfirm(foods) },
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Icon(Icons.Default.Check, null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("确认记录 (${foods.sumOf { it.calories }} kcal)")
    }
    TextButton(onClick = onReset) { Text("重新拍照") }
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
                Text("${food.estimatedGrams}g", style = MaterialTheme.typography.bodySmall)
                if (!food.matched) {
                    Text("未匹配", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
            Text("${food.calories} kcal", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

// ======================== 工具函数 ========================

@Composable
private fun rememberCameraLauncher(uri: Uri, onResult: (Uri) -> Unit) =
    rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) onResult(uri)
    }

@Composable
private fun rememberGalleryLauncher(onResult: (Uri) -> Unit) =
    rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let(onResult)
    }

private fun createPhotoUri(context: android.content.Context): Uri {
    val file = File(context.cacheDir, "food_photo_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

private fun recognizeImage(
    scope: kotlinx.coroutines.CoroutineScope,
    context: android.content.Context,
    uri: Uri,
    onResult: (List<RecognizedFood>, String?) -> Unit
) {
    scope.launch {
        try {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                ?: throw IllegalStateException("无法读取图片")
            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
            val response = ApiClient.api.recognizeFood(RecognizeRequest(base64))
            onResult(response.data ?: emptyList(), null)
        } catch (e: Exception) {
            Log.e(TAG, "识别失败", e)
            onResult(emptyList(), "识别失败: ${e.message}")
        }
    }
}
