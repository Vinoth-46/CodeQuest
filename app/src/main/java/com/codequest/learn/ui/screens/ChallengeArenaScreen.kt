package com.codequest.learn.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codequest.learn.model.*
import com.codequest.learn.ui.components.CodeBlock
import com.codequest.learn.ui.theme.*

@Composable
fun ChallengeArenaScreen(
    questions: List<ChallengeQuestion>,
    activeIndex: Int,
    hintUnlocked: Boolean,
    gemBalance: Int,
    selectedMode: ChallengeMode?,
    onUnlockHint: () -> Boolean,
    onSolved: (ChallengeQuestion) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClearModeFilter: () -> Unit,
    onGenerateAi: () -> Unit
) {
    if (questions.isEmpty()) {
        EmptyArenaState(
            selectedMode = selectedMode,
            onClearFilter = onClearModeFilter,
            onGenerateAi = onGenerateAi
        )
        return
    }

    val currentIdx = activeIndex.coerceIn(0, questions.size - 1)
    val question = questions[currentIdx]

    var showHintDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // ── 1. Top Navigation & Counter Row ──
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onPrevious,
                        enabled = questions.size > 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = "Previous",
                            tint = if (questions.size > 1) Neutral300 else Neutral600,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Question ${currentIdx + 1} of ${questions.size}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Neutral300
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onNext,
                        enabled = questions.size > 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowForwardIos,
                            contentDescription = "Next",
                            tint = if (questions.size > 1) Neutral300 else Neutral600,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Tier & AI Badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (question.isFromAi) {
                        Surface(
                            color = Purple500.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.border(1.dp, Purple500.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Purple400, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("AI Quest", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = Purple400)
                            }
                        }
                    }

                    val tierColor = when (question.tier) {
                        SkillTier.BEGINNER -> Green400
                        SkillTier.INTERMEDIATE -> Yellow400
                        SkillTier.EXPERT -> Red400
                    }
                    Surface(
                        color = tierColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(1.dp, tierColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "${question.tier.title} • +${question.tier.xpReward} XP",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = tierColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // ── 2. Title & Prompt ──
        item {
            Column {
                Text(
                    text = question.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Neutral100
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = question.prompt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral300
                )
            }
        }

        // ── 3. Code Snippet ──
        if (question.codeSnippet.isNotBlank()) {
            item {
                CodeBlock(
                    code = question.codeSnippet,
                    language = question.language,
                    highlightBlanks = question.mode == ChallengeMode.MISSING_WORDS
                )
            }
        }

        // ── 4. Hint Section ──
        item {
            if (hintUnlocked) {
                Surface(
                    color = Yellow500.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Yellow500.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Hint",
                            tint = Yellow400,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Hint",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Yellow400
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = question.hint,
                                style = MaterialTheme.typography.bodySmall,
                                color = Neutral200
                            )
                        }
                    }
                }
            } else {
                Surface(
                    color = Neutral900,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Neutral800, RoundedCornerShape(10.dp))
                        .clickable { showHintDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Neutral400, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Need a hint?", style = MaterialTheme.typography.bodySmall, color = Neutral300)
                        }
                        Text(
                            text = "Unlock (15 💎)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Purple400
                        )
                    }
                }
            }
        }

        // ── 5. Mode-Specific Interactive Body ──
        item {
            key(question.id) {
                when (question.mode) {
                    ChallengeMode.DAILY_QUIZ, ChallengeMode.GUESS_OUTPUT -> {
                        QuizAndPredictionView(
                            question = question,
                            onSolved = onSolved,
                            onNext = onNext
                        )
                    }
                    ChallengeMode.MISSING_WORDS -> {
                        MissingWordsInteractiveView(
                            question = question,
                            onSolved = onSolved,
                            onNext = onNext
                        )
                    }
                    ChallengeMode.LOGIC_BUILDER -> {
                        LogicBuilderInteractiveView(
                            question = question,
                            onSolved = onSolved,
                            onNext = onNext
                        )
                    }
                }
            }
        }
    }

    // Hint confirmation dialog
    if (showHintDialog) {
        AlertDialog(
            onDismissRequest = { showHintDialog = false },
            title = { Text("Unlock Hint?") },
            text = {
                Text(
                    if (gemBalance >= 15)
                        "Spend 15 gems to reveal a helpful hint for this challenge? You currently have $gemBalance gems."
                    else
                        "You need 15 gems to unlock a hint, but you only have $gemBalance gems. Solve challenges to earn more!"
                )
            },
            confirmButton = {
                if (gemBalance >= 15) {
                    Button(
                        onClick = {
                            onUnlockHint()
                            showHintDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple500)
                    ) {
                        Text("Unlock (15 💎)")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showHintDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Neutral900
        )
    }
}

// ── Interactive View: Multiple Choice & Guess Output ──

@Composable
private fun QuizAndPredictionView(
    question: ChallengeQuestion,
    onSolved: (ChallengeQuestion) -> Unit,
    onNext: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var hasSubmitted by remember { mutableStateOf(false) }

    val isCorrect = selectedOption == question.correctIndex

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val optionLetters = listOf("A", "B", "C", "D")

        question.options.forEachIndexed { idx, opt ->
            val isThisSelected = selectedOption == idx
            val isThisCorrectAnswer = idx == question.correctIndex

            val (bgColor, borderColor) = when {
                !hasSubmitted && isThisSelected -> Pair(Blue500.copy(alpha = 0.15f), Blue400)
                hasSubmitted && isThisCorrectAnswer -> Pair(Green500.copy(alpha = 0.15f), Green400)
                hasSubmitted && isThisSelected && !isCorrect -> Pair(Red500.copy(alpha = 0.15f), Red400)
                else -> Pair(Neutral900, Neutral800)
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .clickable(enabled = !hasSubmitted) {
                        selectedOption = idx
                        hasSubmitted = true
                        if (idx == question.correctIndex) {
                            onSolved(question)
                        }
                    },
                color = bgColor
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isThisSelected) Blue500.copy(alpha = 0.2f) else Neutral800),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = optionLetters.getOrElse(idx) { "${idx + 1}" },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isThisSelected) Blue400 else Neutral400
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = opt,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = if (question.mode == ChallengeMode.GUESS_OUTPUT) FontFamily.Monospace else FontFamily.Default
                        ),
                        color = Neutral100,
                        modifier = Modifier.weight(1f)
                    )

                    if (hasSubmitted) {
                        if (isThisCorrectAnswer) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = Green400, modifier = Modifier.size(20.dp))
                        } else if (isThisSelected) {
                            Icon(Icons.Default.Cancel, contentDescription = "Incorrect", tint = Red400, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }

        // Feedback explanation banner
        AnimatedVisibility(visible = hasSubmitted) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp,
                        if (isCorrect) Green500.copy(alpha = 0.3f) else Red500.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    ),
                color = if (isCorrect) Green500.copy(alpha = 0.1f) else Red500.copy(alpha = 0.1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isCorrect) Green400 else Red400,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCorrect) "Spot on! That's correct." else "Not quite right.",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isCorrect) Green400 else Red400
                        )
                    }

                    if (question.explanation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = question.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = Neutral200
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onNext,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCorrect) Green500 else Blue500
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isCorrect) "Continue to Next Challenge →" else "Next Challenge →",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ── Interactive View: Missing Words (Fill in the blanks) ──

