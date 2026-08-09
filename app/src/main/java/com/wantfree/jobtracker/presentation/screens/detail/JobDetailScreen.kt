package com.wantfree.jobtracker.presentation.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wantfree.jobtracker.presentation.screens.common.StatusBadge
import java.time.LocalDate

// 웹 프론트(index.css) 디자인 토큰 — HomeScreen과 동일
private val Indigo = Color(0xFF6366F1)
private val TextDark = Color(0xFF0F172A)
private val TextGray = Color(0xFF64748B)
private val PageBackground = Color(0xFFF8FAFC)
private val ErrorRed = Color(0xFFDC2626)

@Composable
fun JobDetailScreen(
    onBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onDeleted: () -> Unit,
    viewModel: JobDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(state.deleted) {
        if (state.deleted) onDeleted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground),
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 24.dp, bottom = 4.dp),
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로")
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading && state.job == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        color = Indigo,
                    )
                }
                state.errorMessage != null && state.job == null -> {
                    Text(
                        text = state.errorMessage!!,
                        color = ErrorRed,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 32.dp, vertical = 24.dp),
                    )
                }
                state.job != null -> {
                    val job = state.job!!
                    val isPastDeadline = runCatching {
                        job.deadline?.let { LocalDate.parse(it).isBefore(LocalDate.now()) } ?: false
                    }.getOrDefault(false)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        StatusBadge(status = job.status)
                        Text(
                            text = job.companyName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                        )
                        Text(text = job.position, fontSize = 16.sp, color = TextGray)

                        listOfNotNull(job.region, job.experience, job.industry)
                            .filter { it.isNotBlank() }
                            .takeIf { it.isNotEmpty() }
                            ?.joinToString(" · ")
                            ?.let { meta ->
                                Text(text = meta, fontSize = 13.sp, color = TextGray)
                            }

                        if (!job.deadline.isNullOrBlank()) {
                            Text(
                                text = "마감일 ${job.deadline}",
                                fontSize = 14.sp,
                                color = if (isPastDeadline) ErrorRed else TextGray,
                            )
                        }

                        if (!job.link.isNullOrBlank()) {
                            val uriHandler = LocalUriHandler.current
                            Text(
                                text = job.link,
                                fontSize = 14.sp,
                                color = Indigo,
                                modifier = Modifier.clickable { uriHandler.openUri(job.link!!) },
                            )
                        }

                        if (!job.memo.isNullOrBlank()) {
                            Text(
                                text = "메모",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextDark,
                            )
                            Text(text = job.memo!!, fontSize = 14.sp, color = TextGray)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Indigo)
                                    .clickable { onNavigateToEdit(job.id) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "수정", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }

                            OutlinedButton(
                                onClick = { showDeleteConfirm = true },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                            ) {
                                Text(text = "삭제")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("공고 삭제") },
            text = { Text("이 공고를 삭제하시겠습니까? 되돌릴 수 없습니다.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.delete()
                }) {
                    Text("삭제", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("취소") }
            },
        )
    }
}
