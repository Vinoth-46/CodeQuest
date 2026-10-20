package com.codequest.learn.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codequest.learn.model.*
import com.codequest.learn.ui.theme.*

@Composable
fun HomeScreen(
    progress: UserProgress,
    apiKey: String,
    hasAiToday: Boolean,
    isGeneratingAi: Boolean,
    generationMessage: String?,
    selectedTier: SkillTier?,
    onSelectTier: (SkillTier?) -> Unit,
    onSelectMode: (ChallengeMode) -> Unit,
    onGenerateAi: (SkillTier?) -> Unit,
    onOpenSettings: () -> Unit,
    onClearMessage: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCourses = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            com.codequest.learn.data.LearningCatalog.recommendedCourses
        } else {
            com.codequest.learn.data.LearningCatalog.recommendedCourses.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true) ||
                it.level.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp)
    ) {
        // ── 1. Header: Greeting, Streak Badge & Notification Bell ──
        item {
            com.codequest.learn.ui.components.HomeHeader(progress = progress)
        }

        // ── 2. Search Bar ──
        item {
            com.codequest.learn.ui.components.HomeSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it }
            )
        }

        // ── 3. Continue Learning Card (Resume / Start Here) ──
        item {
            com.codequest.learn.ui.components.ContinueLearningCard(
                progress = progress,
                onResume = { onSelectMode(ChallengeMode.DAILY_QUIZ) },
                onStartHere = { onSelectMode(ChallengeMode.DAILY_QUIZ) }
            )
        }

        // ── 4. Daily Challenge Card (Coding task, progress ring, Start button) ──
        item {
            com.codequest.learn.ui.components.DailyChallengeCard(
                onStartChallenge = { onSelectMode(ChallengeMode.LOGIC_BUILDER) }
            )
        }

        // ── 5. Learning Paths Roadmap (Beginner → Intermediate → Advanced → Expert) ──
        item {
            com.codequest.learn.ui.components.LearningPathsRoadmap(
                onSelectMilestone = { _, _ -> onSelectMode(ChallengeMode.MISSING_WORDS) }
            )
        }

        // ── 6. Recommended For You (Horizontal Scroll Courses) ──
        item {
            com.codequest.learn.ui.components.RecommendedCoursesRow(
                courses = filteredCourses,
                onSelectCourse = { onSelectMode(ChallengeMode.GUESS_OUTPUT) }
            )
        }

        // ── 7. Progress Snapshot (XP, Level, Badges, Completed Lessons) ──
        item {
            com.codequest.learn.ui.components.ProgressSnapshotCard(progress = progress)
        }


        // ── Skill Tier Filter Selector ──
        item {
            SkillTierFilterRow(
                selectedTier = selectedTier,
                onSelectTier = onSelectTier
            )
        }

        // ── 4 Interactive Challenge Modes ──
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${progress.selectedLanguage.displayName} Challenge Modes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "4 interactive modes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(ChallengeMode.entries) { mode ->
            ModeCardItem(
                mode = mode,
                language = progress.selectedLanguage,
                onClick = { onSelectMode(mode) }
            )
        }
    }
}


@Composable
private fun UserLevelCard(progress: UserProgress) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.progressToNextLevel,
        label = "levelProgress"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Blue500.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "LEVEL ${progress.level}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                ),
                                color = Blue400,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = progress.rankTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "${progress.totalCompleted} solved",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral400
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // XP Level Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Level Progress",
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral400
                )
                Text(
                    text = "${progress.xpIntoLevel} / ${progress.xpForNextLevel} XP",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = Green400
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Green400,
                trackColor = Neutral800
            )
        }
    }
}

