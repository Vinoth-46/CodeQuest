package com.codequest.learn.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.codequest.learn.model.AiQuestionCache
import com.codequest.learn.model.ProgrammingLanguage
import com.codequest.learn.model.UserProgress
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "codequest_prefs")

class UserPreferences(private val context: Context) {

    private val gson = Gson()

    companion object {
        private val KEY_PROGRESS = stringPreferencesKey("user_progress")
        private val KEY_API_KEY = stringPreferencesKey("gemini_api_key")
        private val KEY_AI_CACHE = stringPreferencesKey("ai_question_cache")

        fun todayDateString(): String {
            return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        }

        fun yesterdayDateString(): String {
            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
        }
    }

    // ── User Progress ──

    val userProgress: Flow<UserProgress> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_PROGRESS]
        if (json != null) {
            try {
                gson.fromJson(json, UserProgress::class.java)
            } catch (e: Exception) {
                UserProgress()
            }
        } else {
            UserProgress()
        }
    }

    suspend fun saveProgress(progress: UserProgress) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PROGRESS] = gson.toJson(progress)
        }
    }

    suspend fun updateProgress(transform: (UserProgress) -> UserProgress) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_PROGRESS]?.let {
                try {
                    gson.fromJson(it, UserProgress::class.java)
                } catch (e: Exception) {
                    UserProgress()
                }
            } ?: UserProgress()
            val updated = transform(current)
            prefs[KEY_PROGRESS] = gson.toJson(updated)
        }
    }

    // ── Gemini API Key ──

    val apiKey: Flow<String> = context.dataStore.data.map { prefs ->
        val saved = prefs[KEY_API_KEY]
        if (!saved.isNullOrBlank()) {
            saved
        } else {
            com.codequest.learn.BuildConfig.GEMINI_API_KEY
        }
    }

    suspend fun saveApiKey(key: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_API_KEY] = key
        }
    }

    // ── AI Question Cache ──

    val aiQuestionCache: Flow<AiQuestionCache> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_AI_CACHE]
        if (json != null) {
            try {
                gson.fromJson(json, AiQuestionCache::class.java)
            } catch (e: Exception) {
                AiQuestionCache()
            }
        } else {
            AiQuestionCache()
        }
    }

    suspend fun saveAiQuestionCache(cache: AiQuestionCache) {
        context.dataStore.edit { prefs ->
            prefs[KEY_AI_CACHE] = gson.toJson(cache)
        }
    }

    // ── Streak Management ──

    suspend fun checkAndUpdateStreak() {
        val today = todayDateString()
        updateProgress { progress ->
            when {
                progress.lastActiveDate == today -> progress
                progress.lastActiveDate == yesterdayDateString() -> progress.copy(
                    streakDays = progress.streakDays + 1,
                    lastActiveDate = today,
                    dailyGoalCompleted = 0
                )
                else -> progress.copy(
                    streakDays = if (progress.lastActiveDate.isEmpty()) 1 else 0,
                    lastActiveDate = today,
                    dailyGoalCompleted = 0
                )
            }
        }
    }
}
