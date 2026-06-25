package com.example.aksarajawa

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository()
    private val notificationHelper = NotificationHelper(application)
    private val auth = FirebaseAuth.getInstance()

    // UI STATE
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isScoreSaved = MutableStateFlow(false)
    val isScoreSaved: StateFlow<Boolean> = _isScoreSaved.asStateFlow()

    // USER DATA
    private val _currentUser = MutableStateFlow<Profile?>(null)
    val currentUser: StateFlow<Profile?> = _currentUser.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    // GAME STATE
    private val _gameScore = MutableStateFlow(0)
    val gameScore: StateFlow<Int> = _gameScore.asStateFlow()

    private val _correctCount = MutableStateFlow(0)
    val correctCount: StateFlow<Int> = _correctCount.asStateFlow()

    private val _wrongCount = MutableStateFlow(0)
    val wrongCount: StateFlow<Int> = _wrongCount.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    // QUESTIONS
    private val _easyMediumQuestions = MutableStateFlow<List<QuestionLocal>>(emptyList())
    val easyMediumQuestions: StateFlow<List<QuestionLocal>> = _easyMediumQuestions.asStateFlow()

    private val _hardQuestions = MutableStateFlow<List<QuestionHard>>(emptyList())
    val hardQuestions: StateFlow<List<QuestionHard>> = _hardQuestions.asStateFlow()

    // HARD MODE SELECTIONS
    private val _selectedAksara = MutableStateFlow<List<String>>(emptyList())
    val selectedAksara: StateFlow<List<String>> = _selectedAksara.asStateFlow()

    private var leaderboardJob: Job? = null
    private var profileJob: Job? = null

    init {
        observeCurrentUser()
        // JALANKAN SINKRONISASI DATA LAMA:
        // Memastikan entry leaderboard lama mendapatkan foto profil dari tabel profiles
        viewModelScope.launch {
            try {
                repository.backfillAvatarUrlInLeaderboard()
            } catch (e: Exception) {
                // Gagal backfill tidak menghentikan aplikasi
            }
        }
    }

    private fun observeCurrentUser() {
        profileJob?.cancel()
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            profileJob = viewModelScope.launch {
                repository.getCurrentProfileRealtime(firebaseUser.uid)
                    .collect { profile ->
                        if (profile != null) {
                            _currentUser.value = profile
                        } else {
                            val newProfile = Profile(
                                id = firebaseUser.uid,
                                username = firebaseUser.email?.split("@")?.get(0) ?: "Pemain",
                                totalScore = 0
                            )
                            _currentUser.value = newProfile
                            repository.updateProfile(newProfile.id, newProfile.username, "")
                        }
                    }
            }
        }
    }

    fun fetchCurrentUser() { observeCurrentUser() }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.signIn(email, pass)
                .onSuccess {
                    observeCurrentUser()
                    notificationHelper.showLoginNotification()
                    onSuccess()
                }
                .onFailure { _error.value = "Login Gagal: ${it.message}" }
            _isLoading.value = false
        }
    }

    fun signUp(email: String, pass: String, username: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.signUp(email, pass, username)
                .onSuccess {
                    observeCurrentUser()
                    notificationHelper.showLoginNotification()
                    onSuccess()
                }
                .onFailure { _error.value = "Daftar Gagal: ${it.message}" }
            _isLoading.value = false
        }
    }

    fun uploadAvatar(imageUri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.uploadProfilePicture(userId, imageUri)
                .onFailure { _error.value = "Gagal mengunggah foto" }
            _isLoading.value = false
        }
    }

    fun updateProfile(username: String, avatarUrl: String, onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateProfile(userId, username, avatarUrl)
                .onSuccess { onSuccess() }
                .onFailure { _error.value = "Gagal update profil" }
            _isLoading.value = false
        }
    }

    fun loadLeaderboard(difficulty: String = "Mudah", sortBy: String = "Skor Tertinggi") {
        leaderboardJob?.cancel()
        leaderboardJob = viewModelScope.launch {
            _isLoading.value = true
            repository.getLeaderboardRealtime(difficulty, sortBy)
                .catch { e ->
                    _error.value = "Gagal memuat leaderboard"
                    _isLoading.value = false
                }
                .collect { entries ->
                    _leaderboard.value = entries
                    _isLoading.value = false
                }
        }
    }

    fun saveScore(difficulty: String) {
        if (_isScoreSaved.value) return
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            repository.insertScore(userId, _gameScore.value, difficulty)
                .onSuccess {
                    _isScoreSaved.value = true
                }
                .onFailure { _error.value = "Gagal menyimpan skor" }
            _isLoading.value = false
        }
    }

    fun generateEasyQuestions() {
        resetGame()
        val questions = aksaraDasar.shuffled().take(10).map { correct ->
            val distractors = aksaraDasar.filter { it != correct }.shuffled().take(3).map { it.latin }
            QuestionLocal(question = correct.char, correctAnswer = correct.latin, options = (distractors + correct.latin).shuffled())
        }
        _easyMediumQuestions.value = questions
    }

    fun generateMediumQuestions() {
        resetGame()
        val allComb = aksaraDasar.flatMap { b -> sandhanganVokal.map { v -> (if (v.latin == "a") b.latin else b.latin.dropLast(1) + v.latin) to (b.char + v.char) } }
        val questions = (1..10).map {
            val correct = allComb.random()
            val distractors = allComb.filter { it.first != correct.first }.shuffled().take(3).map { it.first }
            QuestionLocal(question = correct.second, correctAnswer = correct.first, options = (distractors + correct.first).shuffled())
        }
        _easyMediumQuestions.value = questions
    }

    fun loadHardQuestions() {
        resetGame()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val questions = repository.getQuestionsHard()
                if (questions.isNotEmpty()) {
                    _hardQuestions.value = questions.shuffled().take(10)
                }
            } catch (e: Exception) { _error.value = "Gagal memuat soal" }
            _isLoading.value = false
        }
    }

    fun onAksaraSelected(aksara: String) { _selectedAksara.value += aksara }
    fun removeLastAksara() { if (_selectedAksara.value.isNotEmpty()) _selectedAksara.value = _selectedAksara.value.dropLast(1) }
    fun removeAksaraAt(index: Int) {
        val currentList = _selectedAksara.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _selectedAksara.value = currentList
        }
    }

    fun checkHardAnswer(correctList: List<String>, points: Int): Boolean {
        val isCorrect = _selectedAksara.value == correctList
        submitAnswer(isCorrect, points)
        // Clear is now handled by the UI when moving to the next question
        return isCorrect
    }

    fun submitAnswer(isCorrect: Boolean, points: Int) {
        if (isCorrect) {
            _gameScore.value += points
            _correctCount.value += 1
        } else {
            _wrongCount.value += 1
        }
        _currentQuestionIndex.value += 1
    }

    fun resetGame() {
        _gameScore.value = 0
        _correctCount.value = 0
        _wrongCount.value = 0
        _currentQuestionIndex.value = 0
        _selectedAksara.value = emptyList()
        _isScoreSaved.value = false
        _error.value = null
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.signOut()
            _currentUser.value = null
            notificationHelper.showLogoutNotification()
            onSuccess()
        }
    }

    fun resetSelection() { _selectedAksara.value = emptyList() }
    fun clearError() { _error.value = null }
}
