package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Complaint
import com.example.ui.viewmodel.NezaiUiState
import com.example.ui.viewmodel.NezaiViewModel

@Composable
fun AdminScreen(viewModel: NezaiViewModel) {
    var selectedSubTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Admin Dashboard", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        TabRow(selectedTabIndex = selectedSubTab) {
            Tab(selected = selectedSubTab == 0, onClick = { selectedSubTab = 0 }) {
                Text("Complaints", modifier = Modifier.padding(16.dp))
            }
            Tab(selected = selectedSubTab == 1, onClick = { selectedSubTab = 1 }) {
                Text("Village Info", modifier = Modifier.padding(16.dp))
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        when (selectedSubTab) {
            0 -> AdminComplaintsList(viewModel)
            1 -> AdminVillageInfo(viewModel)
        }
    }
}

@Composable
fun AdminComplaintsList(viewModel: NezaiViewModel) {
    val allComplaintsState by viewModel.allComplaints.collectAsState()

    when (allComplaintsState) {
        is NezaiUiState.Loading -> CircularProgressIndicator()
        is NezaiUiState.Success -> {
            val complaints = (allComplaintsState as NezaiUiState.Success<List<Complaint>>).data
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(complaints) { complaint ->
                    AdminComplaintCard(complaint, viewModel)
                }
            }
        }
        is NezaiUiState.Error -> Text("Error loading complaints")
    }
}

@Composable
fun AdminComplaintCard(complaint: Complaint, viewModel: NezaiViewModel) {
    var showResponseDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(complaint.title, fontWeight = FontWeight.Bold)
            Text(complaint.description)
            Text("By User: ${complaint.userId}", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(8.dp))
            Row {
                Button(onClick = { viewModel.updateComplaintStatus(complaint.id, "resolved", "Resolved by admin.") }) {
                    Text("Resolve")
                }
                Spacer(Modifier.width(8.dp))
                OutlinedButton(onClick = { showResponseDialog = true }) {
                    Text("Response")
                }
            }
        }
    }

    if (showResponseDialog) {
        var responseText by remember { mutableStateOf(complaint.adminResponse ?: "") }
        AlertDialog(
            onDismissRequest = { showResponseDialog = false },
            title = { Text("Admin Response") },
            text = {
                OutlinedTextField(value = responseText, onValueChange = { responseText = it }, label = { Text("Response Text") })
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateComplaintStatus(complaint.id, complaint.status, responseText)
                    showResponseDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showResponseDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminVillageInfo(viewModel: NezaiViewModel) {
    val villageInfoState by viewModel.villageInfo.collectAsState()

    when (villageInfoState) {
        is NezaiUiState.Loading -> CircularProgressIndicator()
        is NezaiUiState.Success -> {
            val info = (villageInfoState as NezaiUiState.Success<com.example.data.model.VillageInfo>).data
            var intro by remember { mutableStateOf(info.introduction) }
            var history by remember { mutableStateOf(info.history) }

            Column {
                OutlinedTextField(
                    value = intro,
                    onValueChange = { intro = it },
                    label = { Text("Introduction") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = history,
                    onValueChange = { history = it },
                    label = { Text("History") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.updateVillageInfo(info.copy(introduction = intro, history = history)) }) {
                    Text("Update Village Information")
                }
            }
        }
        is NezaiUiState.Error -> Text("Error loading village info")
    }
}
