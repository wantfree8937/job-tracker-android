package com.wantfree.jobtracker.presentation.screens.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wantfree.jobtracker.data.model.job.JobPostingResponse
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

private data class StatusMeta(val key: String, val emoji: String, val label: String)

private val STATUSES = listOf(
    StatusMeta("WISH", "📋", "지원 예정"),
    StatusMeta("APPLIED", "✉️", "지원함"),
    StatusMeta("INTERVIEW", "🎤", "면접"),
    StatusMeta("OFFER", "🎉", "합격"),
    StatusMeta("REJECTED", "❌", "불합격"),
)

private fun badgeColors(status: String): Pair<Color, Color> = when (status) {
    "WISH" -> Color(0xFFF1F5F9) to Color(0xFF475569)
    "APPLIED" -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
    "INTERVIEW" -> Color(0xFFFFEDD5) to Color(0xFFC2410C)
    "OFFER" -> Color(0xFFDCFCE7) to Color(0xFF15803D)
    "REJECTED" -> Color(0xFFFEE2E2) to Color(0xFFB91C1C)
    else -> Color(0xFFF1F5F9) to Color(0xFF475569)
}

private fun statusLabel(status: String): String = STATUSES.find { it.key == status }?.label ?: status

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground),
    ) {
        Text(
            text = "Job Tracker",
            style = MaterialTheme.typography.headlineMedium.copy(
                brush = BrandGradient,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        )

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
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.jobs) { job -> JobRow(job = job) }
                    }
                }
            }
        }
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

@Composable
private fun StatusBadge(status: String) {
    val (bg, fg) = badgeColors(status)
    Surface(shape = RoundedCornerShape(6.dp), color = bg) {
        Text(
            text = statusLabel(status),
            color = fg,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun JobRow(job: JobPostingResponse) {
    val isPastDeadline = runCatching { LocalDate.parse(job.deadline).isBefore(LocalDate.now()) }
        .getOrDefault(false)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusBadge(status = job.status)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = job.companyName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(text = job.position, fontSize = 14.sp, color = TextGray)
        }
        Text(
            text = "~${job.deadline}",
            fontSize = 13.sp,
            color = if (isPastDeadline) ErrorRed else TextGray,
        )
    }
}
