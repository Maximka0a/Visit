package com.example.visit.presentation.editProfileScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visit.ui.theme.Radius
import com.example.visit.ui.theme.Spacing


@Composable
fun EditProfileRoute(
    onNavigateToCard: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditProfileScreen(
        uiState = uiState,
        onNameChanged = viewModel::onNameChanged,
        onTitleChanged = viewModel::onTitleChanged,
        onThemeChange = viewModel::onThemeChange,
        onTagAdded = viewModel::onTagAdded,
        onTagRemove = viewModel::onTagRemove,
        onSocialLinkAdded = viewModel::onSocialLinkAdded,
        onSocialLinkRemoved = viewModel::onSocialLinkRemoved,
        onSave = { viewModel.saveProfile(onNavigateToCard)},

    )
}

@Composable
fun EditProfileScreen(
    uiState: EditProfileUiState,
    onNameChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit,
    onThemeChange: (Int) -> Unit,
    onTagAdded: (String) -> Unit,
    onTagRemove: (String) -> Unit,
    onSocialLinkAdded: (String, String) -> Unit,
    onSocialLinkRemoved: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Редактировать визитку",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Spacing.sm)
            )
            TextButton(onClick = onSave) {
                Text(
                    text = "Сохранить",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            FormField(label = "Имя*") {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChanged,
                    isError = uiState.nameError != null,
                    supportingText = {
                        uiState.nameError?.let { Text(it) }
                    },
                    shape = RoundedCornerShape(Radius.sm + 4.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            FormField(label = "Должность / статус") {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = onTitleChanged,
                    placeholder = { Text("Например, Product Designer") },
                    shape = RoundedCornerShape(Radius.sm + 4.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                SectionHeader(title = "Интересы", counter = "${uiState.tags.size} / 3")

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    uiState.tags.forEach { tag ->
                        RemovableChip(text = tag, onRemove = { onTagRemove(tag) })
                    }
                }

                var tagName by remember { mutableStateOf("") }
                AddRow(
                    value = tagName,
                    onValueChange = { tagName = it },
                    placeholder = "Добавить тег…",
                    onAdd = {
                        onTagAdded(tagName)
                        tagName = ""
                    }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                SectionHeader(title = "Ссылки")

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    uiState.socialLinks.entries.forEach { entry ->
                        SocialLinkRow(
                            name = entry.key,
                            link = entry.value,
                            onRemove = { onSocialLinkRemoved(entry.key) }
                        )
                    }
                }

                var socialName by remember { mutableStateOf("") }
                var socialLink by remember { mutableStateOf("") }
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    OutlinedTextField(
                        value = socialName,
                        onValueChange = { socialName = it },
                        placeholder = { Text("Соцсеть") },
                        shape = RoundedCornerShape(Radius.sm + 4.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = socialLink,
                        onValueChange = { socialLink = it },
                        placeholder = { Text("Ссылка") },
                        shape = RoundedCornerShape(Radius.sm + 4.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
                AddButton(
                    onClick = {
                        onSocialLinkAdded(socialName, socialLink)
                        socialName = ""
                        socialLink = ""
                    },
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun FormField(label: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}

@Composable
private fun SectionHeader(title: String, counter: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (counter != null) {
            Text(
                text = counter,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RemovableChip(text: String, onRemove: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(Radius.full))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(start = Spacing.md, end = Spacing.xs, top = Spacing.xs, bottom = Spacing.xs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        IconButton(onClick = onRemove, modifier = Modifier.size(20.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Удалить $text",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AddRow(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onAdd: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(Radius.full))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(Radius.full))
                .padding(horizontal = Spacing.md),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        AddButton(onClick = onAdd)
    }
}

@Composable
private fun AddButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Добавить",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun SocialLinkRow(name: String, link: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(Radius.sm + 4.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(Radius.sm + 4.dp))
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
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Удалить $name",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
