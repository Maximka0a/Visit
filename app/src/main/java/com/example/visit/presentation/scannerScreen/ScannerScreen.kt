package com.example.visit.presentation.scannerScreen

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visit.domain.model.Profile
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    vm: ScannerViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    when {
        cameraPermissionState.status.isGranted -> {
            CameraPreview(onQrCodeScanned = vm::onQrCodeScanned )
            uiState.error?.let { Text(it) }
            if (uiState.scannedProfile !=null){
                ModalBottomSheet(
                    onDismissRequest = { vm.onDismissScannedProfile() }
                ) {
                    ScannedProfileBottomSheet(
                        profile = uiState.scannedProfile!!,
                        commonTags = uiState.commonTags,
                        onSaveClicked = {vm.onSaveContactClicked() },
                        onDismiss = {vm.onDismissScannedProfile()}
                    )
                }
            }
        }
        cameraPermissionState.status.shouldShowRationale -> {
            Text("Камера нужна для сканирования QR-кодов")
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Разрешить")
            }
        }
        else -> {
            Text("Нужен доступ к камере")
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Запросить доступ")
            }
        }
    }
}

@Composable
fun  ScannedProfileBottomSheet(
    profile: Profile,
    commonTags: List<String>,
    onSaveClicked: () -> Unit,
    onDismiss: () -> Unit,
){
    Column(

    ) {
        Text(profile.name)
        if (profile.title != null){
            Text(profile.title)
        }
            Row {
                profile.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (tag in commonTags){
                                    Color.Green.copy(alpha = 0.2f)
                                }else{
                                    Color.White.copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = tag, color = Color.White, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        Button(onSaveClicked) { }
        Text("Сохранить в  контакты")
        }
    }



@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(onQrCodeScanned: (String) -> Unit) {
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    val imageAnalysis = remember {
        ImageAnalysis.Builder().build()
    }
    val context = LocalContext.current
    imageAnalysis.setAnalyzer(
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


    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).await()
        val preview = Preview.Builder().build()
        preview.surfaceProvider = previewView.surfaceProvider
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview,  imageAnalysis)
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )
}

