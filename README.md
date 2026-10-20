# CodeQuest — Production Android Programming App (Python & Java)

A production-grade, gamified programming education app built with **Kotlin**, **Jetpack Compose (Material 3)**, and **Google Gemini 2.0 Flash AI** to guide learners from **Beginner to Expert** in **Python** and **Java**.

---

## 🚀 Key Highlights & Architecture

### 1. 🤖 Daily AI-Powered Non-Repetitive Quests (Gemini 2.0 Flash)
- **Zero Repetition**: Generates fresh, unique programming challenges every single day across all difficulty tiers.
- **Structured JSON Engine**: Prompts Gemini 2.0 Flash with rigorous schemas and extracts valid questions across all 4 modes.
- **Offline Seed Bank**: Includes 28 curated offline fallback questions across Python and Java, so the app is 100% playable without an internet connection or before an API key is configured.
- **Smart Date Caching**: Automatically saves daily generated questions in **Jetpack DataStore** so they are generated once per day without redundant API calls.

### 2. 🎮 4 Interactive Challenge Modes
1. **Daily Quiz (`DAILY_QUIZ`)**: Concept mastery with 4 options, instant visual feedback, and in-depth explanations.
2. **Fill the Code (`MISSING_WORDS`)**: Interactive code completion with syntax-highlighted code blocks, blank slots, and a tap-to-place Word Bank chip row.
3. **Predict Output (`GUESS_OUTPUT`)**: Realistic terminal runner view (`terminal@codequest:~$ run`) testing execution tracing and edge-case prediction.
4. **Logic Builder (`LOGIC_BUILDER`)**: Reorder algorithm lines (Binary Search, Two-Pointer, Merge, Recursion) with Up/Down controls and verify the logical sequence.

### 3. 🎨 Production-Grade UI Design (Clean & Ad-Free)
- **Deep Muted Dark Theme**: Built with a refined dark palette (Obsidian `#0D1117`, Slate `#161B22`, `#21262D`) inspired by GitHub and Linear.
- **Built-in Syntax Highlighter**: Custom `CodeBlock` component with keyword highlighting (`def`, `class`, `public`, `static`), string literals, comments, numbers, and line numbers.
- **Edge-to-Edge**: Seamless edge-to-edge layout with Android system bars.
- **Tactile Gamification**:
  - Real calendar streak calculations (yesterday vs. today tracking).
  - XP progression system with Level Titles (Novice Coder ➔ Syntax Explorer ➔ Logic Architect ➔ Algorithm Master ➔ Grandmaster Dev).
  - Diamond League leaderboard with podium for top 3 learners and dynamic ranking that scales with user progress.
  - Hint unlocking with gems.

---

## 📂 Architecture Overview

```
com.codequest.learn/
├── MainActivity.kt                      # Edge-to-edge entry point
├── CodeQuestApplication.kt              # App singleton container
├── model/
│   └── Models.kt                        # Question, Mode, Tier, UserProgress, Leaderboard models
├── data/
│   ├── local/
│   │   └── UserPreferences.kt           # DataStore-backed progress, streak & cache persistence
│   ├── remote/
│   │   └── GeminiQuestionGenerator.kt   # Gemini 2.0 Flash AI question generator
│   ├── SeedQuestions.kt                 # Curated offline question bank (Python & Java)
│   └── QuestionRepository.kt            # Unified repository with offline fallback & caching
├── viewmodel/
│   └── CodeQuestViewModel.kt            # Reactive StateFlow ViewModel
└── ui/
    ├── components/
    │   ├── CodeBlock.kt                 # Syntax highlighter with line numbers & copy button
    │   └── StatsBar.kt                  # Language switcher, streak, XP, gems, and settings
    ├── screens/
    │   ├── CodeQuestApp.kt              # 4-tab bottom navigation & scaffold
    │   ├── HomeScreen.kt                # Level card, AI banner, goal progress, mode cards
    │   ├── ChallengeArenaScreen.kt      # Interactive runner for all 4 challenge modes
    │   ├── LeaderboardScreen.kt         # Diamond League podium & user rank highlight
    │   └── SettingsScreen.kt            # Gemini API Key setup, learner profile & stats
    └── theme/
        ├── Color.kt                     # Curated GitHub/Linear dark palette tokens
        ├── Type.kt                      # High-legibility typography & monospace styles
        └── Theme.kt                     # Material 3 dark color scheme
```

---

## 🛠️ How to Open & Run

1. Open **Android Studio (Ladybug, Koala, or newer)**.
2. Select **Open** and choose the `CodeQuestAndroid` directory.
3. Allow Gradle to sync dependencies (Android SDK 34, Kotlin 1.9.24, Compose 2024.06.00, Gradle 8.7).
4. Run on any Android Emulator or physical device (Android 7.0 / API 24+).
5. **(Optional)** Open the **Settings** tab in the app and paste your free Gemini API key from [Google AI Studio](https://ai.google.dev) to unlock infinite fresh daily AI quests!
