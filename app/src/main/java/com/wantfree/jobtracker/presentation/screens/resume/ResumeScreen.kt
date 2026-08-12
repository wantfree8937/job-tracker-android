package com.wantfree.jobtracker.presentation.screens.resume

import android.provider.OpenableColumns
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wantfree.jobtracker.presentation.screens.common.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 웹 프론트(index.css) 디자인 토큰 — 다른 화면들과 동일
private val Indigo = Color(0xFF6366F1)
private val TextDark = Color(0xFF0F172A)
private val TextGray = Color(0xFF64748B)
private val BorderGray = Color(0xFFE5E7EB)
private val PageBackground = Color(0xFFF8FAFC)
private val CardBackground = Color(0xFFF1F5F9)

private val RESUME_MIME_TYPES = arrayOf(
    "application/pdf",
    "application/vnd.ms-powerpoint",
    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
)

@Composable
fun ResumeScreen(
    onBack: () -> Unit,
    viewModel: ResumeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 저장/업로드/삭제 중에는 뒤로가기를 막는다 (InterviewScreen과 동일 패턴)
    BackHandler { if (!state.isBusy) onBack() }

    LaunchedEffect(state.message) {
        if (state.message != null) {
            delay(3000)
            viewModel.clearMessage()
        }
    }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val resolver = context.contentResolver
            val fileName = resolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex >= 0) cursor.getString(nameIndex) else null
            } ?: "resume"
            val mimeType = resolver.getType(uri) ?: "application/pdf"
            val bytes = withContext(Dispatchers.IO) {
                resolver.openInputStream(uri)?.use { it.readBytes() }
            }
            if (bytes != null) viewModel.upload(bytes, fileName, mimeType)
        }
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
                    .padding(top = 24.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { if (!state.isBusy) onBack() },
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로")
                }
                Text(text = "내 이력서", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            }

            TabRow(
                selectedTabIndex = if (state.activeTab == ResumeTab.TEXT) 0 else 1,
                containerColor = PageBackground,
                contentColor = Indigo,
            ) {
                Tab(
                    selected = state.activeTab == ResumeTab.TEXT,
                    onClick = { viewModel.onTabChange(ResumeTab.TEXT) },
                    text = { Text("직접 입력") },
                )
                Tab(
                    selected = state.activeTab == ResumeTab.FILE,
                    onClick = { viewModel.onTabChange(ResumeTab.FILE) },
                    text = { Text("파일 업로드") },
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
            ) {
                    if (state.activeTab == ResumeTab.TEXT) {
                        OutlinedTextField(
                            value = state.profileText,
                            onValueChange = viewModel::onTextChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            enabled = !state.isBusy,
                            placeholder = { Text("경력, 프로젝트, 강점 등을 자유롭게 입력해주세요") },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Indigo,
                                unfocusedBorderColor = BorderGray,
                                cursorColor = Indigo,
                            ),
                        )
                        Text(
                            text = "${state.profileText.length} / $PROFILE_TEXT_MAX_LENGTH",
                            fontSize = 12.sp,
                            color = TextGray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 12.dp),
                        )
                        Button(
                            onClick = viewModel::save,
                            enabled = !state.isBusy,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo),
                        ) {
                            Text(if (state.isBusy) "저장 중..." else "저장")
                        }
                    } else {
                        val atMax = state.files.size >= PROFILE_FILE_MAX_COUNT
                        Button(
                            onClick = { filePicker.launch(RESUME_MIME_TYPES) },
                            enabled = !state.isBusy && !atMax,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo),
                        ) {
                            Icon(Icons.Filled.UploadFile, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(if (state.isBusy) "업로드 중..." else "파일 업로드 (PDF, PPT, PPTX)")
                        }
                        Text(
                            text = "${state.files.size} / $PROFILE_FILE_MAX_COUNT 개 저장됨",
                            fontSize = 12.sp,
                            color = TextGray,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        )

                        Spacer(Modifier.height(16.dp))

                        if (state.files.isEmpty()) {
                            Text(text = "저장된 파일이 없어요", fontSize = 14.sp, color = TextGray)
                        } else {
                            state.files.forEach { file ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = CardBackground,
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(Icons.Filled.Description, contentDescription = null, tint = Indigo)
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = file.fileName ?: "",
                                            modifier = Modifier.weight(1f),
                                            fontSize = 14.sp,
                                            color = TextDark,
                                        )
                                        OutlinedButton(
                                            onClick = { file.id?.let(viewModel::deleteFile) },
                                            enabled = !state.isBusy,
                                        ) {
                                            Icon(Icons.Filled.Close, contentDescription = "삭제", modifier = Modifier.height(16.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("삭제")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        }

        state.message?.let { message ->
            Toast(message = message, isError = state.isError)
        }
    }
}
