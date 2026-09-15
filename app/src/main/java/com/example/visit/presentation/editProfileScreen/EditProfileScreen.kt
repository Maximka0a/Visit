package com.example.visit.presentation.editProfileScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChanged,
            label = { Text("Имя*") },
            isError = uiState.nameError != null,
            supportingText = {
                uiState.nameError?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChanged,
            label = { Text("Должность") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Теги (${uiState.tags.size}/3)")
        var tagName by remember { mutableStateOf("") }
        TextField(
            value = tagName,
            onValueChange = { tagName = it },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onTagAdded(tagName)
                tagName = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить")
        }

        LazyRow(

        ) {
            items(uiState.tags) { tag ->
                Card {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            start = 12.dp,
                            end = 4.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        )
                    ) {
                        Text(text = tag)
                        IconButton(onClick = { onTagRemove(tag) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove $tag"
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Ссылки")

        var socialName by remember { mutableStateOf("") }
        var socialLink by remember { mutableStateOf("") }
        TextField(
            value = socialName,
            onValueChange = { socialName = it },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = socialLink,
            onValueChange = { socialLink = it },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onSocialLinkAdded(socialName,socialLink)
                socialName = ""
                socialLink = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить")
        }

        LazyRow(

        ) {
            items(uiState.socialLinks.entries.toList()) { socialLink ->
                Card {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            start = 12.dp,
                            end = 4.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        )
                    ) {
                        Text(text = "${socialLink.key}: ${socialLink.value}")
                        IconButton(onClick = { onSocialLinkRemoved(socialLink.key) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove ${socialLink.key}"
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }
    }
}