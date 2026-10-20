package com.codequest.learn.data.remote

import com.codequest.learn.model.LeaderboardEntry
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

data class CloudUser(
    val id: String = "",
    val googleId: String = "",
    val email: String = "",
    val displayName: String = "",
    val avatarUrl: String = "",
    val xp: Int = 0,
    val streakDays: Int = 0,
    val selectedLanguage: String = "Python",
    val level: Int = 1,
    val rank: Int = 0
)

data class CloudAuthResponse(
    val success: Boolean = false,
    val user: CloudUser? = null,
    val error: String? = null
)

data class CloudLeaderboardEntry(
    val rank: Int,
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val language: String = "Python",
    val streakDays: Int = 0,
    val xp: Int = 0,
    val level: Int = 1,
    val isCurrentUser: Boolean = false
)

data class CloudLeaderboardResponse(
    val success: Boolean = false,
    val totalUsers: Int = 0,
    val entries: List<CloudLeaderboardEntry> = emptyList(),
    val error: String? = null
)

class BackendApiClient(
    var baseUrl: String = DEFAULT_BASE_URL
) {
    private val gson = Gson()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun authenticateWithGoogle(
        idToken: String? = null,
        googleId: String,
        email: String,
        displayName: String,
        avatarUrl: String = "",
        initialXp: Int = 0,
        initialStreak: Int = 0,
        selectedLanguage: String = "Python"
    ): Result<CloudUser> = withContext(Dispatchers.IO) {
        try {
            val payload = mapOf(
                "idToken" to (idToken ?: ""),
                "googleId" to googleId,
                "email" to email,
                "displayName" to displayName,
                "avatarUrl" to avatarUrl,
                "initialXp" to initialXp,
                "initialStreak" to initialStreak,
                "selectedLanguage" to selectedLanguage
            )

            val body = gson.toJson(payload).toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url("$baseUrl/api/auth/google")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}: $responseBody"))
            }

            val result = gson.fromJson(responseBody, CloudAuthResponse::class.java)
            if (result.success && result.user != null) {
                Result.success(result.user)
            } else {
                Result.failure(Exception(result.error ?: "Failed to authenticate with cloud server"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncProgress(
        googleId: String,
        email: String,
        displayName: String,
        avatarUrl: String,
        xp: Int,
        streakDays: Int,
        selectedLanguage: String,
        solvedCount: Int
    ): Result<CloudUser> = withContext(Dispatchers.IO) {
        try {
            val payload = mapOf(
                "googleId" to googleId,
                "email" to email,
                "displayName" to displayName,
                "avatarUrl" to avatarUrl,
                "xp" to xp,
                "streakDays" to streakDays,
                "selectedLanguage" to selectedLanguage,
                "solvedCount" to solvedCount
            )

            val body = gson.toJson(payload).toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url("$baseUrl/api/user/sync")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}: $responseBody"))
            }

            val result = gson.fromJson(responseBody, CloudAuthResponse::class.java)
            if (result.success && result.user != null) {
                Result.success(result.user)
            } else {
                Result.failure(Exception(result.error ?: "Failed to sync progress"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchLiveLeaderboard(
        currentUserGoogleId: String = "",
        currentUserEmail: String = "",
        limit: Int = 50
    ): Result<List<LeaderboardEntry>> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/api/leaderboard?limit=$limit&googleId=$currentUserGoogleId&email=$currentUserEmail"
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}: $responseBody"))
            }

            val result = gson.fromJson(responseBody, CloudLeaderboardResponse::class.java)
            if (result.success) {
                val mapped = result.entries.map { e ->
                    LeaderboardEntry(
                        rank = e.rank,
                        name = e.name,
                        languageBadge = e.language,
                        streakDays = e.streakDays,
                        xp = e.xp,
                        isCurrentUser = e.isCurrentUser,
                        avatarUrl = e.avatarUrl,
                        level = e.level,
                        id = e.id
                    )
                }
                Result.success(mapped)
            } else {
                Result.failure(Exception(result.error ?: "Leaderboard response unsuccessful"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        // Can point to Render/Vercel or local host emulator IP
        const val DEFAULT_BASE_URL = "https://codequest-api.onrender.com"
        const val LOCAL_EMULATOR_URL = "http://10.0.2.2:5000"
    }
}
