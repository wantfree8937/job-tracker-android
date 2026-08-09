package com.wantfree.jobtracker.presentation.screens.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 공고 상태 메타 — 홈/상세/등록폼 화면이 공유 (웹 프론트 상태 뱃지와 동일)
data class StatusMeta(val key: String, val emoji: String, val label: String)

val STATUSES = listOf(
    StatusMeta("WISH", "📋", "지원 예정"),
    StatusMeta("APPLIED", "✉️", "지원함"),
    StatusMeta("INTERVIEW", "🎤", "면접"),
    StatusMeta("OFFER", "🎉", "합격"),
    StatusMeta("REJECTED", "❌", "불합격"),
)

fun badgeColors(status: String): Pair<Color, Color> = when (status) {
    "WISH" -> Color(0xFFF1F5F9) to Color(0xFF475569)
    "APPLIED" -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
    "INTERVIEW" -> Color(0xFFFFEDD5) to Color(0xFFC2410C)
    "OFFER" -> Color(0xFFDCFCE7) to Color(0xFF15803D)
    "REJECTED" -> Color(0xFFFEE2E2) to Color(0xFFB91C1C)
    else -> Color(0xFFF1F5F9) to Color(0xFF475569)
}

fun statusLabel(status: String): String = STATUSES.find { it.key == status }?.label ?: status

@Composable
fun StatusBadge(status: String) {
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
