package com.wantfree.jobtracker.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wantfree.jobtracker.data.model.job.CollectedJobResponse
import com.wantfree.jobtracker.data.model.job.JobPostingResponse
import com.wantfree.jobtracker.domain.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 홈 화면 탭 — 내 공고 / 수집 공고 */
enum class HomeTab { MINE, COLLECTED }

/** 홈(공고 목록) 화면 상태. selectedStatus == null 이면 전체 */
data class HomeUiState(
    val tab: HomeTab = HomeTab.MINE,
    val jobs: List<JobPostingResponse> = emptyList(),
    val stats: Map<String, Long> = emptyMap(),
    val selectedStatus: String? = null,
    val keyword: String = "",
    val collectedJobs: List<CollectedJobResponse> = emptyList(),
    val collectedKeyword: String = "",
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val errorMessage: String? = null,
    val message: String? = null,
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

    fun onTabChange(tab: HomeTab) {
        _uiState.update { it.copy(tab = tab) }
        if (tab == HomeTab.COLLECTED) {
            loadCollected()
        } else {
            load()
        }
    }

    fun onCollectedKeywordChange(keyword: String) = _uiState.update { it.copy(collectedKeyword = keyword) }

    /** 검색창 키워드로 기존 수집 공고를 필터링만 한다 (크롤링 없음) */
    fun applyKeywordFilter() = loadCollected()

    /** collect/search 크롤링 수집 — "가져오기" 버튼 전용 */
    fun collectJobs() {
        val keyword = _uiState.value.collectedKeyword.trim()
        if (keyword.isBlank() || _uiState.value.isSearching) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, errorMessage = null) }
            jobRepository.searchCollectedJobs(keyword)
                .onSuccess { result ->
                    val message = when {
                        result.collected > 0 -> "공고 ${result.collected}개 수집"
                        result.skipped > 0 -> "이미 수집된 공고 ${result.skipped}개 (새 공고 없음)"
                        else -> "검색 결과가 없습니다"
                    }
                    _uiState.update { it.copy(isSearching = false, message = message) }
                    loadCollected()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSearching = false, errorMessage = e.message ?: "공고 검색에 실패했습니다") }
                }
        }
    }

    fun loadCollected() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            jobRepository.getCollectedJobs(_uiState.value.collectedKeyword.trim().ifBlank { null })
                .onSuccess { jobs ->
                    _uiState.update { it.copy(collectedJobs = jobs.filterNot { job -> job.scrapedByMe }, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.message ?: "수집 공고를 불러오지 못했습니다")
                    }
                }
        }
    }

    fun scrap(jobId: Long) {
        viewModelScope.launch {
            jobRepository.scrapCollectedJob(jobId)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            collectedJobs = state.collectedJobs.filterNot { it.id == jobId },
                            message = "스크랩했습니다",
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message ?: "스크랩에 실패했습니다") }
                }
        }
    }

    fun clearMessage() = _uiState.update { it.copy(message = null) }

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
