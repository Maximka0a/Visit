package com.example.visit.presentation.myCardScreen


import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visit.domain.model.Profile
import com.example.visit.domain.repository.ProfileRepository
import com.example.visit.domain.usecase.GenerateQrUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyCardUiState(
    val profile: Profile? = null,
    val qrBitmap: Bitmap? = null,

    val isQRSheetOpen: Boolean = false
)

@HiltViewModel
class CardViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val useCase: GenerateQrUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(MyCardUiState())
    val uiState: StateFlow<MyCardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            profileRepository.observeProfile().collect { profile ->
                if (profile != null){
                    _uiState.value = MyCardUiState(
                        profile = profile,
                        qrBitmap = useCase.invoke(profile)
                    )
                }
            }
        }
    }
    fun onQrIconClicked(){
        _uiState.value = _uiState.value.copy(isQRSheetOpen = true)
    }
    fun onQrSheetDismissed(){
        _uiState.value = _uiState.value.copy(isQRSheetOpen = false)
    }
    fun onThemeChange(newTheme: Int){
        viewModelScope.launch {
            _uiState.value.profile?.let {
                profileRepository.saveProfile(
                    profile = it.copy(themeId = newTheme)
                )
            }
        }
    }
}