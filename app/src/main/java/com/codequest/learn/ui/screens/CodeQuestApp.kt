package com.codequest.learn.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codequest.learn.model.ProgrammingLanguage
import com.codequest.learn.ui.components.TopStatsBar
import com.codequest.learn.ui.theme.*
import com.codequest.learn.viewmodel.CodeQuestViewModel
import kotlinx.coroutines.delay

enum class NavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    LEARN("Learn", Icons.Filled.School, Icons.Outlined.School),
    PRACTICE("Practice", Icons.Filled.Code, Icons.Outlined.Code),
    LEADERBOARD("Leaderboard", Icons.Filled.Leaderboard, Icons.Outlined.Leaderboard),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun CodeQuestApp(viewModel: CodeQuestViewModel) {
    val progress by viewModel.progress.collectAsState()
    val apiKey by viewModel.apiKey.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()
    val selectedTier by viewModel.selectedTier.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val activeIdx by viewModel.activeQuestionIndex.collectAsState()
    val hintUnlocked by viewModel.hintUnlocked.collectAsState()
    val isGeneratingAi by viewModel.isGeneratingAi.collectAsState()
    val generationMessage by viewModel.generationMessage.collectAsState()
    val hasAiToday by viewModel.hasAiQuestionsToday.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()
    val availableUpdate by viewModel.availableUpdate.collectAsState()
    val isDownloadingUpdate by viewModel.isDownloadingUpdate.collectAsState()
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
    var showFlashScreen by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1200) // Smooth branded flash splash reveal
        showFlashScreen = false
    }

    if (showFlashScreen) {
        FlashSplashScreen()
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                if (currentTab != NavigationTab.HOME) {
                    TopStatsBar(
                        progress = progress,
                        onLanguageToggle = {
                            val next = if (progress.selectedLanguage == ProgrammingLanguage.PYTHON)
                                ProgrammingLanguage.JAVA else ProgrammingLanguage.PYTHON
                            viewModel.selectLanguage(next)
                        }
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                ) {
                    NavigationTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Blue400,
                                selectedTextColor = Blue400,
                                indicatorColor = Blue500.copy(alpha = 0.15f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            progress = progress,
                            apiKey = apiKey,
                            hasAiToday = hasAiToday,
                            isGeneratingAi = isGeneratingAi,
                            generationMessage = generationMessage,
                            selectedTier = selectedTier,
                            onSelectTier = { viewModel.selectTier(it) },
                            onSelectMode = { mode ->
                                viewModel.selectMode(mode)
                                currentTab = NavigationTab.PRACTICE
                            },
                            onGenerateAi = { tier -> viewModel.generateDailyAiQuestions(force = false, tier = tier) },
                            onOpenSettings = { currentTab = NavigationTab.PROFILE },
                            onClearMessage = { viewModel.clearGenerationMessage() }
                        )
                    }
                    NavigationTab.LEARN -> {
                        LearnCatalogScreen(
                            onSelectMode = { mode ->
                                viewModel.selectMode(mode)
                                currentTab = NavigationTab.PRACTICE
                            }
                        )
                    }
                    NavigationTab.PRACTICE -> {
                        ChallengeArenaScreen(
                            questions = questions,
                            activeIndex = activeIdx,
                            hintUnlocked = hintUnlocked,
                            gemBalance = progress.gems,
                            selectedMode = selectedMode,
                            onUnlockHint = { viewModel.unlockHint() },
                            onSolved = { q -> viewModel.recordCorrectAnswer(q) },
                            onNext = { viewModel.nextQuestion() },
                            onPrevious = { viewModel.previousQuestion() },
                            onClearModeFilter = { viewModel.selectMode(null) },
                            onGenerateAi = { viewModel.generateDailyAiQuestions(force = true, tier = selectedTier) }
                        )
                    }
                    NavigationTab.LEADERBOARD -> {
                        LeaderboardScreen(entries = leaderboard)
                    }
                    NavigationTab.PROFILE -> {
                        SettingsScreen(
                            apiKey = apiKey,
                            progress = progress,
                            onSaveApiKey = { viewModel.saveApiKey(it) },
                            onUpdateUserName = { viewModel.updateUserName(it) },
                            onToggleDarkMode = { viewModel.toggleDarkMode(it) },
                            onSetDailyGoal = { viewModel.setDailyGoalTarget(it) },
                            onResetProgress = { viewModel.resetProgress() },
                            onCheckForUpdates = { viewModel.checkForAppUpdates() }
                        )
                    }
                }
            }
        }

        // Play Store-style In-App Update Prompt
        availableUpdate?.let { updateInfo ->
            com.codequest.learn.ui.components.AppUpdateModal(
                updateInfo = updateInfo,
                downloadProgress = downloadProgress,
                isDownloading = isDownloadingUpdate,
                onUpdateClick = { viewModel.startAppUpdate(context) },
                onDismiss = { viewModel.dismissUpdatePrompt() }
            )
        }
    }
}

/**
 * Branded Flash Splash Screen shown briefly on initial cold launch
 */
@Composable
private fun FlashSplashScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "flashPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral950),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size((90 * scale).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Blue500.copy(alpha = 0.25f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Neutral900,
                    modifier = Modifier
                        .size(72.dp)
                        .border(1.5.dp, Blue400, RoundedCornerShape(22.dp))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = Blue400,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "CodeQuest",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    letterSpacing = 1.sp
                ),
                color = Neutral100
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Python & Java Mastery",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                color = Blue400
            )

            Spacer(modifier = Modifier.height(28.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Blue400,
                strokeWidth = 2.5.dp
            )
        }
    }
}

/**
 * Rich AI Generation Loading Overlay with spinning gradient aura & live status text
 */
@Composable
private fun AiGenerationLoadingOverlay(generationMessage: String?) {
    val infiniteTransition = rememberInfiniteTransition(label = "aiSpin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "spinAngle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral950.copy(alpha = 0.88f)),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Neutral900,
            modifier = Modifier
                .padding(32.dp)
                .border(1.dp, Purple500.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .rotate(rotation)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(Blue500, Purple500, Green400, Blue500)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Neutral900),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Loading",
                            tint = Purple400,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Crafting Fresh Quests",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Neutral100
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = generationMessage ?: "Consulting Gemini AI engine...",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral400,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LearnCatalogScreen(
    onSelectMode: (com.codequest.learn.model.ChallengeMode) -> Unit
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Full Learning Tracks",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Master modern programming across 4 specialized roadmaps.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item {
            com.codequest.learn.ui.components.LearningPathsRoadmap(
                onSelectMilestone = { _, _ -> onSelectMode(com.codequest.learn.model.ChallengeMode.MISSING_WORDS) }
            )
        }
        item {
            com.codequest.learn.ui.components.RecommendedCoursesRow(
                onSelectCourse = { onSelectMode(com.codequest.learn.model.ChallengeMode.GUESS_OUTPUT) }
            )
        }
    }
}

