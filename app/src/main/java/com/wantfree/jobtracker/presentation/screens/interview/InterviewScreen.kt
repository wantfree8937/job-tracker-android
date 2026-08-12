package com.wantfree.jobtracker.presentation.screens.interview

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wantfree.jobtracker.data.model.job.JobPostingResponse
import com.wantfree.jobtracker.presentation.screens.common.Toast

// 웹 프론트(index.css) 디자인 토큰 — JobDetailScreen/HomeScreen과 동일
private val Indigo = Color(0xFF6366F1)
private val TextDark = Color(0xFF0F172A)
private val TextGray = Color(0xFF64748B)
private val BorderGray = Color(0xFFE5E7EB)
private val PageBackground = Color(0xFFF8FAFC)
private val CardBackground = Color(0xFFF1F5F9)

private val TOPICS = listOf("TECHNICAL" to "기술 질문 위주", "MOTIVE" to "지원동기·인성 위주", "MIXED" to "혼합")
private val DIFFICULTIES = listOf("ENTRY" to "신입", "EASY" to "쉬움", "NORMAL" to "보통", "HARD" to "어려움")

@Composable
fun InterviewScreen(
    onBack: () -> Unit,
    viewModel: InterviewViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    // 로딩 중에는 뒤로가기를 막는다 (질문 생성 중 이탈 방지)
    BackHandler { if (!state.isLoading) onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { if (!state.isLoading) onBack() },
                modifier = Modifier.padding(start = 4.dp, end = 4.dp),
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로")
            }
            Text(
                text = "AI 면접 질문 연습",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column {
                    Text(text = "면접볼 공고 (선택)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    Spacer(Modifier.height(8.dp))
                    JobSelector(
                        jobs = state.myJobs,
                        selectedJob = state.selectedJob,
                        enabled = !state.isLoading,
                        onSelect = viewModel::onSelectJob,
                    )
                }
            }

            item {
                Column {
                    Text(text = "질문 유형", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(TOPICS) { (value, label) ->
                            FilterChip(
                                selected = state.topic == value,
                                onClick = { viewModel.onTopicChange(value) },
                                enabled = !state.isLoading,
                                label = { Text(label) },
                            )
                        }
                    }
                }
            }

            item {
                Column {
                    Text(text = "난이도", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(DIFFICULTIES) { (value, label) ->
                            FilterChip(
                                selected = state.difficulty == value,
                                onClick = { viewModel.onDifficultyChange(value) },
                                enabled = !state.isLoading,
                                label = { Text(label) },
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = viewModel::generateQuestions,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo),
                ) {
                    Text(if (state.isLoading) "생성 중..." else "시작하기")
                }
            }

            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Indigo)
                    }
                }
            }

            if (state.questions.isNotEmpty()) {
                state.usedResume?.let { usedResume ->
                    item {
                        Text(
                            text = if (usedResume) {
                                "📄 이력서를 반영해 질문을 만들었어요"
                            } else {
                                "ℹ️ 이력서가 반영되지 않았어요 (공고와 무관하거나 이력서가 없음)"
                            },
                            fontSize = 13.sp,
                            color = TextGray,
                        )
                    }
                }

                itemsIndexed(state.questions) { index, question ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = CardBackground,
                    ) {
                        Text(
                            text = "Q${index + 1}. $question",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 15.sp,
                            color = TextDark,
                        )
                    }
                }
            }
        }

        state.errorMessage?.let { message ->
            Toast(message = message, isError = true)
        }
        }
    }
}

@Composable
private fun JobSelector(
    jobs: List<JobPostingResponse>,
    selectedJob: JobPostingResponse?,
    enabled: Boolean,
    onSelect: (JobPostingResponse?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(
                text = selectedJob?.let { "${it.companyName} · ${it.position}" } ?: "공고를 선택하세요",
                modifier = Modifier.fillMaxWidth(),
                color = if (selectedJob != null) TextDark else TextGray,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("공고를 선택하세요") },
                onClick = { onSelect(null); expanded = false },
            )
            jobs.forEach { job ->
                DropdownMenuItem(
                    text = { Text("${job.companyName} · ${job.position}") },
                    onClick = { onSelect(job); expanded = false },
                )
            }
        }
    }
}
