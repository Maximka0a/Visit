package com.example.visit.presentation.editProfileScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visit.domain.model.Profile
import com.example.visit.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val name: String = "",
    val title: String = "",
    val tags: List<String> = listOf(),
    val socialLinks: Map<String, String> = mapOf(),
    val themeId: Int = 0,

    val nameError: String? = null,
)



@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            profileRepository.observeProfile().collect { profile ->
                if (profile != null){
                    _uiState.value=
                        EditProfileUiState(
                            name = profile.name,
                            title = profile.title ?: "",
                            tags = profile.tags,
                            socialLinks = profile.socialLinks,
                            themeId = profile.themeId
                        )
                }
            }
        }
    }
    fun onNameChanged(newName: String){
            _uiState.value = _uiState.value.copy(name = newName)
        }
    fun onTitleChanged(newTitle: String){
        _uiState.value = _uiState.value.copy(title = newTitle)
    }
    fun onThemeChange(newTheme: Int){
        _uiState.value = _uiState.value.copy(themeId = newTheme)
    }
    fun onTagAdded(tag: String) {
        val trimmedTag = tag.trim()
        if (trimmedTag.isBlank()) return
        if (_uiState.value.tags.size >= 3) return
        if (_uiState.value.tags.contains(trimmedTag)) return

        _uiState.value = _uiState.value.copy(
            tags = _uiState.value.tags + trimmedTag
        )
    }
    fun onTagRemove(tag: String) {
        _uiState.value = _uiState.value.copy(
            tags = _uiState.value.tags.minus(tag)
        )
    }
    fun onSocialLinkAdded(social: String, link: String){
        val trimmedSocial = social.trim()
        val trimmedLink = link.trim()
        if (trimmedSocial.isBlank() || trimmedLink.isBlank()) return

        _uiState.value = _uiState.value.copy(
            socialLinks = _uiState.value.socialLinks.plus(trimmedSocial to trimmedLink)
        )
    }
    fun onSocialLinkRemoved(social: String){
        _uiState.value = _uiState.value.copy(
            socialLinks = _uiState.value.socialLinks.minus(social)
        )
    }
    fun saveProfile(onNavigateToCard:() ->Unit){
        viewModelScope.launch {
            if (_uiState.value.name.isNotBlank()){

                profileRepository.saveProfile(profile = Profile(
                    name = uiState.value.name,
                    title = uiState.value.title,
                    tags = uiState.value.tags,
                    socialLinks = uiState.value.socialLinks,
                    themeId = uiState.value.themeId
                )
                )
                onNavigateToCard()
            }else{
                _uiState.value = _uiState.value.copy(
                    nameError = "Имя не может быть пустым"
                )
            }
        }
    }
}
