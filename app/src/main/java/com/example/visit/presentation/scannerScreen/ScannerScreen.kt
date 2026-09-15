package com.example.visit.presentation.scannerScreen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visit.domain.model.Profile
import com.example.visit.presentation.myCardScreen.BusinessCardCanvas
import com.example.visit.presentation.myCardScreen.CardThemes
import com.example.visit.ui.theme.Radius
import com.example.visit.ui.theme.Spacing
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

private val ScannerBackground = Color(0xFF201E1D)
private val ScannerAccent = Color(0xFFF6A06B)
private val ScannerOnBackground = Color(0xFFF5EAD8)
private val ScannerCircleBackground = Color(0xFF474238)
private val ScannerOnCircle = Color(0xFFC0B6A5)


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    vm: ScannerViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )
    var permissionRequested by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScannerBackground)
    ) {
        when {
            cameraPermissionState.status.isGranted -> {
                CameraPreview(onQrCodeScanned = vm::onQrCodeScanned)
                ViewfinderOverlay(modifier = Modifier.fillMaxSize())

                uiState.error?.let { message ->
                    Text(
                        text = message,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = Spacing.xl, start = Spacing.xl, end = Spacing.xl)
                            .clip(RoundedCornerShape(Radius.md))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                    )
                }

                if (uiState.scannedProfile != null) {
                    ModalBottomSheet(
                        onDismissRequest = { vm.onDismissScannedProfile() }
                    ) {
                        ScannedProfileBottomSheet(
                            profile = uiState.scannedProfile!!,
                            commonTags = uiState.commonTags,
                            onSaveClicked = { vm.onSaveContactClicked() },
                            onDismiss = { vm.onDismissScannedProfile() }
                        )
                    }
                }
            }
            cameraPermissionState.status.shouldShowRationale -> {
                PermissionMessage(
                    message = "Камера нужна для сканирования QR-кодов визиток.",
                    buttonText = "Разрешить",
                    onClick = {
                        permissionRequested = true
                        cameraPermissionState.launchPermissionRequest()
                    }
                )
            }
            permissionRequested -> {
                PermissionMessage(
                    message = "Доступ к камере запрещён. Разрешите его в настройках приложения.",
                    buttonText = "Открыть настройки",
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                )
            }
            else -> {
                PermissionMessage(
                    message = "Нужен доступ к камере, чтобы сканировать QR-коды визиток.",
                    buttonText = "Разрешить доступ к камере",
                    onClick = {
                        permissionRequested = true
                        cameraPermissionState.launchPermissionRequest()
                    }
                )
            }
        }
    }
}

@Composable
private fun PermissionMessage(
    message: String,
    buttonText: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(ScannerCircleBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.VideocamOff,
                contentDescription = null,
                tint = ScannerOnCircle,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = message,
            color = ScannerOnBackground,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(Spacing.lg))
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(Radius.full),
            colors = ButtonDefaults.buttonColors(
                containerColor = ScannerAccent,
                contentColor = Color(0xFF402310)
            )
        ) {
            Text(buttonText, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ViewfinderOverlay(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        ) {
            val squareSize = 220.dp.toPx()
            val left = (size.width - squareSize) / 2f
            val top = (size.height - squareSize) / 2f - 40.dp.toPx()
            val cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())

            drawRect(color = Color.Black.copy(alpha = 0.55f))
            drawRoundRect(
                topLeft = Offset(left, top),
                size = Size(squareSize, squareSize),
                cornerRadius = cornerRadius,
                color = Color.Transparent,
                blendMode = androidx.compose.ui.graphics.BlendMode.Clear
            )

            val bracket = 32.dp.toPx()
            val strokeWidth = 3.dp.toPx()
            fun corner(x: Float, y: Float, dx: Int, dy: Int) {
                drawLine(ScannerAccent, Offset(x, y), Offset(x + bracket * dx, y), strokeWidth)
                drawLine(ScannerAccent, Offset(x, y), Offset(x, y + bracket * dy), strokeWidth)
            }
            corner(left, top, 1, 1)
            corner(left + squareSize, top, -1, 1)
            corner(left, top + squareSize, 1, -1)
            corner(left + squareSize, top + squareSize, -1, -1)
        }
        Text(
            text = "Наведите на QR-код визитки",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = Spacing.xxl)
        )
    }
}

@Composable
fun ScannedProfileBottomSheet(
    profile: Profile,
    commonTags: List<String>,
    onSaveClicked: () -> Unit,
    onDismiss: () -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg)
            .padding(bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        BusinessCardCanvas(
            name = profile.name,
            title = profile.title ?: "",
            tags = profile.tags,
            qrBitmap = null,
            onQrIconClicked = {},
            theme = CardThemes.findById(profile.themeId),
        )

        if (commonTags.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text(
                    text = "ОБЩИЕ ИНТЕРЕСЫ",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    commonTags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Radius.full))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                        ) {
                            Text(text = tag, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Button(
                onClick = onSaveClicked,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(Radius.full)
            ) {
                Text("Сохранить контакт")
            }
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(0.7f)
                    .height(48.dp),
                shape = RoundedCornerShape(Radius.full)
            ) {
                Text("Закрыть")
            }
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(onQrCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    val imageAnalysis = remember {
        ImageAnalysis.Builder().build().also { analysis ->
            analysis.setAnalyzer(
                ContextCompat.getMainExecutor(context)
            ) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    barcodeScanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            val qrText = barcodes.firstOrNull()?.rawValue
                            if (qrText != null) {
                                onQrCodeScanned(qrText)
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                } else {
                    imageProxy.close()
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    LaunchedEffect(Unit) {
        val provider = ProcessCameraProvider.getInstance(context).await()
        cameraProvider = provider
        val preview = Preview.Builder().build()
        preview.surfaceProvider = previewView.surfaceProvider
        provider.unbindAll()
        provider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
            barcodeScanner.close()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )
}
