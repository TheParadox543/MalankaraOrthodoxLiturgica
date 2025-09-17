package com.paradox543.malankaraorthodoxliturgica.qr

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.BuildConfig
import com.paradox543.malankaraorthodoxliturgica.data.model.Screen
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar

enum class Decoder {
    Zxing, MLKit
}

@Composable
fun QrScannerView(navController: NavController) {
    var code by remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )
    var decoder by remember { mutableStateOf(Decoder.Zxing) }

    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = { TopNavBar("QR Scanner", navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (hasCamPermission) {
                Box(
                    Modifier.weight(0.5f)
                ) {
                    if (decoder == Decoder.Zxing) {
                        AndroidView (
                            factory = { context ->
                                val previewView = PreviewView(context)
                                val preview = Preview.Builder().build()
                                val selector = CameraSelector.Builder()
                                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                    .build()
                                preview.surfaceProvider = previewView.surfaceProvider
                                val imageAnalysis = ImageAnalysis.Builder()
//                                .setTargetResolution(
//                                    Size(
//                                        previewView.width,
//                                        previewView.height
//                                    )
//                                )
                                    .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                imageAnalysis.setAnalyzer(
                                    ContextCompat.getMainExecutor(context),
                                    ZxingQrCodeAnalyzer { result ->
                                        code = result
                                    }
                                )
                                try {
                                    cameraProviderFuture.get().bindToLifecycle(
                                        lifecycleOwner,
                                        selector,
                                        preview,
                                        imageAnalysis
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (decoder == Decoder.MLKit) {
                        AndroidView(factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().apply {
                                    surfaceProvider = previewView.surfaceProvider
                                }
                                val analyzer = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                                    .build().also {
                                        it.setAnalyzer(
                                            ContextCompat.getMainExecutor(ctx),
                                            MLKitQRCodeAnalyzer { qrCode ->
                                                code = qrCode
                                            }
                                        )
                                    }
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    analyzer
                                )
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        }, modifier = Modifier.fillMaxSize())
                    }

                    QrScannerOverlay(
                        isDetected = code.startsWith("app://liturgica/")
                    )
                }
                if (BuildConfig.DEBUG) {
                    Row (
                        Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Button (
                            onClick = {
                                decoder = Decoder.Zxing
                            },
                            enabled = decoder != Decoder.Zxing,
                        ) {
                           Text("Zebra Crossing")
                        }
                        Button (
                            onClick = {
                                decoder = Decoder.MLKit
                            },
                            enabled = decoder != Decoder.MLKit,
                        ) {
                            Text("ML Kit")
                        }
                    }
                }
                Card {
                    Text(
                        text = code,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }
                if (code.startsWith("app://liturgica/")) {
                    LaunchedEffect(key1 = code) {
                        val route = code.replace("app://liturgica/", "")
                        Log.d("QR Scanner View", "Navigating to $route")
//                        delay(100)
                        if (route.isNotEmpty()) {
                            navController.navigate(route) {
                                launchSingleTop = true
                                navController.popBackStack(Screen.QrScanner.route, true)
                            }
                        }
                    }
                }
            }
        }
    }
}