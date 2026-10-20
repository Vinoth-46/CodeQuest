package com.codequest.learn.data.remote

import com.codequest.learn.model.ChallengeMode
import com.codequest.learn.model.ChallengeQuestion
import com.codequest.learn.model.ProgrammingLanguage
import com.codequest.learn.model.SkillTier
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GeminiQuestionGenerator {

    private val gson = Gson()
    // gemini-3.8-flash is the primary model supported for this key
    private val candidateModels = listOf("gemini-3.8-flash", "gemini-2.5-flash", "gemini-flash-latest", "gemini-pro-latest")

    /**
     * Generates a balanced set of questions across ALL skill tiers:
     * Beginner, Intermediate, and Expert across all 4 challenge modes.
     */
    suspend fun generateComprehensiveBatch(
        apiKey: String,
        language: ProgrammingLanguage
    ): Result<List<ChallengeQuestion>> = withContext(Dispatchers.IO) {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isBlank()) return@withContext Result.failure(IllegalStateException("No API key configured. Please add one in Settings."))

        val prompt = buildComprehensivePrompt(language)
        generateWithFallback(trimmedKey, prompt) { text ->
            parseMixedResponse(text, language)
        }
    }

    /**
     * Generates questions for a specific skill tier (Beginner, Intermediate, or Expert)
     */
    suspend fun generateTierSpecificQuestions(
        apiKey: String,
        language: ProgrammingLanguage,
        tier: SkillTier,
        count: Int = 4
    ): Result<List<ChallengeQuestion>> = withContext(Dispatchers.IO) {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isBlank()) return@withContext Result.failure(IllegalStateException("No API key configured. Please add one in Settings."))

        val prompt = buildTierPrompt(language, tier, count)
        generateWithFallback(trimmedKey, prompt) { text ->
            parseMixedResponse(text, language)
        }
    }

    suspend fun generateMixedQuestions(
        apiKey: String,
        language: ProgrammingLanguage,
        count: Int = 6
    ): Result<List<ChallengeQuestion>> = withContext(Dispatchers.IO) {
        generateComprehensiveBatch(apiKey, language)
    }

    /**
     * Direct HTTP REST implementation - fast, robust, and completely immune to GrpcError serialization bugs
     */
    private suspend fun generateWithFallback(
        apiKey: String,
        prompt: String,
        parser: (String) -> List<ChallengeQuestion>
    ): Result<List<ChallengeQuestion>> {
        var lastError: Exception? = null

        for (modelName in candidateModels) {
            try {
                val responseText = executeHttpRequest(modelName, apiKey, prompt)
                if (responseText.isNotBlank()) {
                    val parsed = parser(responseText)
                    if (parsed.isNotEmpty()) {
                        return Result.success(parsed.map { it.withShuffledOptions() })
                    }
                }
            } catch (e: Exception) {
                lastError = e
                // Try next model if 404 or 503
                continue
            }
        }

        val friendlyMessage = formatErrorMessage(lastError, apiKey)
        return Result.failure(Exception(friendlyMessage))
    }

    private fun executeHttpRequest(modelName: String, apiKey: String, prompt: String): String {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"
        val url = URL(endpoint)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 15000
        connection.readTimeout = 30000
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")

        // Build clean JSON payload
        val requestPayload = gson.toJson(
            mapOf(
                "contents" to listOf(
                    mapOf(
                        "parts" to listOf(
                            mapOf("text" to prompt)
                        )
                    )
                ),
                "generationConfig" to mapOf(
                    "temperature" to 0.5,
                    "maxOutputTokens" to 2048
                )
            )
        )

        OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
            writer.write(requestPayload)
            writer.flush()
        }

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseString = connection.inputStream.bufferedReader().use { it.readText() }
            return extractTextFromResponse(responseString)
        } else {
            val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
            val sanitizedError = sanitizeApiKey(errorBody, apiKey)
            throw RuntimeException("HTTP $responseCode: $sanitizedError")
        }
    }

    private fun extractTextFromResponse(jsonResponse: String): String {
        return try {
            val parsed = gson.fromJson(jsonResponse, ApiResponse::class.java)
            parsed.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text.orEmpty()
        } catch (e: Exception) {
            ""
        }
    }

    private fun sanitizeApiKey(text: String, apiKey: String): String {
        var clean = text
        if (apiKey.isNotBlank()) {
            clean = clean.replace(apiKey, "••••••••")
        }
        return clean.replace(Regex("key=[^&\\s\"']+"), "key=••••••••")
    }

    private fun formatErrorMessage(e: Exception?, apiKey: String = ""): String {
        var msg = e?.message.orEmpty()
        msg = sanitizeApiKey(msg, apiKey)
        return when {
            msg.contains("API_KEY_INVALID", ignoreCase = true) || msg.contains("400", ignoreCase = true) ->
                "Invalid API Key. Please verify your key in Settings."
            msg.contains("PERMISSION_DENIED", ignoreCase = true) || msg.contains("403", ignoreCase = true) ->
                "Permission Denied: Please check API key permissions."
            msg.contains("RESOURCE_EXHAUSTED", ignoreCase = true) || msg.contains("429", ignoreCase = true) ->
                "Quota rate limit reached. Please wait a moment and try again."
            msg.contains("Unable to resolve host", ignoreCase = true) ->
                "No internet connection. Offline seed challenges are available."
            msg.isNotBlank() -> msg
            else -> "Failed to generate AI questions. Please retry."
        }
    }

    private fun buildComprehensivePrompt(language: ProgrammingLanguage): String {
        return """
            You are an expert ${language.displayName} educator. Generate exactly 4 concise, high-quality challenges across all skill tiers:
            1. "DAILY_QUIZ" (tier: "BEGINNER"): Multiple choice with 4 distinct options, set "correctIndex" (0-3).
            2. "MISSING_WORDS" (tier: "INTERMEDIATE"): Fill-in-the-blank code with ___1___, ___2___. Provide "missingBlanks" and "wordBank" (correct + 2 distractors). "options": [].
            3. "GUESS_OUTPUT" (tier: "INTERMEDIATE"): Short code (max 4 lines). Exactly 4 outputs in "options", set "correctIndex".
            4. "LOGIC_BUILDER" (tier: "EXPERT"): 4 lines in "logicStepsCorrectOrder". "options": [].

            RULES:
            - Keep code snippets under 5 lines and explanations under 1 sentence for fast loading.
            - Randomize correctIndex among 0, 1, 2, 3 so Option A is not always correct.
            - Valid syntax only.

            Return ONLY raw JSON array without markdown fences:
            [
              {
                "mode": "DAILY_QUIZ",
                "tier": "BEGINNER",
                "title": "Short title",
                "prompt": "Question text",
                "codeSnippet": "code here",
                "options": ["A", "B", "C", "D"],
                "correctIndex": 2,
                "missingBlanks": [],
                "wordBank": [],
                "logicStepsCorrectOrder": [],
                "hint": "1 sentence hint",
                "explanation": "1 sentence explanation"
              }
            ]
        """.trimIndent()
    }

    private fun buildTierPrompt(language: ProgrammingLanguage, tier: SkillTier, count: Int): String {
        val tierTopics = when (tier) {
            SkillTier.BEGINNER -> "syntax, data types, if/else, loops, list basics"
            SkillTier.INTERMEDIATE -> "OOP, dicts/maps, slicing, list comprehensions, exceptions"
            SkillTier.EXPERT -> "binary search, two-pointer, recursion, complexity, generators"
        }

        return """
            You are an expert ${language.displayName} educator. Generate exactly $count fast, concise challenges for the "${tier.name}" level ($tierTopics).
            Distribute across DAILY_QUIZ, MISSING_WORDS, and GUESS_OUTPUT.
            Keep code snippets under 5 lines and explanations to 1 sentence. Randomize correctIndex among 0-3.

            Return ONLY raw JSON array without markdown fences:
            [
              {
                "mode": "DAILY_QUIZ",
                "tier": "${tier.name}",
                "title": "Short title",
                "prompt": "Question text",
                "codeSnippet": "code here",
                "options": ["A", "B", "C", "D"],
                "correctIndex": 1,
                "missingBlanks": [],
                "wordBank": [],
                "logicStepsCorrectOrder": [],
                "hint": "Helpful hint",
                "explanation": "Clear explanation"
              }
            ]
        """.trimIndent()
    }

    private fun parseMixedResponse(
        rawText: String,
        language: ProgrammingLanguage
    ): List<ChallengeQuestion> {
        val jsonText = extractJson(rawText)
        return try {
            val listType = object : TypeToken<List<RawMixedQuestion>>() {}.type
            val rawList: List<RawMixedQuestion> = gson.fromJson(jsonText, listType)
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            rawList.mapIndexed { index, raw ->
                val mode = try {
                    ChallengeMode.valueOf(raw.mode ?: "DAILY_QUIZ")
                } catch (e: Exception) {
                    ChallengeMode.DAILY_QUIZ
                }
                val tier = try {
                    SkillTier.valueOf(raw.tier ?: "BEGINNER")
                } catch (e: Exception) {
                    SkillTier.BEGINNER
                }
                ChallengeQuestion(
                    id = "ai_${language.tag}_${mode.name}_${tier.name}_${today}_$index",
                    language = language,
                    tier = tier,
                    mode = mode,
                    title = raw.title.orEmpty(),
                    prompt = raw.prompt.orEmpty(),
                    codeSnippet = raw.codeSnippet.orEmpty(),
                    options = raw.options ?: emptyList(),
                    correctIndex = raw.correctIndex ?: 0,
                    missingBlanks = raw.missingBlanks ?: emptyList(),
                    wordBank = raw.wordBank ?: emptyList(),
                    logicStepsCorrectOrder = raw.logicStepsCorrectOrder ?: emptyList(),
                    hint = raw.hint.orEmpty(),
                    explanation = raw.explanation.orEmpty(),
                    isFromAi = true
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun extractJson(raw: String): String {
        var text = raw.trim()
        if (text.startsWith("```")) {
            val firstNewline = text.indexOf('\n')
            if (firstNewline != -1) text = text.substring(firstNewline + 1)
            if (text.endsWith("```")) text = text.substring(0, text.length - 3)
            text = text.trim()
        }
        val start = text.indexOf('[')
        val end = text.lastIndexOf(']')
        return if (start != -1 && end != -1 && end > start) {
            text.substring(start, end + 1)
        } else {
            text
        }
    }

    private data class ApiResponse(val candidates: List<Candidate>?)
    private data class Candidate(val content: Content?)
    private data class Content(val parts: List<Part>?)
    private data class Part(val text: String?)

    private data class RawMixedQuestion(
        val mode: String? = null,
        val tier: String? = null,
        val title: String? = null,
        val prompt: String? = null,
        val codeSnippet: String? = null,
        val options: List<String>? = null,
        val correctIndex: Int? = null,
        val missingBlanks: List<String>? = null,
        val wordBank: List<String>? = null,
        val logicStepsCorrectOrder: List<String>? = null,
        val hint: String? = null,
        val explanation: String? = null
    )
}
