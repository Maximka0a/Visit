package com.example.visit.presentation.scannerScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visit.domain.model.Profile
import com.example.visit.domain.model.ScannedContact
import com.example.visit.domain.repository.ContactsRepository
import com.example.visit.domain.repository.ProfileRepository
import com.example.visit.domain.usecase.FindCommonTagsUseCase
import com.example.visit.domain.usecase.ParseQrUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScannerUiState(
    val scannedProfile: Profile? = null,
    val error: String? = null,
    val commonTags: List<String>  = emptyList()
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val parseQrUseCase: ParseQrUseCase,
    private val  profileRepository: ProfileRepository,
    private val  findCommonTagsUseCase: FindCommonTagsUseCase,
    private val  contactsRepository: ContactsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    fun onSaveContactClicked() {
        val scannedProfile = _uiState.value.scannedProfile ?: return

        val scannedContact = ScannedContact(
            id = 0,
            profile = scannedProfile,
            scannedAt = System.currentTimeMillis(),
            note = null
        )

        viewModelScope.launch {
            contactsRepository.saveContact(scannedContact)
            _uiState.value = ScannerUiState()
        }
    }

    fun onDismissScannedProfile() {
        _uiState.value = _uiState.value.copy(scannedProfile = null)
    }
    fun onQrCodeScanned(rawText: String) {
        val result = parseQrUseCase.invoke(rawText)

        result
            .onSuccess { profile ->
                _uiState.value  = _uiState.value.copy(scannedProfile = profile)

                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(
                        commonTags =                 findCommonTagsUseCase.invoke(
                            profileRepository.observeProfile().first()?.tags ?: emptyList(),
                            otherTags = profile.tags,
                        )
                    )
                }
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(error = "QR-код не распознан. Убедитесь, что это визитка из приложения.")
            }
    }
}