@Composable
private fun MissingWordsInteractiveView(
    question: ChallengeQuestion,
    onSolved: (ChallengeQuestion) -> Unit,
    onNext: () -> Unit
) {
    val totalBlanks = question.missingBlanks.size.coerceAtLeast(1)
    var placedWords by remember { mutableStateOf(List(totalBlanks) { "" }) }
    var availableBank by remember { mutableStateOf(question.wordBank.ifEmpty { question.missingBlanks }) }
    var hasSubmitted by remember { mutableStateOf(false) }
    var isAllCorrect by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Fill the blanks below:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Neutral100
        )

        // Blank slots
        placedWords.forEachIndexed { idx, word ->
            val correctWord = question.missingBlanks.getOrNull(idx) ?: ""
            val isSlotCorrect = hasSubmitted && word.trim().equals(correctWord.trim(), ignoreCase = false)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (hasSubmitted) (if (isSlotCorrect) Green400 else Red400) else Neutral700,
                        RoundedCornerShape(10.dp)
                    ),
                color = Neutral900
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Blank #${idx + 1}:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Neutral400
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = word.ifEmpty { "Tap a word below..." },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (word.isEmpty()) Neutral500 else Neutral100
                        )
                    }

                    if (word.isNotEmpty() && !hasSubmitted) {
                        IconButton(
                            onClick = {
                                // Return word to bank
                                availableBank = availableBank + word
                                val updated = placedWords.toMutableList()
                                updated[idx] = ""
                                placedWords = updated
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Neutral400, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Word Bank
        if (!hasSubmitted) {
            Column {
                Text(
                    text = "Word Bank (Tap to insert):",
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral400
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableBank.forEach { bankWord ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Blue500.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable {
                                    // Place in first empty blank
                                    val firstEmpty = placedWords.indexOfFirst { it.isEmpty() }
                                    if (firstEmpty != -1) {
                                        val updated = placedWords.toMutableList()
                                        updated[firstEmpty] = bankWord
                                        placedWords = updated
                                        availableBank = availableBank.toMutableList().also { it.remove(bankWord) }
                                    }
                                },
                            color = Neutral800
                        ) {
                            Text(
                                text = bankWord,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Blue400,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Verify button
            val allFilled = placedWords.none { it.isEmpty() }
            Button(
                onClick = {
                    hasSubmitted = true
                    val correct = placedWords.indices.all { i ->
                        placedWords[i].trim().equals(question.missingBlanks.getOrNull(i)?.trim(), ignoreCase = false)
                    }
                    isAllCorrect = correct
                    if (correct) {
                        onSolved(question)
                    }
                },
                enabled = allFilled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green500)
            ) {
                Text("Check Answers", fontWeight = FontWeight.Bold)
            }
        }

        // Result and Next
        AnimatedVisibility(visible = hasSubmitted) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = if (isAllCorrect) Green500.copy(alpha = 0.1f) else Red500.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.border(
                        1.dp,
                        if (isAllCorrect) Green500.copy(alpha = 0.3f) else Red500.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isAllCorrect) "All blanks correctly filled!" else "Some blanks were incorrect.",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isAllCorrect) Green400 else Red400
                        )
                        if (question.explanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(question.explanation, style = MaterialTheme.typography.bodySmall, color = Neutral200)
                        }
                    }
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAllCorrect) Green500 else Blue500
                    )
                ) {
                    Text("Next Challenge →", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ── Interactive View: Logic Builder (Reorder Steps) ──

@Composable
private fun LogicBuilderInteractiveView(
    question: ChallengeQuestion,
    onSolved: (ChallengeQuestion) -> Unit,
    onNext: () -> Unit
) {
    // Initial shuffled state
    var steps by remember {
        mutableStateOf(
            if (question.logicStepsCorrectOrder.size > 1) {
                question.logicStepsCorrectOrder.shuffled()
            } else {
                question.logicStepsCorrectOrder
            }
        )
    }
    var hasVerified by remember { mutableStateOf(false) }
    var isCorrectOrder by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Arrange lines in correct logical order:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Neutral100
        )

        steps.forEachIndexed { idx, step ->
            val isStepInCorrectPlace = hasVerified && (step == question.logicStepsCorrectOrder.getOrNull(idx))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (hasVerified) (if (isStepInCorrectPlace) Green400 else Red400) else Neutral800,
                        RoundedCornerShape(10.dp)
                    ),
                color = Neutral900
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${idx + 1}.",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Neutral400
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Neutral100,
                        modifier = Modifier.weight(1f)
                    )

                    if (!hasVerified) {
                        IconButton(
                            onClick = {
                                if (idx > 0) {
                                    val list = steps.toMutableList()
                                    val temp = list[idx]
                                    list[idx] = list[idx - 1]
                                    list[idx - 1] = temp
                                    steps = list
                                }
                            },
                            enabled = idx > 0,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up", tint = if (idx > 0) Neutral300 else Neutral700)
                        }

                        IconButton(
                            onClick = {
                                if (idx < steps.size - 1) {
                                    val list = steps.toMutableList()
                                    val temp = list[idx]
                                    list[idx] = list[idx + 1]
                                    list[idx + 1] = temp
                                    steps = list
                                }
                            },
                            enabled = idx < steps.size - 1,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down", tint = if (idx < steps.size - 1) Neutral300 else Neutral700)
                        }
                    }
                }
            }
        }

        if (!hasVerified) {
            Button(
                onClick = {
                    hasVerified = true
                    val correct = steps == question.logicStepsCorrectOrder
                    isCorrectOrder = correct
                    if (correct) {
                        onSolved(question)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Yellow500)
            ) {
                Text("Verify Logic Order", fontWeight = FontWeight.Bold, color = Neutral950)
            }
        }

        AnimatedVisibility(visible = hasVerified) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = if (isCorrectOrder) Green500.copy(alpha = 0.1f) else Red500.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.border(
                        1.dp,
                        if (isCorrectOrder) Green500.copy(alpha = 0.3f) else Red500.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isCorrectOrder) "Algorithm steps perfectly arranged!" else "Some steps are out of sequence.",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isCorrectOrder) Green400 else Red400
                        )
                        if (question.explanation.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(question.explanation, style = MaterialTheme.typography.bodySmall, color = Neutral200)
                        }
                    }
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCorrectOrder) Green500 else Blue500
                    )
                ) {
                    Text("Next Challenge →", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ── Empty State ──

@Composable
private fun EmptyArenaState(
    selectedMode: ChallengeMode?,
    onClearFilter: () -> Unit,
    onGenerateAi: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Neutral800),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.TaskAlt, contentDescription = null, tint = Green400, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "All Caught Up!",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Neutral100
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (selectedMode != null)
                    "You've completed all challenges in ${selectedMode.label}. Reset the filter or generate fresh AI challenges."
                else
                    "You've completed all available challenges! Generate fresh daily AI questions to keep practicing.",
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral400,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (selectedMode != null) {
                OutlinedButton(
                    onClick = onClearFilter,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Show All Modes")
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            Button(
                onClick = onGenerateAi,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Blue500)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Daily AI Challenges")
            }
        }
    }
}
