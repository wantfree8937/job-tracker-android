package com.wantfree.jobtracker.presentation.screens.interview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wantfree.jobtracker.data.model.job.JobPostingResponse
import com.wantfree.jobtracker.domain.repository.AiRepository
import com.wantfree.jobtracker.domain.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InterviewUiState(
    val job: JobPostingResponse? = null,
    val questions: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class InterviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val jobRepository: JobRepository,
    private val aiRepository: AiRepository,
) : ViewModel() {

    private val jobId: Long = checkNotNull(savedStateHandle["jobId"])

    private val _uiState = MutableStateFlow(InterviewUiState())
    val uiState: StateFlow<InterviewUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            jobRepository.getJob(jobId)
                .onSuccess { job ->
                    _uiState.update { it.copy(job = job) }
                    aiRepository.getInterviewQuestions(job)
                        .onSuccess { questions -> _uiState.update { it.copy(isLoading = false, questions = questions) } }
                        .onFailure { e ->
                            _uiState.update {
                                it.copy(isLoading = false, errorMessage = e.message ?: "질문 생성에 실패했습니다")
                            }
                        }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "공고를 불러오지 못했습니다")
                    }
                }
        }
    }
}
