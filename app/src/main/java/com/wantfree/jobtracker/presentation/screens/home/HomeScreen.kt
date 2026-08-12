package com.wantfree.jobtracker.presentation.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wantfree.jobtracker.data.model.job.CollectedJobResponse
import com.wantfree.jobtracker.data.model.job.JobPostingResponse
import com.wantfree.jobtracker.presentation.screens.common.STATUSES
import com.wantfree.jobtracker.presentation.screens.common.SourceBadge
import com.wantfree.jobtracker.presentation.screens.common.StatusBadge
import com.wantfree.jobtracker.presentation.screens.common.StatusMeta
import com.wantfree.jobtracker.presentation.screens.common.Toast
import kotlinx.coroutines.delay
import java.time.LocalDate

// 웹 프론트(index.css) 디자인 토큰 — LoginScreen과 동일
private val Indigo = Color(0xFF6366F1)
private val Purple = Color(0xFF8B5CF6)
private val TextDark = Color(0xFF0F172A)
private val TextGray = Color(0xFF64748B)
private val BorderGray = Color(0xFFE5E7EB)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val PageBackground = Color(0xFFF8FAFC)
private val ErrorRed = Color(0xFFDC2626)

private val BrandGradient = Brush.linearGradient(listOf(Indigo, Purple))

@Composable
fun HomeScreen(
    onNavigateToForm: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onLogout: () -> Unit,
    onInterview: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var showLogoutConfirm by remember { mutableStateOf(false) }

    // 스크랩 성공 토스트 3초 후 자동 제거 (LoginScreen과 동일 패턴)
    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(3000)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLogout()
    }

    // 수정/삭제 후 홈 복귀 시(popBackStack) 목록 새로고침
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Job Tracker",
                style = MaterialTheme.typography.headlineMedium.copy(
                    brush = BrandGradient,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onInterview) {
                Text("AI 면접 질문", color = Indigo, fontSize = 13.sp)
            }
            TextButton(onClick = viewModel::onOpenKeywords) {
                Text("관심 분야", color = Indigo, fontSize = 13.sp)
            }
            TextButton(onClick = { showLogoutConfirm = true }) {
                Text("로그아웃", color = TextGray, fontSize = 13.sp)
            }
        }

        TabRow(
            selectedTabIndex = if (state.tab == HomeTab.COLLECTED) 0 else 1,
            containerColor = PageBackground,
            contentColor = Indigo,
        ) {
            Tab(
                selected = state.tab == HomeTab.COLLECTED,
                onClick = { viewModel.onTabChange(HomeTab.COLLECTED) },
                text = { Text("전체 공고") },
            )
            Tab(
                selected = state.tab == HomeTab.MINE,
                onClick = { viewModel.onTabChange(HomeTab.MINE) },
                text = { Text("내 공고") },
            )
        }

        if (state.tab == HomeTab.MINE) {
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.keyword,
            onValueChange = viewModel::onKeywordChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            placeholder = { Text("회사명, 포지션 검색") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { viewModel.search() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Indigo,
                unfocusedBorderColor = BorderGray,
                cursorColor = Indigo,
                unfocusedPlaceholderColor = TextGray,
                focusedPlaceholderColor = TextGray,
            ),
        )

        Spacer(Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(STATUSES) { meta ->
                StatCard(meta = meta, count = state.stats[meta.key] ?: 0L)
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                FilterChip(
                    selected = state.selectedStatus == null,
                    onClick = { viewModel.onStatusSelected(null) },
                    label = { Text("전체") },
                )
            }
            items(STATUSES) { meta ->
                FilterChip(
                    selected = state.selectedStatus == meta.key,
                    onClick = { viewModel.onStatusSelected(meta.key) },
                    label = { Text(meta.label) },
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading && state.jobs.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Indigo,
                    )
                }
                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage!!,
                        color = ErrorRed,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp),
                    )
                }
                state.jobs.isEmpty() -> {
                    Text(
                        text = "등록된 공고가 없습니다",
                        color = TextGray,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp),
                    ) {
                        itemsIndexed(state.jobs) { index, job ->
                            JobRow(job = job, onClick = { onNavigateToDetail(job.id) })
                            if (index < state.jobs.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    thickness = 1.dp,
                                    color = BorderGray,
                                )
                            }
                        }
                    }
                }
            }
        }
        } else {
        Spacer(Modifier.height(12.dp))

        val keyboardController = LocalSoftwareKeyboardController.current
        val runSearch = {
            keyboardController?.hide()
            viewModel.applyKeywordFilter()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = state.collectedKeyword,
                onValueChange = viewModel::onCollectedKeywordChange,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                placeholder = { Text("키워드로 공고 찾기") },
                singleLine = true,
                minLines = 1,
                maxLines = 1,
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { runSearch() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Indigo,
                    unfocusedBorderColor = BorderGray,
                    cursorColor = Indigo,
                    unfocusedPlaceholderColor = TextGray,
                    focusedPlaceholderColor = TextGray,
                ),
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = runSearch,
                modifier = Modifier.width(64.dp),
                contentPadding = PaddingValues(horizontal = 0.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo),
            ) {
                Text("검색")
            }
        }

        Spacer(Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading && state.collectedJobs.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Indigo,
                    )
                }
                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage!!,
                        color = ErrorRed,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 32.dp),
                    )
                }
                state.collectedJobs.isEmpty() -> {
                    Text(
                        text = "수집된 공고가 없습니다",
                        color = TextGray,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp),
                    ) {
                        itemsIndexed(state.collectedJobs) { index, job ->
                            CollectedJobRow(job = job, onScrap = { viewModel.scrap(job.id) })
                            if (index < state.collectedJobs.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    thickness = 1.dp,
                                    color = BorderGray,
                                )
                            }
                        }
                    }
                }
            }
        }
        }
    }

        ExtendedFloatingActionButton(
            onClick = onNavigateToForm,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 32.dp),
            containerColor = Indigo,
            contentColor = Color.White,
            icon = { Icon(Icons.Filled.Add, contentDescription = null) },
            text = { Text("공고 추가") },
        )

        state.message?.let { message ->
            Toast(message = message, isError = false)
        }
    }

    if (state.showKeywordsDialog) {
        KeywordsDialog(
            currentKeywords = state.myKeywords,
            isSearching = state.isSearching,
            dialogMessage = state.dialogMessage,
            onSave = viewModel::autoSaveKeywords,
            onFind = viewModel::findJobsWithKeyword,
            onDismiss = viewModel::closeKeywordsDialog,
        )
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("로그아웃 하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirm = false
                    viewModel.logout()
                }) {
                    Text("확인", color = Indigo)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("취소", color = TextGray)
                }
            },
        )
    }
}

