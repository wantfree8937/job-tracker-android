package com.wantfree.jobtracker.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp

private const val MAX_KEYWORDS = 10

private val Indigo = Color(0xFF6366F1)
private val BorderGray = Color(0xFFE5E7EB)
private val TextGray = Color(0xFF64748B)

@Composable
fun KeywordsDialog(
    currentKeywords: List<String>,
    onSave: (List<String>) -> Unit,
    onFind: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var selected by remember { mutableStateOf(currentKeywords) }
    var customInput by remember { mutableStateOf("") }

    fun remove(keyword: String) {
        selected = selected - keyword
        onSave(selected)
    }

    fun addCustom() {
        val keyword = customInput.trim()
        if (keyword.isNotEmpty() && keyword !in selected && selected.size < MAX_KEYWORDS) {
            selected = selected + keyword
            onSave(selected)
        }
        customInput = ""
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
                                onClick = { remove(keyword) },
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
                        onValueChange = { customInput = it },
                        modifier = Modifier.weight(1f).height(56.dp),
                        placeholder = { Text("직접 입력") },
                        singleLine = true,
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
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo),
                    ) {
                        Text("추가")
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onFind(customInput.trim().ifEmpty { selected.lastOrNull().orEmpty() }) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo),
                ) {
                    Text("키워드로 공고 찾기")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기", color = TextGray)
            }
        },
    )
}
