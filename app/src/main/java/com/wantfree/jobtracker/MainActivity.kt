package com.wantfree.jobtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.wantfree.jobtracker.core.navigation.AppNavHost
import com.wantfree.jobtracker.data.local.TokenManager
import com.wantfree.jobtracker.presentation.theme.JobTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 전체 화면 (immersive) — 상태바/네비게이션바 숨김, 스와이프 시 잠깐 표시
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // 저장된 토큰이 있으면 자동 로그인 (home으로 바로 진입)
        val token = runBlocking { tokenManager.accessToken.first() }

        setContent {
            JobTrackerTheme {
                AppNavHost(startDestination = if (token != null) "home" else "login")
            }
        }
    }
}
