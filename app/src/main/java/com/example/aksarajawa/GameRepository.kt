package com.example.aksarajawa

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GameRepository {
    private val database = Firebase.database.reference
    private val auth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage.reference

    fun getCurrentUser() = auth.currentUser

    suspend fun signIn(email: String, pass: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun signUp(email: String, pass: String, username: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val userId = result.user?.uid ?: throw Exception("User ID null")
            val newProfile = Profile(
                id = userId, 
                username = username, 
                totalScore = 0,
                avatarUrl = "https://api.dicebear.com/7.x/avataaars/png?seed=$username"
            )
            database.child("profiles").child(userId).setValue(newProfile).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun signOut() { auth.signOut() }

    fun getCurrentProfileRealtime(userId: String): Flow<Profile?> = callbackFlow {
        val ref = database.child("profiles").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Profile::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun getCurrentProfile(userId: String): Profile? = withContext(Dispatchers.IO) {
        try {
            val snapshot = database.child("profiles").child(userId).get().await()
            snapshot.getValue(Profile::class.java)
        } catch (e: Exception) { null }
    }

    suspend fun updateProfile(userId: String, username: String, avatarUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updates = mapOf(
                "username" to username,
                "avatarUrl" to avatarUrl,
                "avatar_url" to avatarUrl
            )
            database.child("profiles").child(userId).updateChildren(updates).await()
            
            // Sinkronisasi ke Leaderboard (Jangan biarkan gagal menghentikan update profil)
            try {
                syncUserInLeaderboards(userId, username, avatarUrl)
            } catch (e: Exception) {
                Log.e("GameRepository", "Sync Leaderboard Gagal: ${e.message}")
            }
            
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun uploadProfilePicture(userId: String, imageUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Jika Anda mendapat error 404 di sini, pastikan sudah klik "Get Started" di Firebase Console > Storage
            val fileRef = storage.child("avatars/$userId.jpg")
            fileRef.putFile(imageUri).await()
            val downloadUrl = fileRef.downloadUrl.await().toString()
            
            val updates = mapOf(
                "avatarUrl" to downloadUrl,
                "avatar_url" to downloadUrl
            )
            database.child("profiles").child(userId).updateChildren(updates).await()
            
            val profile = getCurrentProfile(userId)
            syncUserInLeaderboards(userId, profile?.username ?: "", downloadUrl)
            
            Result.success(downloadUrl)
        } catch (e: Exception) { 
            Log.e("GameRepository", "Upload Gagal: ${e.message}")
            Result.failure(e) 
        }
    }

    private suspend fun syncUserInLeaderboards(userId: String, newUsername: String, newAvatarUrl: String) {
        val difficulties = listOf("Mudah", "Sedang", "Sulit")
        val updates = mutableMapOf<String, Any?>()
        
        difficulties.forEach { diff ->
            val snapshot = database.child("leaderboards").child(diff)
                .orderByChild("userId").equalTo(userId).get().await()
            
            snapshot.children.forEach { child ->
                val key = child.key
                if (key != null) {
                    updates["leaderboards/$diff/$key/username"] = newUsername
                    updates["leaderboards/$diff/$key/avatarUrl"] = newAvatarUrl
                    updates["leaderboards/$diff/$key/avatar_url"] = newAvatarUrl
                }
            }
        }
        
        if (updates.isNotEmpty()) {
            database.updateChildren(updates).await()
        }
    }

    suspend fun backfillAvatarUrlInLeaderboard() {
        val difficulties = listOf("Mudah", "Sedang", "Sulit")
        val updates = mutableMapOf<String, Any?>()
        
        difficulties.forEach { diff ->
            val snapshot = database.child("leaderboards").child(diff).get().await()
            snapshot.children.forEach { entry ->
                val userId = entry.child("userId").getValue(String::class.java) ?: return@forEach
                val existingAvatar = entry.child("avatarUrl").getValue(String::class.java)
                    ?: entry.child("avatar_url").getValue(String::class.java)
                
                if (existingAvatar.isNullOrEmpty()) {
                    val profileSnap = database.child("profiles").child(userId).get().await()
                    val profileAvatar = profileSnap.child("avatarUrl").getValue(String::class.java)
                        ?: profileSnap.child("avatar_url").getValue(String::class.java)
                    
                    if (!profileAvatar.isNullOrEmpty()) {
                        updates["leaderboards/$diff/${entry.key}/avatarUrl"] = profileAvatar
                        updates["leaderboards/$diff/${entry.key}/avatar_url"] = profileAvatar
                    }
                }
            }
        }
        if (updates.isNotEmpty()) database.updateChildren(updates).await()
    }

    fun getLeaderboardRealtime(difficulty: String, sortBy: String): Flow<List<LeaderboardEntry>> = callbackFlow {
        val sortField = if (sortBy == "Terbaru") "createdAt" else "score"
        val query = database.child("leaderboards").child(difficulty).orderByChild(sortField).limitToLast(20)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries = mutableListOf<LeaderboardEntry>()
                for (child in snapshot.children) {
                    child.getValue(LeaderboardEntry::class.java)?.let { entries.add(it) }
                }
                trySend(entries.reversed())
            }
            override fun onCancelled(error: DatabaseError) { close(error.toException()) }
        }
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    suspend fun insertScore(userId: String, score: Int, difficulty: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val profileSnap = database.child("profiles").child(userId).get().await()
            val profile = profileSnap.getValue(Profile::class.java)
            
            val entry = LeaderboardEntry(
                userId = userId,
                username = profile?.username ?: "Pemain",
                avatarUrl = profile?.displayAvatar,
                score = score,
                difficulty = difficulty,
                createdAt = System.currentTimeMillis().toString()
            )
            val key = database.child("leaderboards").child(difficulty).push().key
            if (key != null) {
                database.child("leaderboards").child(difficulty).child(key).setValue(entry).await()
                if (profile != null) {
                    database.child("profiles").child(userId).child("totalScore").setValue(profile.totalScore + score).await()
                }
                Result.success(Unit)
            } else Result.failure(Exception("Key failed"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getQuestionsHard(): List<QuestionHard> = withContext(Dispatchers.IO) {
        try {
            val snapshot = database.child("questions_hard").get().await()
            val list = mutableListOf<QuestionHard>()
            for (child in snapshot.children) { child.getValue(QuestionHard::class.java)?.let { list.add(it) } }
            list
        } catch (e: Exception) { emptyList() }
    }
}
