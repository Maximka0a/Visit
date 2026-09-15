package com.example.visit.presentation.myCardScreen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visit.ui.theme.FigtreeFontFamily
import com.example.visit.ui.theme.Radius
import com.example.visit.ui.theme.Spacing
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class CardTheme(
    val id: Int,
    val displayName: String,
    val startColor: Color,
    val endColor: Color
)
object CardThemes {
    val all = listOf(
        CardTheme(id = 0, displayName = "Терракотовый", startColor = Color(0xFFD67F48), endColor = Color(0xFF8C491A)),
        CardTheme(id = 1, displayName = "Шалфей", startColor = Color(0xFFAEBF92), endColor = Color(0xFF56633F)),
        CardTheme(id = 2, displayName = "Закат", startColor = Color(0xFFF6A06B), endColor = Color(0xFF7A8A5E)),
        CardTheme(id = 3, displayName = "Чернила", startColor = Color(0xFF474238), endColor = Color(0xFF201E1D)),
    )

    fun findById(id: Int): CardTheme = all.firstOrNull { it.id == id } ?: all.first()
}

@Composable
fun MyCardRoute(
    onNavigateToEdit: () -> Unit,
    viewModel: CardViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyCardScreen(
        uiState,
        onQrIconClicked = viewModel::onQrIconClicked,
        onQrSheetDismissed = viewModel::onQrSheetDismissed,
        onNavigateToEdit = onNavigateToEdit,
        onThemeChange = viewModel::onThemeChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCardScreen(
    uiState: MyCardUiState,
    onQrIconClicked: () -> Unit,
    onQrSheetDismissed: () -> Unit,
    onNavigateToEdit:() -> Unit,
    onThemeChange:(Int) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cardGraphicsLayer = rememberGraphicsLayer()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "QR-визитка",
                fontFamily = FigtreeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (uiState.profile == null){
            Text(
                text = "Загрузка или профиль не заполнен",
                modifier = Modifier.padding(Spacing.lg)
            )
        }else{
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.xl)
            ) {
                BusinessCardCanvas(
                    name = uiState.profile.name,
                    title = uiState.profile.title?: "",
                    tags = uiState.profile.tags,
                    qrBitmap = uiState.qrBitmap,
                    onQrIconClicked = onQrIconClicked,
                    theme = CardThemes.findById(uiState.profile.themeId),
                    modifier = Modifier.drawWithContent {
                        // Записываем визитку в отдельный слой, чтобы по кнопке "Поделиться"
                        // можно было получить её как обычную картинку (Bitmap)
                        cardGraphicsLayer.record { this@drawWithContent.drawContent() }
                        drawLayer(cardGraphicsLayer)
                    },
                )

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Text(
                        text = "Оформление",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        CardThemes.all.forEach { theme ->
                            val isSelected = theme.id == uiState.profile.themeId
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(listOf(theme.startColor, theme.endColor))
                                    )
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { onThemeChange(theme.id) }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    OutlinedButton(
                        onClick = onNavigateToEdit,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(Radius.full)
                    ) {
                        Text("Редактировать")
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val bitmap = cardGraphicsLayer.toImageBitmap().asAndroidBitmap()
                                shareCardImage(context, bitmap)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(Radius.full)
                    ) {
                        Text("Поделиться")
                    }
                }
            }

            if (uiState.isQRSheetOpen){
                ModalBottomSheet(
                    onDismissRequest = {
                        onQrSheetDismissed()
                    }
                ) {
                    QrCodeDisplay(
                        name = uiState.profile.name,
                        title = uiState.profile.title ?: "",
                        bitmap = uiState.qrBitmap!!,
                        onShareClicked = {
                            shareCardImage(context, uiState.qrBitmap, fileName = "qr_code.png")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BusinessCardCanvas(
    name: String,
    title: String,
    tags: List<String>,
    qrBitmap: Bitmap?,
    onQrIconClicked: () -> Unit,
    theme: CardTheme,
    modifier: Modifier = Modifier

) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
    ) {
        // Градиентный фон
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        theme.startColor,
                        theme.endColor
                    )
                ),
                cornerRadius = CornerRadius(Radius.lg.toPx(), Radius.lg.toPx())
            )
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.lg)) {
            Row(verticalAlignment = Alignment.Top) {
                // Аватар с инициалами
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.24f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.md))

                Column(modifier = Modifier.weight(1f).padding(top = 2.dp)) {
                    Text(
                        text = name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    if (title.isNotBlank()) {
                        Text(
                            text = title,
                            color = Color.White.copy(alpha = 0.86f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(verticalAlignment = Alignment.Bottom) {
                FlowRow(
                    modifier = Modifier.weight(1f).padding(end = Spacing.xxxl + Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(Radius.full))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = tag, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        if (qrBitmap != null) {
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "Открыть QR-код",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Spacing.lg)
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(6.dp)
                    .clickable { onQrIconClicked() }
            )
        }
    }
}

private fun shareCardImage(context: Context, bitmap: Bitmap, fileName: String = "business_card.png") {
    val imagesDir = File(context.cacheDir, "shared_images").apply { mkdirs() }
    val file = File(imagesDir, fileName)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Поделиться визиткой"))
}

@Composable
fun QrCodeDisplay(
    name: String,
    title: String,
    bitmap: Bitmap,
    onShareClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.xl, vertical = Spacing.lg)
            .padding(bottom = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Text(text = name, style = MaterialTheme.typography.titleMedium)
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(Radius.lg))
                .background(Color.White)
                .padding(Spacing.lg)
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR-код визитки",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(Spacing.lg))

        Text(
            text = "Дайте отсканировать этот код, чтобы поделиться визиткой",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(Spacing.lg))

        Button(
            onClick = onShareClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(Radius.full)
        ) {
            Text("Поделиться QR-кодом")
        }
    }
}
