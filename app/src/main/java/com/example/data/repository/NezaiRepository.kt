package com.example.data.repository

import android.content.Context
import com.example.data.model.*
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class NezaiRepository(private val context: Context) {
    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance(context.getString(R.string.firestore_database_id))
    }
    private val auth = Firebase.auth

    private fun requireUserId(): String = auth.currentUser?.uid
        ?: throw IllegalStateException("User must be signed in with Google.")

    // Village Info
    fun observeVillageInfo(): Flow<VillageInfo?> = db.collection("village_info")
        .document("giridharpur")
        .snapshots()
        .map { it.toObject(VillageInfo::class.java) }

    suspend fun updateVillageInfo(info: VillageInfo) {
        val docRef = db.collection("village_info").document("giridharpur")
        try {
            docRef.set(info.copy(updatedAt = null)).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // Panchayat Members
    fun observePanchayatMembers(): Flow<List<PanchayatMember>> = db.collection("panchayat_members")
        .orderBy("order")
        .snapshots()
        .map { it.toObjects(PanchayatMember::class.java) }

    suspend fun addPanchayatMember(member: PanchayatMember) {
        try {
            db.collection("panchayat_members").add(member).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "panchayat_members")
            throw e
        }
    }

    // Govt Schemes
    fun observeGovtSchemes(): Flow<List<GovtScheme>> = db.collection("govt_schemes")
        .snapshots()
        .map { it.toObjects(GovtScheme::class.java) }

    // News
    fun observeNews(): Flow<List<NewsItem>> = db.collection("news")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .snapshots()
        .map { it.toObjects(NewsItem::class.java) }

    // Complaints
    fun observeUserComplaints(): Flow<List<Complaint>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("complaints")
                .whereEqualTo("userId", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { it.toObjects(Complaint::class.java) }
        )
    }

    fun observeAllComplaints(): Flow<List<Complaint>> = db.collection("complaints")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .snapshots()
        .map { it.toObjects(Complaint::class.java) }

    suspend fun submitComplaint(complaint: Complaint) {
        val uid = requireUserId()
        val ref = db.collection("complaints")
        try {
            val complaintNumber = "CMP-${System.currentTimeMillis() % 1000000}"
            ref.add(complaint.copy(userId = uid, complaintNumber = complaintNumber)).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, ref.path)
            throw e
        }
    }

    suspend fun updateComplaintStatus(complaintId: String, status: String, response: String?) {
        val docRef = db.collection("complaints").document(complaintId)
        try {
            docRef.update(
                mapOf(
                    "status" to status,
                    "adminResponse" to response,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            throw e
        }
    }

    // User Profile
    fun observeUserProfile(): Flow<User?> = flow {
        val uid = auth.currentUser?.uid ?: return@flow
        emitAll(
            db.collection("users").document(uid).snapshots().map { it.toObject(User::class.java) }
        )
    }

    suspend fun syncUserProfile() {
        val firebaseUser = auth.currentUser ?: return
        val docRef = db.collection("users").document(firebaseUser.uid)
        try {
            val existing = docRef.get().await()
            if (!existing.exists()) {
                val newUser = User(
                    name = firebaseUser.displayName ?: "",
                    phone = firebaseUser.phoneNumber ?: "",
                    role = "user"
                )
                docRef.set(newUser).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
        }
    }

    // Emergency Contacts
    fun observeEmergencyContacts(): Flow<List<EmergencyContact>> = db.collection("emergency_contacts")
        .snapshots()
        .map { it.toObjects(EmergencyContact::class.java) }

    // Gallery
    fun observeGallery(): Flow<List<GalleryPhoto>> = db.collection("gallery")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .snapshots()
        .map { it.toObjects(GalleryPhoto::class.java) }
}
