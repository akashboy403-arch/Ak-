package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.EmergencyContact
import com.example.data.model.PanchayatMember
import com.example.ui.theme.NezaiGreen
import com.example.ui.viewmodel.NezaiUiState
import com.example.ui.viewmodel.NezaiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VillageInfoScreen(viewModel: NezaiViewModel, onBack: () -> Unit) {
    val infoState by viewModel.villageInfo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Village Information") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            when (infoState) {
                is NezaiUiState.Loading -> CircularProgressIndicator()
                is NezaiUiState.Success -> {
                    val info = (infoState as NezaiUiState.Success<com.example.data.model.VillageInfo>).data
                    LazyColumn {
                        item {
                            Text("Introduction", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(info.introduction)
                            Spacer(Modifier.height(16.dp))
                        }
                        item {
                            Text("History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(info.history)
                            Spacer(Modifier.height(16.dp))
                        }
                        item {
                            Text("Ward Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(info.ward_details)
                            Spacer(Modifier.height(16.dp))
                        }
                        item {
                            Text("Population", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Total Verified Population: ${info.population}")
                        }
                    }
                }
                is NezaiUiState.Error -> Text("Error loading info")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(viewModel: NezaiViewModel, onBack: () -> Unit) {
    val contactsState by viewModel.emergencyContacts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Important Contacts") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            when (contactsState) {
                is NezaiUiState.Loading -> CircularProgressIndicator()
                is NezaiUiState.Success -> {
                    val contacts = (contactsState as NezaiUiState.Success<List<EmergencyContact>>).data
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(contacts) { contact ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text(contact.name, fontWeight = FontWeight.Bold)
                                        Text(contact.category, style = MaterialTheme.typography.labelSmall)
                                    }
                                    Text(contact.number, color = NezaiGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                is NezaiUiState.Error -> Text("Error loading contacts")
            }
        }
    }
}
