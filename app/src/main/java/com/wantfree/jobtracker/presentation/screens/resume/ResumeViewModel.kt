package com.wantfree.jobtracker.presentation.screens.resume

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wantfree.jobtracker.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

const val PROFILE_TEXT_MAX_LENGTH = 5000

enum class ResumeTab { TEXT, FILE }

data class ResumeUiState(
    val profileText: String = "",
    val savedFileName: String? = null,
    val activeTab: ResumeTab = ResumeTab.TEXT,
    val isLoading: Boolean = true,
    val isBusy: Boolean = false, // 저장/업로드/삭제 중 — 이탈 방지
    val message: String? = null,
    val isError: Boolean = false,
)

@HiltViewModel
class ResumeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResumeUiState())
    val uiState: StateFlow<ResumeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val profileResult = profileRepository.getProfile()
            val fileResult = profileRepository.getFileInfo()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    profileText = profileResult.getOrNull().orEmpty(),
                    savedFileName = fileResult.getOrNull()?.fileName,
                )
            }
        }
    }

    fun onTabChange(tab: ResumeTab) {
        if (_uiState.value.isBusy) return
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun onTextChange(text: String) {
        _uiState.update { it.copy(profileText = text.take(PROFILE_TEXT_MAX_LENGTH)) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun save() {
        if (_uiState.value.isBusy) return
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true) }
            profileRepository.saveProfile(_uiState.value.profileText)
                .onSuccess {
                    _uiState.update { it.copy(isBusy = false, message = "저장되었어요", isError = false) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isBusy = false, message = e.message ?: "저장에 실패했어요", isError = true)
                    }
                }
        }
    }

    fun upload(bytes: ByteArray, fileName: String, mimeType: String) {
        if (_uiState.value.isBusy) return
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true) }
            profileRepository.uploadFile(bytes, fileName, mimeType)
                .onSuccess { res ->
                    _uiState.update {
                        it.copy(isBusy = false, savedFileName = res.fileName, message = "파일이 저장되었어요", isError = false)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isBusy = false, message = e.message ?: "업로드에 실패했어요", isError = true)
                    }
                }
        }
    }

    fun deleteFile() {
        if (_uiState.value.isBusy) return
        viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true) }
            profileRepository.deleteFile()
                .onSuccess {
                    _uiState.update {
                        it.copy(isBusy = false, savedFileName = null, message = "파일이 삭제되었어요", isError = false)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isBusy = false, message = e.message ?: "삭제에 실패했어요", isError = true)
                    }
                }
        }
    }
}
