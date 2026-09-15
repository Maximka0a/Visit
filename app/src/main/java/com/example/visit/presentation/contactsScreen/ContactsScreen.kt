package com.example.visit.presentation.contactsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun RouteContactsScreen(
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val  uiState:   ContactsUiState by viewModel.uiState.collectAsStateWithLifecycle()
    ContactsScreen(
        uiState
    )
}

@Composable
fun ContactsScreen(uiState: ContactsUiState) {
    if (uiState.contacts.isEmpty()){
        Text("Пока нет знакомств")
    }else{
        LazyColumn(

        ) {
            items(uiState.contacts){ contact ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(

                    ) {
                        Text(contact.profile.name)
                        Text(contact.profile.title?: "")
                        Text(convertLongToTime(contact.scannedAt))
                    }
                }
            }
        }
    }
}

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    return format.format(date)
}