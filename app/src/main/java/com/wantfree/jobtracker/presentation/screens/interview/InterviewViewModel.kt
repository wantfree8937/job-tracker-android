package com.wantfree.jobtracker.presentation.screens.interview

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
    val myJobs: List<JobPostingResponse> = emptyList(),
    val selectedJob: JobPostingResponse? = null,
    val topic: String = "MIXED",
    val difficulty: String = "ENTRY",
    val questions: List<String> = emptyList(),
    val usedResume: Boolean? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class InterviewViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val aiRepository: AiRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InterviewUiState())
    val uiState: StateFlow<InterviewUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            jobRepository.getJobs(status = null, keyword = null)
                .onSuccess { jobs -> _uiState.update { it.copy(myJobs = jobs) } }
        }
    }

    fun onSelectJob(job: JobPostingResponse?) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(selectedJob = job) }
    }

    fun onTopicChange(topic: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(topic = topic) }
    }

    fun onDifficultyChange(difficulty: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(difficulty = difficulty) }
    }

    fun generateQuestions() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null, questions = emptyList(), usedResume = null)
            }
            val state = _uiState.value
            aiRepository.getInterviewQuestions(state.selectedJob, state.topic, state.difficulty)
                .onSuccess { res ->
                    _uiState.update {
                        it.copy(isLoading = false, questions = res.questions, usedResume = res.usedResume)
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "질문 생성에 실패했습니다")
                    }
                }
        }
    }
}
