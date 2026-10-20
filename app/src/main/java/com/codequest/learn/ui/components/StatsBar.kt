package com.codequest.learn.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codequest.learn.model.ProgrammingLanguage
import com.codequest.learn.model.UserProgress
import com.codequest.learn.ui.theme.*

@Composable
fun TopStatsBar(
    progress: UserProgress,
    onLanguageToggle: () -> Unit,
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language Switcher Chip
                LanguagePill(
                    language = progress.selectedLanguage,
                    onClick = onLanguageToggle
                )

                // Stats Badges (Streak, XP, Gems)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatPill(
                        icon = Icons.Default.LocalFireDepartment,
                        label = "${progress.streakDays}d",
                        iconColor = Orange400,
                        backgroundColor = Orange500.copy(alpha = 0.12f),
                        borderColor = Orange500.copy(alpha = 0.3f)
                    )

                    StatPill(
                        icon = Icons.Default.Bolt,
                        label = "${progress.xp}",
                        iconColor = Green400,
                        backgroundColor = Green500.copy(alpha = 0.12f),
                        borderColor = Green500.copy(alpha = 0.3f)
                    )

                    StatPill(
                        icon = Icons.Default.Diamond,
                        label = "${progress.gems}",
                        iconColor = Purple400,
                        backgroundColor = Purple500.copy(alpha = 0.12f),
                        borderColor = Purple500.copy(alpha = 0.3f)
                    )
                }
            }

            // Subtle bottom border line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Neutral800)
            )
        }
    }
}

@Composable
private fun LanguagePill(
    language: ProgrammingLanguage,
    onClick: () -> Unit
) {
    val langColor by animateColorAsState(
        targetValue = if (language == ProgrammingLanguage.PYTHON) Blue400 else Yellow400,
        label = "langColor"
    )

    Surface(
        color = Neutral800,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Neutral700, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(langColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = language.displayName,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = Neutral100
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Toggle language",
                tint = Neutral400,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun StatPill(
    icon: ImageVector,
    label: String,
    iconColor: Color,
    backgroundColor: Color,
    borderColor: Color
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.border(1.dp, borderColor, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                ),
                color = Neutral100
            )
        }
    }
}
