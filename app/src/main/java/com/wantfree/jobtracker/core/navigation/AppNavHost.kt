package com.wantfree.jobtracker.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wantfree.jobtracker.presentation.screens.detail.JobDetailScreen
import com.wantfree.jobtracker.presentation.screens.form.JobFormScreen
import com.wantfree.jobtracker.presentation.screens.home.HomeScreen
import com.wantfree.jobtracker.presentation.screens.login.LoginScreen

@Composable
fun AppNavHost(startDestination: String = "login") {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true } // 로그인 화면 스택에서 제거
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToForm = { navController.navigate("job_form") },
                onNavigateToDetail = { jobId -> navController.navigate("job_detail/$jobId") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true } // 전체 스택 제거
                    }
                },
            )
        }
        composable(
            route = "job_detail/{jobId}",
            arguments = listOf(navArgument("jobId") { type = NavType.LongType }),
        ) {
            JobDetailScreen(
                onBack = { navController.popBackStack() },
                onNavigateToEdit = { jobId -> navController.navigate("job_form?jobId=$jobId") },
                onDeleted = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
            )
        }
        composable(
            route = "job_form?jobId={jobId}",
            arguments = listOf(navArgument("jobId") { type = NavType.StringType; nullable = true }),
        ) {
            JobFormScreen(
                onSaved = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }
    }
}
