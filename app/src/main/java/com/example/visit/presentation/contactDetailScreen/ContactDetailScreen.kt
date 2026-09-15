package com.example.visit.presentation.contactDetailScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visit.domain.model.ScannedContact
import com.example.visit.presentation.contactsScreen.convertLongToTime
import com.example.visit.presentation.myCardScreen.BusinessCardCanvas
import com.example.visit.presentation.myCardScreen.CardThemes
import com.example.visit.ui.theme.Radius
import com.example.visit.ui.theme.Spacing

@Composable
fun ContactDetailRoute(
    onBack: () -> Unit,
    viewModel: ContactDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ContactDetailScreen(
        contact = uiState.contact,
        noteText = uiState.noteText,
        onNoteChanged = viewModel::onNoteChanged,
        onBack = onBack,
        onDelete = { viewModel.onDeleteClicked(onDeleted = onBack) }
    )
}

@Composable
fun ContactDetailScreen(
    contact: ScannedContact?,
    noteText: String,
    onNoteChanged: (String) -> Unit,
    onBack: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            Text(
                text = contact?.profile?.name ?: "",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить контакт",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        if (contact != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.xl)
            ) {
                BusinessCardCanvas(
                    name = contact.profile.name,
                    title = contact.profile.title ?: "",
                    tags = contact.profile.tags,
                    qrBitmap = null,
                    onQrIconClicked = {},
                    theme = CardThemes.findById(contact.profile.themeId),
                )

                Text(
                    text = "Отсканировано ${convertLongToTime(contact.scannedAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                if (contact.profile.socialLinks.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        Text(
                            text = "ССЫЛКИ",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        contact.profile.socialLinks.entries.forEach { entry ->
                            ReadOnlyLinkRow(name = entry.key, link = entry.value)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Text(
                        text = "ЗАМЕТКА",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = onNoteChanged,
                        placeholder = { Text("Где и при каких обстоятельствах вы познакомились…") },
                        shape = RoundedCornerShape(Radius.sm + 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyLinkRow(name: String, link: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(Radius.sm + 4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(2).uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "$name: $link",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
