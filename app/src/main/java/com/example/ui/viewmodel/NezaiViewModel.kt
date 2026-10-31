package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.NezaiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface NezaiUiState<out T> {
    object Loading : NezaiUiState<Nothing>
    data class Success<T>(val data: T) : NezaiUiState<T>
    data class Error(val message: String) : NezaiUiState<Nothing>
}

class NezaiViewModel(private val repository: NezaiRepository) : ViewModel() {

    val villageInfo = repository.observeVillageInfo()
        .map { it?.let { NezaiUiState.Success(it) } ?: NezaiUiState.Error("No village info found") }
        .catch { emit(NezaiUiState.Error(it.message ?: "Error loading village info")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NezaiUiState.Loading)

    val panchayatMembers = repository.observePanchayatMembers()
        .map<List<PanchayatMember>, NezaiUiState<List<PanchayatMember>>> { NezaiUiState.Success(it) }
        .catch { emit(NezaiUiState.Error(it.message ?: "Error loading members")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NezaiUiState.Loading)

    val govtSchemes = repository.observeGovtSchemes()
        .map<List<GovtScheme>, NezaiUiState<List<GovtScheme>>> { NezaiUiState.Success(it) }
        .catch { emit(NezaiUiState.Error(it.message ?: "Error loading schemes")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NezaiUiState.Loading)

    val news = repository.observeNews()
        .map<List<NewsItem>, NezaiUiState<List<NewsItem>>> { NezaiUiState.Success(it) }
        .catch { emit(NezaiUiState.Error(it.message ?: "Error loading news")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NezaiUiState.Loading)

    val userComplaints = repository.observeUserComplaints()
        .map<List<Complaint>, NezaiUiState<List<Complaint>>> { NezaiUiState.Success(it) }
        .catch { emit(NezaiUiState.Error(it.message ?: "Error loading complaints")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NezaiUiState.Loading)

    val allComplaints = repository.observeAllComplaints()
        .map<List<Complaint>, NezaiUiState<List<Complaint>>> { NezaiUiState.Success(it) }
        .catch { emit(NezaiUiState.Error(it.message ?: "Error loading all complaints")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NezaiUiState.Loading)

    val userProfile = repository.observeUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val emergencyContacts = repository.observeEmergencyContacts()
        .map<List<EmergencyContact>, NezaiUiState<List<EmergencyContact>>> { NezaiUiState.Success(it) }
        .catch { emit(NezaiUiState.Error(it.message ?: "Error loading contacts")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NezaiUiState.Loading)

    val gallery = repository.observeGallery()
        .map<List<GalleryPhoto>, NezaiUiState<List<GalleryPhoto>>> { NezaiUiState.Success(it) }
        .catch { emit(NezaiUiState.Error(it.message ?: "Error loading gallery")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NezaiUiState.Loading)

    init {
        viewModelScope.launch {
            repository.syncUserProfile()
        }
    }

    fun submitComplaint(title: String, description: String, category: String) {
        viewModelScope.launch {
            try {
                repository.submitComplaint(
                    Complaint(
                        title = title,
                        description = description,
                        category = category
                    )
                )
            } catch (e: Exception) {
                Log.e("NezaiViewModel", "Failed to submit complaint", e)
            }
        }
    }

    fun updateComplaintStatus(complaintId: String, status: String, response: String?) {
        viewModelScope.launch {
            try {
                repository.updateComplaintStatus(complaintId, status, response)
            } catch (e: Exception) {
                Log.e("NezaiViewModel", "Failed to update complaint status", e)
            }
        }
    }

    fun updateVillageInfo(info: VillageInfo) {
        viewModelScope.launch {
            try {
                repository.updateVillageInfo(info)
            } catch (e: Exception) {
                Log.e("NezaiViewModel", "Failed to update village info", e)
            }
        }
    }
}
