package com.wantfree.jobtracker.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp

private const val MAX_KEYWORDS = 10
private const val KEYWORD_ERROR = "키워드는 2~20자, 한글/영문/숫자만 가능해요"
private val KEYWORD_REGEX = Regex("^[가-힣a-zA-Z0-9\\s]+$")

private val Indigo = Color(0xFF6366F1)
private val BorderGray = Color(0xFFE5E7EB)
private val TextGray = Color(0xFF64748B)

private fun isValidKeyword(keyword: String) =
    keyword.length in 2..20 && KEYWORD_REGEX.matches(keyword)

@Composable
fun KeywordsDialog(
    currentKeywords: List<String>,
    isSearching: Boolean,
    onSave: (List<String>) -> Unit,
    onFind: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var selected by remember { mutableStateOf(currentKeywords) }
    var customInput by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun remove(keyword: String) {
        selected = selected - keyword
        onSave(selected)
    }

    fun addCustom() {
        val keyword = customInput.trim()
        if (keyword.isEmpty()) return
        if (!isValidKeyword(keyword)) {
            inputError = KEYWORD_ERROR
            return
        }
        if (keyword !in selected && selected.size < MAX_KEYWORDS) {
            selected = selected + keyword
            onSave(selected)
        }
        customInput = ""
        inputError = null
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("관심 분야 설정") },
        text = {
            Column {
                if (selected.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(selected) { keyword ->
                            InputChip(
                                selected = false,
                                onClick = { if (!isSearching) remove(keyword) },
                                label = { Text(keyword) },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, contentDescription = "$keyword 삭제")
                                },
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    OutlinedTextField(
                        value = customInput,
                        onValueChange = { customInput = it; inputError = null },
                        modifier = Modifier.weight(1f),
                        enabled = !isSearching,
                        placeholder = { Text("직접 입력") },
                        singleLine = true,
                        isError = inputError != null,
                        supportingText = inputError?.let { error -> { Text(error) } },
                        shape = RoundedCornerShape(10.dp),
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
                        onClick = { addCustom() },
                        enabled = !isSearching,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo),
                    ) {
                        Text("추가")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSearching) {
                Text("닫기", color = TextGray)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val keyword = customInput.trim().ifEmpty { selected.lastOrNull().orEmpty() }
                    if (keyword.isBlank()) return@Button
                    if (!isValidKeyword(keyword)) {
                        inputError = KEYWORD_ERROR
                        return@Button
                    }
                    keyboardController?.hide()
                    onFind(keyword)
                },
                enabled = !isSearching,
                modifier = Modifier.width(140.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo),
            ) {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("키워드로 공고 찾기")
                }
            }
        },
    )
}
