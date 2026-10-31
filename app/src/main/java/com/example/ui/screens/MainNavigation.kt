package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.NezaiViewModel

@Composable
fun MainNavigation(
    authViewModel: AuthViewModel,
    nezaiViewModel: NezaiViewModel
) {
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Authenticated -> {
            HomeScreen(authViewModel, nezaiViewModel)
        }
        is AuthState.Loading -> {
            LoadingScreen()
        }
        else -> {
            AuthScreen(authViewModel)
        }
    }
}

@Composable
fun LoadingScreen() {
    // Simple splash/loading screen
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.CircularProgressIndicator()
    }
}
