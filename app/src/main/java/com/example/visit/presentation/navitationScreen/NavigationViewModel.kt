package com.example.visit.presentation.navitationScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visit.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NavigationUiState(
    val isLoading: Boolean = true,
    val hasProfile: Boolean = false
)

@HiltViewModel
class NavigationViewModel @Inject constructor(
    profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NavigationUiState())
    val uiState: StateFlow<NavigationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = profileRepository.observeProfile().first()
            _uiState.value = NavigationUiState(
                isLoading = false,
                hasProfile = profile != null
            )
        }
    }
}