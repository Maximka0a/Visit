package com.example.visit.presentation.contactDetailScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visit.domain.model.ScannedContact
import com.example.visit.domain.repository.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactDetailUiState(
    val contact: ScannedContact? = null,
    val noteText: String = "",
)

@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val contactsRepository: ContactsRepository,
) : ViewModel() {
    private val contactId: Long = checkNotNull(savedStateHandle["id"])

    private val _uiState = MutableStateFlow(ContactDetailUiState())
    val uiState: StateFlow<ContactDetailUiState> = _uiState.asStateFlow()

    private var noteInitialized = false

    init {
        viewModelScope.launch {
            contactsRepository.observeContact().collect { contacts ->
                val contact = contacts.firstOrNull { it.id == contactId } ?: return@collect

                _uiState.value = _uiState.value.copy(
                    contact = contact,
                    noteText = if (!noteInitialized) {
                        noteInitialized = true
                        contact.note ?: ""
                    } else {
                        _uiState.value.noteText
                    }
                )
            }
        }
    }

    fun onNoteChanged(text: String) {
        _uiState.value = _uiState.value.copy(noteText = text)
        viewModelScope.launch {
            contactsRepository.updateNote(contactId, text)
        }
    }

    fun onDeleteClicked(onDeleted: () -> Unit) {
        viewModelScope.launch {
            contactsRepository.deleteContact(contactId)
            onDeleted()
        }
    }
}
