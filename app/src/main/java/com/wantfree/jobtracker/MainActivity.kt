package com.wantfree.jobtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wantfree.jobtracker.presentation.navigation.AppNavHost
import com.wantfree.jobtracker.presentation.theme.JobTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JobTrackerTheme {
                AppNavHost()
            }
        }
    }
}