@Composable
private fun StatCard(meta: StatusMeta, count: Long) {
    Surface(
        modifier = Modifier.widthIn(min = 80.dp),
        shape = RoundedCornerShape(12.dp),
        color = SurfaceWhite,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = meta.emoji, fontSize = 20.sp)
            Spacer(Modifier.height(4.dp))
            Text(text = count.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(text = meta.label, fontSize = 11.sp, color = TextGray)
        }
    }
}

// 지역/경력/업종 + 마감일 중 값이 있는 것만 " · "로 join ("2026-08-31" → "마감 8/31")
private fun metaLine(region: String?, experience: String?, industry: String?, deadline: String? = null): String? {
    val parts = listOfNotNull(region, experience, industry).filter { it.isNotBlank() }
    val deadlinePart = deadline?.takeIf { it.isNotBlank() }?.split("-")
        ?.takeIf { it.size == 3 }
        ?.let { "마감 ${it[1].toInt()}/${it[2].toInt()}" }
    return listOfNotNull(parts.joinToString(" · ").ifEmpty { null }, deadlinePart)
        .joinToString(" · ")
        .ifEmpty { null }
}

@Composable
private fun JobRow(job: JobPostingResponse, onClick: () -> Unit) {
    val isPastDeadline = runCatching { job.deadline?.let { LocalDate.parse(it).isBefore(LocalDate.now()) } ?: false }
        .getOrDefault(false)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusBadge(status = job.status)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = job.companyName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(text = job.position, fontSize = 14.sp, color = TextGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            metaLine(job.region, job.experience, job.industry)?.let { meta ->
                Text(text = meta, fontSize = 12.sp, color = TextGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(start = 8.dp),
        ) {
            job.source?.let { source ->
                SourceBadge(source = source, fontSize = 11.sp)
            }
            if (!job.deadline.isNullOrBlank()) {
                if (job.source != null) Spacer(Modifier.height(4.dp))
                Text(
                    text = "~${job.deadline}",
                    fontSize = 13.sp,
                    color = if (isPastDeadline) ErrorRed else TextGray,
                )
            }
        }
    }
}

@Composable
private fun CollectedJobRow(job: CollectedJobResponse, onScrap: () -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = job.url.isNotBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = job.company, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = job.title, fontSize = 14.sp, color = TextGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            metaLine(job.region, job.experience, job.industry, job.deadline)?.let { meta ->
                Text(text = meta, fontSize = 12.sp, color = TextGray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (job.url.isNotBlank()) {
                Text(text = job.url, fontSize = 11.sp, color = Indigo, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(4.dp))
            SourceBadge(source = job.source)
        }
        Spacer(Modifier.width(12.dp))
        Button(
            onClick = onScrap,
            enabled = !job.scrapedByMe,
            colors = ButtonDefaults.buttonColors(
                containerColor = Indigo,
                disabledContainerColor = BorderGray,
                disabledContentColor = TextGray,
            ),
        ) {
            Text(if (job.scrapedByMe) "스크랩됨" else "스크랩")
        }
    }
}
