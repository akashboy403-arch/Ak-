package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Complaint
import com.example.data.model.GovtScheme
import com.example.ui.theme.NezaiGreen
import com.example.ui.theme.NezaiSaffron
import com.example.ui.viewmodel.NezaiUiState
import com.example.ui.viewmodel.NezaiViewModel

@Composable
fun SchemesScreen(viewModel: NezaiViewModel) {
    val schemesState by viewModel.govtSchemes.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Government Schemes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        when (schemesState) {
            is NezaiUiState.Loading -> CircularProgressIndicator()
            is NezaiUiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items((schemesState as NezaiUiState.Success<List<GovtScheme>>).data) { scheme ->
                        SchemeCard(scheme)
                    }
                }
            }
            is NezaiUiState.Error -> Text("Error loading schemes")
        }
    }
}

@Composable
fun SchemeCard(scheme: GovtScheme) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(scheme.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(scheme.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text("Eligibility: ${scheme.eligibility}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            TextButton(onClick = { /* Open link */ }) {
                Text("Apply Now", color = NezaiGreen)
            }
        }
    }
}

@Composable
fun ComplaintsScreen(viewModel: NezaiViewModel) {
    val complaintsState by viewModel.userComplaints.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("My Complaints", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            when (complaintsState) {
                is NezaiUiState.Loading -> CircularProgressIndicator()
                is NezaiUiState.Success -> {
                    val complaints = (complaintsState as NezaiUiState.Success<List<Complaint>>).data
                    if (complaints.isEmpty()) {
                        Text("No complaints submitted yet.")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(complaints) { complaint ->
                                ComplaintCard(complaint)
                            }
                        }
                    }
                }
                is NezaiUiState.Error -> Text("Error loading complaints")
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = NezaiSaffron
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Complaint")
        }
    }

    if (showDialog) {
        AddComplaintDialog(
            onDismiss = { showDialog = false },
            onSubmit = { title, desc, cat ->
                viewModel.submitComplaint(title, desc, cat)
                showDialog = false
            }
        )
    }
}

@Composable
fun ComplaintCard(complaint: Complaint) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(complaint.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Badge(containerColor = when(complaint.status) {
                    "resolved" -> NezaiGreen
                    "pending" -> Color.Gray
                    else -> NezaiSaffron
                }) {
                    Text(complaint.status.uppercase(), color = Color.White)
                }
            }
            Text("ID: ${complaint.complaintNumber}", style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(8.dp))
            Text(complaint.description, style = MaterialTheme.typography.bodyMedium)
            if (complaint.adminResponse != null) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Text("Admin Response:", fontWeight = FontWeight.Bold)
                Text(complaint.adminResponse, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun AddComplaintDialog(onDismiss: () -> Unit, onSubmit: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Complaint") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
                Spacer(Modifier.height(8.dp))
                // Simple dropdown simulation
                Text("Category: $category")
                Row {
                    listOf("Road", "Drainage", "Electricity", "Other").forEach { cat ->
                        FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(cat) })
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(title, desc, category) }) { Text("Submit") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
