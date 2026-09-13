package com.example.visit.presentation.myCardScreen

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visit.domain.model.Profile

data class CardTheme(
    val id: Int,
    val startColor: Color,
    val endColor: Color
)
object CardThemes {
    val all = listOf(
        CardTheme(id = 0, startColor = Color(0xFFB5651D), endColor = Color(0xFF8B4513)), // коричневый
        CardTheme(id = 1, startColor = Color(0xFF6B8E5A), endColor = Color(0xFF3F5C33)), // зелёный
        CardTheme(id = 2, startColor = Color(0xFFD2A679), endColor = Color(0xFFB5651D)), // песочный
        CardTheme(id = 3, startColor = Color(0xFF3A3A3A), endColor = Color(0xFF1A1A1A))  // чёрный
    )

    fun findById(id: Int): CardTheme = all.firstOrNull { it.id == id } ?: all.first()
}
@Composable
fun MyCardRoute(
    onNavitateToEdit: () -> Unit,
    viewModel: CardViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyCardScreen(
        uiState,
        onQrIconClicked = viewModel::onQrIconClicked,
        onQrSheetDismissed = viewModel::onQrSheetDismissed,
        onNavitateToEdit = onNavitateToEdit,
        onThemeChange = viewModel::onThemeChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCardScreen(
    uiState: MyCardUiState,
    onQrIconClicked: () -> Unit,
    onQrSheetDismissed: () -> Unit,
    onNavitateToEdit:() -> Unit,
    onThemeChange:(Int) -> Unit
) {
    if (uiState.profile == null){
        Text("Загрузка или профиль не заполнен ")
    }else{
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            BuisessCardCanvas(
                name = uiState.profile.name,
                title = uiState.profile.title?: "",
                tags = uiState.profile.tags,
                qrBitmap = uiState.qrBitmap,
                onQrIconClicked = onQrIconClicked,
                theme = CardThemes.findById(uiState.profile.themeId),
                modifier = Modifier,
            )

            Spacer(Modifier.height(15.dp))
            Row() {
                CardThemes.all.forEach { theme ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(theme.startColor)
                            .clickable { onThemeChange(theme.id) }
                    ){

                    }
                }
            }


            Spacer(Modifier.height(15.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onNavitateToEdit()
                }
            ) {
                Text("Редактировать ")
            }

            if (uiState.isQRSheetOpen){
                ModalBottomSheet(
                    onDismissRequest = {
                        onQrSheetDismissed()
                    }
                ) {
                    QrCodeDisplay(uiState.qrBitmap!!)
                }
            }
        }

    }
}

@Composable
fun BuisessCardCanvas(
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
            .height(160.dp)
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
                cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар с инициалами
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Имя, должность, теги
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = tag, color = Color.White, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
            
            if (qrBitmap != null) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "Открыть QR-код",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(4.dp)
                        .clickable { onQrIconClicked() }
                )
            }
        }
    }
}
@Composable
fun QrCodeDisplay(bitmap: Bitmap) {
    val imageBitmap = bitmap.asImageBitmap()

    Image(
        bitmap = imageBitmap,
        contentDescription = "QR Code"
    )
}