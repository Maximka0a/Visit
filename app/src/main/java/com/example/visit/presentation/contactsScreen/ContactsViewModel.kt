package com.example.visit.presentation.contactsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visit.domain.model.ScannedContact
import com.example.visit.domain.repository.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactsUiState(
    val contacts: List<ScannedContact> = emptyList()
)

@HiltViewModel
class ContactsViewModel @Inject constructor(
    contactsRepository: ContactsRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            contactsRepository.observeContact().collect { contacts ->
                _uiState.value = _uiState.value.copy(contacts = contacts)
            }
        }
    }
}