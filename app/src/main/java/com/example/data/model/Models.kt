package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class User(
    @DocumentId val id: String = "",
    val name: String = "",
    val phone: String = "",
    val role: String = "user", // "admin" or "user"
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class VillageInfo(
    @DocumentId val id: String = "",
    val history: String = "",
    val introduction: String = "",
    val ward_details: String = "",
    val population: Long = 0,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PanchayatMember(
    @DocumentId val id: String = "",
    val name: String = "",
    val role: String = "",
    val photoUrl: String = "",
    val contact: String = "",
    val order: Int = 0
)

@IgnoreExtraProperties
data class GovtScheme(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val eligibility: String = "",
    val link: String = "",
    val category: String = ""
)

@IgnoreExtraProperties
data class Complaint(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val status: String = "pending", // "pending", "in_progress", "resolved", "rejected"
    val imageUrl: String? = null,
    val complaintNumber: String = "",
    val adminResponse: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class NewsItem(
    @DocumentId val id: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    @ServerTimestamp val timestamp: Timestamp? = null
)

@IgnoreExtraProperties
data class GalleryPhoto(
    @DocumentId val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    @ServerTimestamp val timestamp: Timestamp? = null,
    val category: String = ""
)

@IgnoreExtraProperties
data class EmergencyContact(
    @DocumentId val id: String = "",
    val name: String = "",
    val number: String = "",
    val category: String = ""
)