@Composable
private fun AiDailyQuestBanner(
    apiKey: String,
    hasAiToday: Boolean,
    isGeneratingAi: Boolean,
    generationMessage: String?,
    selectedTier: SkillTier? = null,
    onGenerateAi: () -> Unit,
    onOpenSettings: () -> Unit,
    onClearMessage: () -> Unit
) {
    val isKeyEmpty = apiKey.isBlank()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (hasAiToday) Purple500.copy(alpha = 0.4f) else Blue500.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Blue500.copy(alpha = 0.2f), Purple500.copy(alpha = 0.2f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = Purple400,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (selectedTier != null) "${selectedTier.title} AI Quests" else "Daily AI Quests",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when {
                            isKeyEmpty -> "Connect Gemini to get infinite fresh challenges"
                            selectedTier != null -> "Fetch fresh AI challenges for ${selectedTier.title} level"
                            hasAiToday -> "Fresh AI challenges loaded for Beginner, Intermediate & Expert"
                            else -> "Generate non-repetitive challenges for Beginner, Intermediate & Expert"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (hasAiToday) {
                    Surface(
                        color = Green500.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(1.dp, Green500.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "Active",
                            color = Green400,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isKeyEmpty) {
                    Button(
                        onClick = onOpenSettings,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue500),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Free Gemini API Key", fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Button(
                        onClick = onGenerateAi,
                        enabled = !isGeneratingAi,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasAiToday && selectedTier == null) Neutral800 else Blue500
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isGeneratingAi) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Neutral100,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating...", style = MaterialTheme.typography.labelMedium)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when {
                                    selectedTier != null -> "Generate ${selectedTier.title} AI Quests"
                                    hasAiToday -> "Refresh All AI Quests"
                                    else -> "Generate Today's AI Quests"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = generationMessage != null) {
                generationMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Neutral800,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (msg.startsWith("Success") || msg.startsWith("API")) Green400 else Yellow400,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = onClearMessage,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Neutral400, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyGoalCard(progress: UserProgress) {
    val completedRatio = (progress.dailyGoalCompleted.toFloat() / progress.dailyGoalTarget.toFloat()).coerceIn(0f, 1f)
    val animatedRatio by animateFloatAsState(targetValue = completedRatio, label = "goalProgress")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Orange500.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Orange400,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Daily Challenge Goal",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${progress.dailyGoalCompleted}/${progress.dailyGoalTarget}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (completedRatio >= 1f) Green400 else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { animatedRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (completedRatio >= 1f) Green400 else Orange400,
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun SkillTierFilterRow(
    selectedTier: SkillTier?,
    onSelectTier: (SkillTier?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedTier == null,
            onClick = { onSelectTier(null) },
            label = { Text("All Tiers", style = MaterialTheme.typography.labelSmall) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Blue500.copy(alpha = 0.2f),
                selectedLabelColor = Blue400,
                containerColor = Neutral900,
                labelColor = Neutral300
            ),
            border = FilterChipDefaults.filterChipBorder(
                borderColor = if (selectedTier == null) Blue400 else Neutral800,
                enabled = true,
                selected = selectedTier == null
            )
        )

        SkillTier.entries.forEach { tier ->
            val tierColor = when (tier) {
                SkillTier.BEGINNER -> Green400
                SkillTier.INTERMEDIATE -> Yellow400
                SkillTier.EXPERT -> Red400
            }
            FilterChip(
                selected = selectedTier == tier,
                onClick = { onSelectTier(if (selectedTier == tier) null else tier) },
                label = { Text(tier.title, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = tierColor.copy(alpha = 0.2f),
                    selectedLabelColor = tierColor,
                    containerColor = Neutral900,
                    labelColor = Neutral300
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = if (selectedTier == tier) tierColor else Neutral800,
                    enabled = true,
                    selected = selectedTier == tier
                )
            )
        }
    }
}

@Composable
private fun ModeCardItem(
    mode: ChallengeMode,
    language: ProgrammingLanguage,
    onClick: () -> Unit
) {
    val (icon, accentColor, tag) = when (mode) {
        ChallengeMode.DAILY_QUIZ -> Triple(Icons.Default.Quiz, Blue400, "QUIZ")
        ChallengeMode.MISSING_WORDS -> Triple(Icons.Default.EditNote, Green400, "FILL")
        ChallengeMode.GUESS_OUTPUT -> Triple(Icons.Default.Terminal, Purple400, "PREDICT")
        ChallengeMode.LOGIC_BUILDER -> Triple(Icons.Default.AccountTree, Yellow400, "ALGORITHM")
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.12f))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = mode.label,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${language.tag} • $tag",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mode.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Neutral500,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
