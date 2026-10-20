package com.codequest.learn.model

enum class ProgrammingLanguage(val displayName: String, val tag: String) {
    PYTHON("Python", "PY"),
    JAVA("Java", "JV")
}

enum class SkillTier(val title: String, val xpReward: Int) {
    BEGINNER("Beginner", 25),
    INTERMEDIATE("Intermediate", 40),
    EXPERT("Expert", 65)
}

enum class ChallengeMode(val label: String, val description: String) {
    DAILY_QUIZ("Daily Quiz", "Test your concept mastery"),
    MISSING_WORDS("Fill the Code", "Complete code by filling blanks"),
    GUESS_OUTPUT("Predict Output", "Read code and predict the result"),
    LOGIC_BUILDER("Logic Builder", "Arrange steps in correct order")
}

data class ChallengeQuestion(
    val id: String,
    val language: ProgrammingLanguage,
    val tier: SkillTier,
    val mode: ChallengeMode,
    val title: String,
    val prompt: String,
    val codeSnippet: String,
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val missingBlanks: List<String> = emptyList(),
    val wordBank: List<String> = emptyList(),
    val logicStepsCorrectOrder: List<String> = emptyList(),
    val hint: String = "",
    val explanation: String = "",
    val isFromAi: Boolean = false
) {
    /**
     * Randomizes the option order and recalibrates correctIndex
     * so students can never guess by repeatedly tapping the first option.
     */
    fun withShuffledOptions(): ChallengeQuestion {
        if (options.size <= 1) return this
        val correctOption = options.getOrElse(correctIndex) { options[0] }
        val shuffled = options.shuffled()
        val newCorrectIndex = shuffled.indexOf(correctOption)
        return this.copy(
            options = shuffled,
            correctIndex = if (newCorrectIndex != -1) newCorrectIndex else 0
        )
    }
}

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val languageBadge: String,
    val streakDays: Int,
    val xp: Int,
    val isCurrentUser: Boolean = false,
    val avatarUrl: String = "",
    val level: Int = 1,
    val id: String = ""
)

data class UserProgress(
    val selectedLanguage: ProgrammingLanguage = ProgrammingLanguage.PYTHON,
    val xp: Int = 0,
    val streakDays: Int = 0,
    val gems: Int = 50,
    val completedChallengeIds: Set<String> = emptySet(),
    val dailyGoalCompleted: Int = 0,
    val dailyGoalTarget: Int = 5,
    val lastActiveDate: String = "",
    val userName: String = "",
    val isDarkMode: Boolean = true,
    val isLoggedIn: Boolean = false,
    val googleId: String = "",
    val userEmail: String = "",
    val userAvatarUrl: String = "",
    val cloudRank: Int = 0,
    val googleClientId: String = ""
) {
    val level: Int get() = (xp / 200) + 1
    val xpForNextLevel: Int get() = 200
    val xpIntoLevel: Int get() = xp % 200
    val progressToNextLevel: Float
        get() = xpIntoLevel.toFloat() / xpForNextLevel.toFloat()
    val rankTitle: String
        get() = when {
            level < 3  -> "Novice Coder"
            level < 5  -> "Syntax Explorer"
            level < 8  -> "Logic Architect"
            level < 12 -> "Algorithm Master"
            else       -> "Grandmaster Dev"
        }
    val totalCompleted: Int get() = completedChallengeIds.size
}

data class AiQuestionCache(
    val questions: List<ChallengeQuestion> = emptyList(),
    val dateGenerated: String = ""
)

data class PathMilestone(
    val id: String,
    val tier: String, // "Beginner", "Intermediate", "Advanced", "Expert"
    val title: String,
    val description: String,
    val lessonsCount: Int,
    val isCompleted: Boolean = false,
    val isUnlocked: Boolean = false
)

data class LearningPath(
    val id: String,
    val name: String, // "Web Dev", "Python", "DSA", "AI/ML"
    val description: String,
    val milestones: List<PathMilestone> = emptyList()
)

data class RecommendedCourse(
    val id: String,
    val title: String,
    val level: String,
    val duration: String,
    val progress: Int, // 0 to 100
    val category: String,
    val rating: Float = 4.9f
)

data class LearnerBadge(
    val id: String,
    val name: String,
    val tier: String,
    val description: String,
    val isUnlocked: Boolean = false
)

data class HomeNotification(
    val id: String,
    val title: String,
    val description: String,
    val timeAgo: String,
    val isUnread: Boolean = true
)

