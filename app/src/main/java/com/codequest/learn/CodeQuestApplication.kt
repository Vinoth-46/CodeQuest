package com.codequest.learn

import android.app.Application
import com.codequest.learn.data.QuestionRepository
import com.codequest.learn.data.local.UserPreferences
import com.codequest.learn.data.remote.GeminiQuestionGenerator

class CodeQuestApplication : Application() {

    lateinit var userPreferences: UserPreferences
        private set

    lateinit var geminiGenerator: GeminiQuestionGenerator
        private set

    lateinit var questionRepository: QuestionRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        userPreferences = UserPreferences(applicationContext)
        geminiGenerator = GeminiQuestionGenerator()
        questionRepository = QuestionRepository(userPreferences, geminiGenerator)
    }

    companion object {
        lateinit var instance: CodeQuestApplication
            private set
    }
}
