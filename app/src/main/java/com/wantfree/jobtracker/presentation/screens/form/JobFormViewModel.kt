package com.wantfree.jobtracker.presentation.screens.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wantfree.jobtracker.data.model.job.JobPostingRequest
import com.wantfree.jobtracker.data.model.job.JobPostingUpdateRequest
import com.wantfree.jobtracker.domain.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 공고 추가/수정 겸용 폼 상태. isEditMode == false 면 신규 등록 */
data class JobFormUiState(
    val isEditMode: Boolean = false,
    val companyName: String = "",
    val position: String = "",
    val link: String = "",
    val deadline: String? = null, // "YYYY-MM-DD"
    val status: String = "WISH",
    val memo: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saved: Boolean = false,
)

@HiltViewModel
class JobFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val jobRepository: JobRepository,
) : ViewModel() {

    private val jobId: Long? = savedStateHandle.get<String>("jobId")?.toLongOrNull()

    private val _uiState = MutableStateFlow(JobFormUiState(isEditMode = jobId != null))
    val uiState: StateFlow<JobFormUiState> = _uiState.asStateFlow()

    init {
        jobId?.let(::load)
    }

    fun onCompanyNameChange(value: String) = _uiState.update { it.copy(companyName = value) }
    fun onPositionChange(value: String) = _uiState.update { it.copy(position = value) }
    fun onLinkChange(value: String) = _uiState.update { it.copy(link = value) }
    fun onDeadlineChange(value: String?) = _uiState.update { it.copy(deadline = value) }
    fun onStatusChange(value: String) = _uiState.update { it.copy(status = value) }
    fun onMemoChange(value: String) = _uiState.update { it.copy(memo = value) }

    private fun load(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            jobRepository.getJob(id)
                .onSuccess { job ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            companyName = job.companyName,
                            position = job.position,
                            link = job.link,
                            deadline = job.deadline.ifBlank { null },
                            status = job.status,
                            memo = job.memo,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "공고를 불러오지 못했습니다")
                    }
                }
        }
    }

    fun submit() {
        val state = _uiState.value
        if (state.companyName.isBlank() || state.position.isBlank()) {
            _uiState.update { it.copy(errorMessage = "회사명과 포지션은 필수입니다") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val result = if (jobId != null) {
                jobRepository.updateJob(
                    jobId,
                    JobPostingUpdateRequest(
                        companyName = state.companyName,
                        position = state.position,
                        link = state.link.ifBlank { null },
                        deadline = state.deadline,
                        status = state.status,
                        memo = state.memo.ifBlank { null },
                    ),
                )
            } else {
                jobRepository.createJob(
                    JobPostingRequest(
                        companyName = state.companyName,
                        position = state.position,
                        link = state.link.ifBlank { null },
                        deadline = state.deadline,
                        status = state.status,
                        memo = state.memo.ifBlank { null },
                    ),
                )
            }

            result
                .onSuccess { _uiState.update { it.copy(isSaving = false, saved = true) } }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "저장에 실패했습니다") }
                }
        }
    }
}
