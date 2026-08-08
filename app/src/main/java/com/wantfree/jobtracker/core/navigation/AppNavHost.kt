package com.wantfree.jobtracker.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wantfree.jobtracker.presentation.screens.home.HomeScreen
import com.wantfree.jobtracker.presentation.screens.login.LoginScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true } // 로그인 화면 스택에서 제거
                    }
                }
            )
        }
        composable("home") { HomeScreen() }
    }
}
