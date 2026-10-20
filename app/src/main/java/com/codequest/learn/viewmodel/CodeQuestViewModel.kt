package com.codequest.learn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codequest.learn.CodeQuestApplication
import com.codequest.learn.data.QuestionRepository
import com.codequest.learn.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CodeQuestViewModel(
    private val repository: QuestionRepository = CodeQuestApplication.instance.questionRepository
) : ViewModel() {

    // ── User Progress & Preferences ──
    val progress: StateFlow<UserProgress> = repository.userProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProgress()
        )

    val apiKey: StateFlow<String> = repository.apiKey
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    // ── Filters & Active Challenge ──
    private val _selectedMode = MutableStateFlow<ChallengeMode?>(null)
    val selectedMode: StateFlow<ChallengeMode?> = _selectedMode.asStateFlow()

    private val _selectedTier = MutableStateFlow<SkillTier?>(null)
    val selectedTier: StateFlow<SkillTier?> = _selectedTier.asStateFlow()

    private val _questions = MutableStateFlow<List<ChallengeQuestion>>(emptyList())
    val questions: StateFlow<List<ChallengeQuestion>> = _questions.asStateFlow()

    private val _activeQuestionIndex = MutableStateFlow(0)
    val activeQuestionIndex: StateFlow<Int> = _activeQuestionIndex.asStateFlow()

    val currentQuestion: StateFlow<ChallengeQuestion?> = combine(
        _questions,
        _activeQuestionIndex
    ) { qList, idx ->
        if (qList.isNotEmpty() && idx in qList.indices) qList[idx] else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _hintUnlocked = MutableStateFlow(false)
    val hintUnlocked: StateFlow<Boolean> = _hintUnlocked.asStateFlow()

    // ── AI Generation State ──
    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi: StateFlow<Boolean> = _isGeneratingAi.asStateFlow()

    private val _generationMessage = MutableStateFlow<String?>(null)
    val generationMessage: StateFlow<String?> = _generationMessage.asStateFlow()

    private val _hasAiQuestionsToday = MutableStateFlow(false)
    val hasAiQuestionsToday: StateFlow<Boolean> = _hasAiQuestionsToday.asStateFlow()

    // ── Dynamic Leaderboard ──
    val leaderboard: StateFlow<List<LeaderboardEntry>> = progress
        .map { repository.generateLeaderboard(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.checkAndUpdateStreak()
            checkAiStatus()
            loadQuestions()
            checkForAppUpdates()

            // If API key is configured and no AI questions for today, auto-fetch quietly in the background without blocking the UI
            val key = repository.apiKey.first()
            if (key.isNotBlank() && !repository.hasAiQuestionsForToday()) {
                generateDailyAiQuestions(force = false, isSilent = true)
            }
        }

        // Whenever language changes in progress, reload questions and fetch quietly if empty
        viewModelScope.launch {
            progress
                .map { it.selectedLanguage }
                .distinctUntilChanged()
                .collect { lang ->
                    loadQuestions()
                    checkAiStatus()
                    val key = repository.apiKey.first()
                    val cached = repository.getCachedAiQuestions(lang)
                    if (key.isNotBlank() && cached.isEmpty()) {
                        generateDailyAiQuestions(force = false, isSilent = true)
                    }
                }
        }
    }

    private suspend fun checkAiStatus() {
        _hasAiQuestionsToday.value = repository.hasAiQuestionsForToday()
    }

    fun loadQuestions() {
        viewModelScope.launch {
            val lang = progress.value.selectedLanguage
            val mode = _selectedMode.value
            val tier = _selectedTier.value
            val fetched = if (tier != null && mode != null) {
                repository.getFilteredQuestions(lang, mode, tier)
            } else {
                repository.getQuestions(lang, mode).let { list ->
                    if (tier != null) list.filter { it.tier == tier } else list
                }
            }
            _questions.value = fetched
            _activeQuestionIndex.value = 0
            _hintUnlocked.value = false
        }
    }

    fun selectLanguage(language: ProgrammingLanguage) {
        viewModelScope.launch {
            repository.selectLanguage(language)
            _activeQuestionIndex.value = 0
            _hintUnlocked.value = false
            loadQuestions()
        }
    }

    fun selectMode(mode: ChallengeMode?) {
        _selectedMode.value = mode
        _activeQuestionIndex.value = 0
        _hintUnlocked.value = false
        loadQuestions()
    }

    fun selectTier(tier: SkillTier?) {
        _selectedTier.value = tier
        _activeQuestionIndex.value = 0
        _hintUnlocked.value = false
        loadQuestions()
    }

    fun unlockHint(): Boolean {
        val current = progress.value
        if (current.gems >= 15 && !_hintUnlocked.value) {
            viewModelScope.launch {
                val ok = repository.unlockHint()
                if (ok) {
                    _hintUnlocked.value = true
                }
            }
            return true
        }
        return false
    }

    fun recordCorrectAnswer(question: ChallengeQuestion) {
        viewModelScope.launch {
            repository.recordCorrectAnswer(question)
        }
    }

    fun nextQuestion() {
        val total = _questions.value.size
        if (total > 0) {
            _activeQuestionIndex.value = (_activeQuestionIndex.value + 1) % total
            _hintUnlocked.value = false
        }
    }

    fun previousQuestion() {
        val total = _questions.value.size
        if (total > 0) {
            val prev = _activeQuestionIndex.value - 1
            _activeQuestionIndex.value = if (prev < 0) total - 1 else prev
            _hintUnlocked.value = false
        }
    }

    fun generateDailyAiQuestions(force: Boolean = false, tier: SkillTier? = null, isSilent: Boolean = false) {
        viewModelScope.launch {
            val lang = progress.value.selectedLanguage
            if (!isSilent) {
                _isGeneratingAi.value = true
                _generationMessage.value = if (tier != null) {
                    "Generating fresh ${tier.title} challenges with Gemini AI..."
                } else {
                    "Generating fresh challenges with Gemini AI..."
                }
            }

            val result = repository.refreshAiQuestions(lang, forceRefresh = force, tier = tier)
            result.fold(
                onSuccess = { count ->
                    if (!isSilent) {
                        _generationMessage.value = "Success! Loaded $count fresh challenges."
                    }
                    _hasAiQuestionsToday.value = true
                    loadQuestions()
                },
                onFailure = { error ->
                    if (!isSilent) {
                        _generationMessage.value = error.message ?: "Failed to generate AI questions"
                    }
                }
            )
            if (!isSilent) {
                _isGeneratingAi.value = false
            }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDarkMode(enabled)
        }
    }

    fun setDailyGoalTarget(target: Int) {
        viewModelScope.launch {
            repository.setDailyGoalTarget(target)
        }
    }

    fun clearGenerationMessage() {
        _generationMessage.value = null
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            repository.saveApiKey(key.trim())
            _generationMessage.value = "API key saved successfully"
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            repository.updateUserName(name.trim())
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            repository.resetProgress()
            repository.clearAiCache()
            loadQuestions()
        }
    }

    // ── In-App Play Store-style Updates ──
    private val appUpdateManager = com.codequest.learn.data.remote.AppUpdateManager()

    private val _availableUpdate = MutableStateFlow<com.codequest.learn.data.remote.AppUpdateInfo?>(null)
    val availableUpdate: StateFlow<com.codequest.learn.data.remote.AppUpdateInfo?> = _availableUpdate.asStateFlow()

    private val _isDownloadingUpdate = MutableStateFlow(false)
    val isDownloadingUpdate: StateFlow<Boolean> = _isDownloadingUpdate.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Float?>(null)
    val downloadProgress: StateFlow<Float?> = _downloadProgress.asStateFlow()

    fun checkForAppUpdates(manifestUrl: String? = null) {
        viewModelScope.launch {
            val result = if (manifestUrl != null) {
                appUpdateManager.checkForUpdates(manifestUrl)
            } else {
                appUpdateManager.checkForUpdates()
            }
            result.onSuccess { updateInfo ->
                if (updateInfo != null) {
                    _availableUpdate.value = updateInfo
                }
            }
        }
    }

    fun startAppUpdate(context: android.content.Context) {
        val update = _availableUpdate.value ?: return
        viewModelScope.launch {
            _isDownloadingUpdate.value = true
            _downloadProgress.value = 0f
            val result = appUpdateManager.downloadAndInstallApk(
                context = context,
                updateInfo = update,
                onProgress = { progress ->
                    _downloadProgress.value = progress
                }
            )
            _isDownloadingUpdate.value = false
            result.onFailure { error ->
                android.widget.Toast.makeText(
                    context,
                    "Update failed: ${error.localizedMessage ?: "Unknown error"}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun dismissUpdatePrompt() {
        _availableUpdate.value = null
        _downloadProgress.value = null
        _isDownloadingUpdate.value = false
    }
}
