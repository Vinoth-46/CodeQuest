package com.codequest.learn.data

import com.codequest.learn.data.local.UserPreferences
import com.codequest.learn.data.remote.GeminiQuestionGenerator
import com.codequest.learn.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuestionRepository(
    private val preferences: UserPreferences,
    private val geminiGenerator: GeminiQuestionGenerator
) {
    val userProgress: Flow<UserProgress> = preferences.userProgress
    val apiKey: Flow<String> = preferences.apiKey

    /**
     * Returns all available questions for the given language, prioritizing cached AI questions.
     * Seed questions serve as fallback.
     * Excludes already-completed questions (unless fewer than 3 remain).
     */
    suspend fun getQuestions(
        language: ProgrammingLanguage,
        mode: ChallengeMode? = null
    ): List<ChallengeQuestion> {
        val aiCache = preferences.aiQuestionCache.first()
        val aiQuestions = aiCache.questions.filter {
            it.language == language && (mode == null || it.mode == mode)
        }

        val seed = SeedQuestions.all.filter {
            it.language == language && (mode == null || it.mode == mode)
        }

        // AI questions are the primary source; seed questions serve as offline fallback
        val combined = if (aiQuestions.isNotEmpty()) {
            (aiQuestions + seed).distinctBy { it.id }
        } else {
            seed
        }

        val all = combined.map { it.withShuffledOptions() }
        val progress = preferences.userProgress.first()
        val uncompleted = all.filter { it.id !in progress.completedChallengeIds }

        // If very few uncompleted remain, show all (including completed) so the user still has content
        return if (uncompleted.size >= 3) uncompleted else all
    }

    /**
     * Get questions filtered by mode and optionally tier.
     */
    suspend fun getFilteredQuestions(
        language: ProgrammingLanguage,
        mode: ChallengeMode,
        tier: SkillTier? = null
    ): List<ChallengeQuestion> {
        val questions = getQuestions(language, mode)
        return if (tier != null) {
            questions.filter { it.tier == tier }
        } else {
            questions
        }
    }

    /**
     * Attempt to generate fresh AI questions for the selected language and tier.
     * Returns count of new questions generated and cached.
     */
    suspend fun refreshAiQuestions(
        language: ProgrammingLanguage,
        forceRefresh: Boolean = false,
        tier: SkillTier? = null
    ): Result<Int> {
        val key = preferences.apiKey.first()
        if (key.isBlank()) return Result.failure(IllegalStateException("No Gemini API key set. Please add one in Settings."))

        val today = todayString()
        val cache = preferences.aiQuestionCache.first()

        // Don't regenerate if we already have today's questions for this language/tier and force isn't requested
        if (!forceRefresh && cache.dateGenerated == today) {
            val matching = cache.questions.filter {
                it.language == language && (tier == null || it.tier == tier)
            }
            if (matching.isNotEmpty()) {
                return Result.success(matching.size)
            }
        }

        val result = if (tier != null) {
            geminiGenerator.generateTierSpecificQuestions(key, language, tier, count = 5)
        } else {
            geminiGenerator.generateComprehensiveBatch(key, language)
        }

        return result.map { newQuestions ->
            val otherQuestions = if (tier != null) {
                cache.questions.filter { !(it.language == language && it.tier == tier) }
            } else {
                cache.questions.filter { it.language != language }
            }
            val updatedCache = AiQuestionCache(
                questions = (newQuestions + otherQuestions).distinctBy { it.id },
                dateGenerated = today
            )
            preferences.saveAiQuestionCache(updatedCache)
            newQuestions.size
        }
    }

    /**
     * Returns cached AI questions for a specific language and optional tier.
     */
    suspend fun getCachedAiQuestions(
        language: ProgrammingLanguage,
        tier: SkillTier? = null
    ): List<ChallengeQuestion> {
        val cache = preferences.aiQuestionCache.first()
        return cache.questions.filter {
            it.language == language && (tier == null || it.tier == tier)
        }
    }

    /**
     * Check if today's AI questions have been generated.
     */
    suspend fun hasAiQuestionsForToday(): Boolean {
        val cache = preferences.aiQuestionCache.first()
        return cache.dateGenerated == todayString() && cache.questions.isNotEmpty()
    }

    // ── Progress Management ──

    suspend fun recordCorrectAnswer(question: ChallengeQuestion) {
        preferences.updateProgress { current ->
            val alreadyDone = question.id in current.completedChallengeIds
            val xpGain = if (alreadyDone) 10 else question.tier.xpReward
            val gemGain = if (alreadyDone) 2 else 10
            current.copy(
                xp = current.xp + xpGain,
                gems = current.gems + gemGain,
                completedChallengeIds = current.completedChallengeIds + question.id,
                dailyGoalCompleted = (current.dailyGoalCompleted + 1).coerceAtMost(current.dailyGoalTarget)
            )
        }
    }

    suspend fun unlockHint(): Boolean {
        var success = false
        preferences.updateProgress { current ->
            if (current.gems >= 15) {
                success = true
                current.copy(gems = current.gems - 15)
            } else {
                current
            }
        }
        return success
    }

    suspend fun selectLanguage(language: ProgrammingLanguage) {
        preferences.updateProgress { it.copy(selectedLanguage = language) }
    }

    suspend fun saveApiKey(key: String) {
        preferences.saveApiKey(key)
    }

    suspend fun updateUserName(name: String) {
        preferences.updateProgress { it.copy(userName = name) }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        preferences.updateProgress { it.copy(isDarkMode = enabled) }
    }

    suspend fun setDailyGoalTarget(target: Int) {
        preferences.updateProgress { it.copy(dailyGoalTarget = target) }
    }

    suspend fun checkAndUpdateStreak() {
        preferences.checkAndUpdateStreak()
    }

    suspend fun resetProgress() {
        preferences.saveProgress(UserProgress())
    }

    suspend fun clearAiCache() {
        preferences.saveAiQuestionCache(AiQuestionCache())
    }

    // ── Leaderboard ──

    fun generateLeaderboard(userProgress: UserProgress): List<LeaderboardEntry> {
        val userName = userProgress.userName.ifEmpty { "You" }
        val userXp = userProgress.xp

        val entries = mutableListOf(
            LeaderboardEntry(0, userName, userProgress.selectedLanguage.displayName, userProgress.streakDays, userXp, isCurrentUser = true)
        )

        // Add simulated competitors that scale with user progress
        SeedQuestions.sampleLeaderboard.forEach { template ->
            entries.add(template.copy(
                xp = (template.xp + (userXp * 0.2).toInt()).coerceAtLeast(template.xp)
            ))
        }

        return entries
            .sortedByDescending { it.xp }
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
    }

    private fun todayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
}
