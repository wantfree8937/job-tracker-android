package com.wantfree.jobtracker.presentation.screens.common

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ErrorRed = Color(0xFFDC2626)
private val SuccessGreen = Color(0xFF15803D)

/** 상단 오버레이 토스트 (레이아웃을 밀지 않음) — LoginScreen/HomeScreen 공용 */
@Composable
fun BoxScope.Toast(message: String, isError: Boolean) {
    Surface(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 48.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (isError) ErrorRed else SuccessGreen,
        shadowElevation = 6.dp,
    ) {
        Text(
            text = message,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        )
    }
}
