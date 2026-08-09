package com.wantfree.jobtracker.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wantfree.jobtracker.data.model.job.JobPostingResponse
import com.wantfree.jobtracker.domain.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 홈(공고 목록) 화면 상태. selectedStatus == null 이면 전체 */
data class HomeUiState(
    val jobs: List<JobPostingResponse> = emptyList(),
    val stats: Map<String, Long> = emptyMap(),
    val selectedStatus: String? = null,
    val keyword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val jobRepository: JobRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun onStatusSelected(status: String?) {
        _uiState.update { it.copy(selectedStatus = status) }
        load()
    }

    fun onKeywordChange(keyword: String) = _uiState.update { it.copy(keyword = keyword) }

    fun search() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val state = _uiState.value

            jobRepository.getJobs(status = state.selectedStatus, keyword = state.keyword.ifBlank { null })
                .onSuccess { jobs -> _uiState.update { it.copy(jobs = jobs, isLoading = false) } }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "공고 목록을 불러오지 못했습니다")
                    }
                }

            jobRepository.getStats()
                .onSuccess { stats -> _uiState.update { it.copy(stats = stats) } }
        }
    }
}
