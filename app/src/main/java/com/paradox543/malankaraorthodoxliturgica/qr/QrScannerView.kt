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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.zxing.qrcode.encoder.QRCode
import com.paradox543.malankaraorthodoxliturgica.BuildConfig
import com.paradox543.malankaraorthodoxliturgica.data.model.Screen
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import kotlinx.coroutines.delay

data class ScannerMessage(
    val text: String,
    val color: Color,
)

@Composable
fun QrScannerView(navController: NavController) {
    var code by remember { mutableStateOf("") }
    var useHybrid by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    var message by remember { mutableStateOf(
        ScannerMessage(
            "Point camera at QR code",
            colorScheme.onSurface
            )
        )
    }
    var lockedRoute by remember { mutableStateOf("")}
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
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

    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(code) {
        if (!hasNavigated && code.startsWith("app://liturgica/")) {
            lockedRoute = code.replace("app://liturgica/", "")
        }
        if (!hasNavigated) {
            message = when {
                code.isEmpty() -> ScannerMessage(
                    "Point camera at QR code",
                    colorScheme.onSurface
                )
                !code.startsWith("app://liturgica/") -> ScannerMessage(
                    "Invalid QR code",
                    colorScheme.error
                )
                else -> ScannerMessage(
                    "Valid QR code detected",
                    colorScheme.tertiary
                )
            }
        }
    }

    LaunchedEffect(lockedRoute) {
        if (lockedRoute.isNotEmpty() && !hasNavigated) {
            Log.d("QR Scanner View", "Navigating to $lockedRoute")
            hasNavigated = true
            delay(500) // short pause so the UI shows "QR detected"
            navController.navigate(lockedRoute) {
                launchSingleTop = true
                navController.popBackStack(Screen.QrScanner.route, true)
            }
        }
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
                                        if (useHybrid) {
                                            HybridQRAnalyzer { qrCode ->
                                                code = qrCode
                                            }
                                        } else {
                                            MLKitQRCodeAnalyzer { qrCode ->
                                                code = qrCode
                                            }
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

                    QrScannerOverlay(
                        isDetected = code.startsWith("app://liturgica/")
                    )
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = message.color,
                    )
                ) {
                    Text(
                        text = message.text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}