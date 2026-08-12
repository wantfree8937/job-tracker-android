package com.wantfree.jobtracker.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wantfree.jobtracker.data.model.job.CollectedJobResponse
import com.wantfree.jobtracker.data.model.job.JobPostingResponse
import com.wantfree.jobtracker.domain.repository.AuthRepository
import com.wantfree.jobtracker.domain.repository.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

/** 홈 화면 탭 — 내 공고 / 수집 공고 */
enum class HomeTab { MINE, COLLECTED }

/** 백엔드 키워드 규칙: 2~20자, 한글/영문/숫자/공백만 (400 방지용 클라이언트 선검증) */
private val KEYWORD_REGEX = Regex("^[가-힣a-zA-Z0-9\\s]+$")
private fun isValidKeyword(keyword: String) = keyword.length in 2..20 && KEYWORD_REGEX.matches(keyword)

/** 홈(공고 목록) 화면 상태. selectedStatus == null 이면 전체 */
data class HomeUiState(
    val tab: HomeTab = HomeTab.COLLECTED,
    val jobs: List<JobPostingResponse> = emptyList(),
    val stats: Map<String, Long> = emptyMap(),
    val selectedStatus: String? = null,
    val keyword: String = "",
    val collectedJobs: List<CollectedJobResponse> = emptyList(),
    val collectedKeyword: String = "",
    val sourceFilter: String = "ALL",
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val isCrawling: Boolean = false,
    val errorMessage: String? = null,
    val message: String? = null,
    val myKeywords: List<String> = emptyList(),
    val showKeywordsDialog: Boolean = false,
    val dialogMessage: String? = null,
    val loggedOut: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        if (_uiState.value.tab == HomeTab.COLLECTED) loadCollected() else load()
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

    /** 화면 재진입 시 현재 탭만 새로고침 (탭 전환 없음) */
    fun refresh() {
        if (_uiState.value.tab == HomeTab.COLLECTED) loadCollected() else load()
    }

    fun onCollectedKeywordChange(keyword: String) = _uiState.update { it.copy(collectedKeyword = keyword) }

    /** 검색창 키워드로 기존 수집 공고를 필터링만 한다 (크롤링 없음) */
    fun applyKeywordFilter() = loadCollected()

    fun onSourceFilterChange(filter: String) = _uiState.update { it.copy(sourceFilter = filter) }

    /** 관심 키워드 전체 크롤링 — "공고 불러오기" 버튼 전용 */
    fun crawl() {
        if (_uiState.value.isCrawling) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCrawling = true, errorMessage = null) }
            jobRepository.crawlCollected()
                .onSuccess { result ->
                    val message = if (result.loaded > 0) "${result.loaded}건 새로 가져왔어요" else "새 공고가 없어요"
                    _uiState.update { it.copy(isCrawling = false, message = message) }
                    loadCollected()
                }
                .onFailure { e ->
                    val message = if (e is HttpException && e.code() == 400) {
                        "관심 분야를 먼저 등록해주세요"
                    } else {
                        e.message ?: "공고 불러오기에 실패했습니다"
                    }
                    _uiState.update { it.copy(isCrawling = false, errorMessage = message) }
                }
        }
    }

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
                    _uiState.update { it.copy(collectedJobs = jobs, isLoading = false) }
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
                            collectedJobs = state.collectedJobs.map { if (it.id == jobId) it.copy(scrapedByMe = true) else it },
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

    /** 관심 분야 모달 열기 — 내 정보에서 현재 키워드를 불러온다 */
    fun onOpenKeywords() {
        viewModelScope.launch {
            authRepository.getMe()
                .onSuccess { user -> _uiState.update { it.copy(myKeywords = user.keywords, showKeywordsDialog = true, dialogMessage = null) } }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message ?: "내 정보를 불러오지 못했습니다") } }
        }
    }

    fun closeKeywordsDialog() = _uiState.update { it.copy(showKeywordsDialog = false, dialogMessage = null) }

    fun clearDialogMessage() = _uiState.update { it.copy(dialogMessage = null) }

    /** 관심 분야 자동 저장(다이얼로그 유지) — 저장만 한다. 공고 수집은 findJobsWithKeyword에서만 */
    fun autoSaveKeywords(selected: List<String>) {
        if (selected.any { !isValidKeyword(it) }) {
            _uiState.update { it.copy(errorMessage = "2~20자, 한글/영문/숫자만 가능해요") }
            return
        }
        viewModelScope.launch {
            authRepository.updateKeywords(selected)
                .onSuccess { user ->
                    _uiState.update { it.copy(myKeywords = user.keywords, dialogMessage = "관심 분야를 저장했어요") }
                }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message ?: "관심 분야 저장에 실패했습니다") } }
        }
    }

    /** 관심 분야 모달의 "키워드로 공고 찾기" — 미저장 키워드면 자동 저장 후 즉시 수집 */
    fun findJobsWithKeyword(keyword: String) {
        val trimmed = keyword.trim()
        if (trimmed.isBlank() || _uiState.value.isSearching) return
        if (!isValidKeyword(trimmed)) {
            _uiState.update { it.copy(errorMessage = "2~20자, 한글/영문/숫자만 가능해요") }
            return
        }
        viewModelScope.launch {
            if (trimmed !in _uiState.value.myKeywords) {
                authRepository.updateKeywords(_uiState.value.myKeywords + trimmed)
                    .onSuccess { user -> _uiState.update { it.copy(myKeywords = user.keywords) } }
            }
            _uiState.update { it.copy(isSearching = true, errorMessage = null) }
            jobRepository.searchCollectedJobs(trimmed)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(isSearching = false, message = "${result.collected}건을 가져왔어요", showKeywordsDialog = false)
                    }
                    loadCollected()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSearching = false, errorMessage = e.message ?: "공고 검색에 실패했습니다") }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(loggedOut = true) }
        }
    }

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
