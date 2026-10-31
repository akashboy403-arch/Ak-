package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.NezaiRepository
import com.example.ui.screens.MainNavigation
import com.example.ui.theme.NezaiTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.NezaiViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NezaiTheme {
                val authViewModel: AuthViewModel = viewModel()
                
                // Factory for NezaiViewModel with Repository injection
                val nezaiViewModel: NezaiViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer {
                            val app = checkNotNull(this[APPLICATION_KEY])
                            val repository = NezaiRepository(app)
                            NezaiViewModel(repository)
                        }
                    }
                )

                MainNavigation(authViewModel, nezaiViewModel)
            }
        }
    }
}
