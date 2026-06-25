package com.example.aksarajawa

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseRepository {
    private val database = Firebase.database.reference

    // 1. Simpan atau Update Profile
    fun saveProfile(profile: Profile, onComplete: (Boolean) -> Unit = {}) {
        database.child("profiles").child(profile.id).setValue(profile)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // 2. Simpan Skor Leaderboard
    fun saveLeaderboard(entry: LeaderboardEntry, onComplete: (Boolean) -> Unit = {}) {
        val key = database.child("leaderboards").child(entry.difficulty).push().key
        if (key != null) {
            database.child("leaderboards").child(entry.difficulty).child(key).setValue(entry)
                .addOnCompleteListener { task ->
                    onComplete(task.isSuccessful)
                }
        } else {
            onComplete(false)
        }
    }

    // 3. Ambil Leaderboard secara Realtime
    fun getLeaderboard(difficulty: String, onDataChange: (List<LeaderboardEntry>) -> Unit) {
        database.child("leaderboards").child(difficulty)
            .orderByChild("score")
            .limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries = mutableListOf<LeaderboardEntry>()
                    for (child in snapshot.children) {
                        child.getValue(LeaderboardEntry::class.java)?.let {
                            entries.add(it)
                        }
                    }
                    // Balik urutan agar skor tertinggi di atas
                    onDataChange(entries.reversed())
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                }
            })
    }

    // 4. Ambil Profile User
    fun getProfile(userId: String, onResult: (Profile?) -> Unit) {
        database.child("profiles").child(userId).get().addOnSuccessListener { snapshot ->
            val profile = snapshot.getValue(Profile::class.java)
            onResult(profile)
        }.addOnFailureListener {
            onResult(null)
        }
    }
}
