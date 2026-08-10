package com.wantfree.jobtracker.presentation.screens.interview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wantfree.jobtracker.presentation.screens.common.Toast

// 웹 프론트(index.css) 디자인 토큰 — JobDetailScreen/HomeScreen과 동일
private val Indigo = Color(0xFF6366F1)
private val TextDark = Color(0xFF0F172A)
private val TextGray = Color(0xFF64748B)
private val PageBackground = Color(0xFFF8FAFC)
private val CardBackground = Color(0xFFF1F5F9)

@Composable
fun InterviewScreen(
    onBack: () -> Unit,
    viewModel: InterviewViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

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
            IconButton(onClick = onBack, modifier = Modifier.padding(start = 4.dp, end = 4.dp)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로")
            }
            Column {
                Text(
                    text = state.job?.companyName ?: "",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                )
                state.job?.position?.let {
                    Text(text = it, fontSize = 13.sp, color = TextGray)
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp),
                        color = Indigo,
                    )
                }
                state.questions.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
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
            }

            state.errorMessage?.let { message ->
                Toast(message = message, isError = true)
            }
        }
    }
}
