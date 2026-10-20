package com.codequest.learn.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.codequest.learn.model.LeaderboardEntry
import com.codequest.learn.ui.theme.*

@Composable
fun LeaderboardScreen(
    entries: List<LeaderboardEntry>,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val topThree = entries.take(3)
    val rest = entries.drop(3)

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // ── Live Cloud & League Header ──
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Yellow500.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                color = Neutral900
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Yellow500.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "League",
                                    tint = Yellow400,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Diamond League",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Neutral100
                                )
                                Text(
                                    text = "Top 3 learners advance • Live Leaderboard",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Neutral400
                                )
                            }
                        }

                        IconButton(
                            onClick = onRefresh,
                            enabled = !isRefreshing
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = if (isRefreshing) Blue400 else Neutral300,
                                modifier = if (isRefreshing) Modifier.rotate(angle) else Modifier
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cloud MongoDB live status pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Green500.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Green500.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Green400)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Live MongoDB Atlas Cloud • Real Players",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Green400
                            )
                        }
                    }
                }
            }
        }

        // ── Top 3 Podium ──
        if (topThree.isNotEmpty()) {
            item {
                PodiumSection(topThree = topThree)
            }
        }

        // ── Rest of the Ranks ──
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Player Rankings (${entries.size})",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Neutral400
                )
                Text(
                    text = "Sorted by XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral500
                )
            }
        }

        items(rest) { entry ->
            RankRowItem(entry = entry)
        }
    }
}

@Composable
private fun PodiumSection(topThree: List<LeaderboardEntry>) {
    // Reorder for standard podium visual: 2nd, 1st, 3rd
    val first = topThree.getOrNull(0)
    val second = topThree.getOrNull(1)
    val third = topThree.getOrNull(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        if (second != null) {
            PodiumColumn(
                entry = second,
                height = 145.dp,
                medalColor = Color(0xFFC0C0C0), // Silver
                rankNumber = 2,
                modifier = Modifier.weight(1f)
            )
        }

        if (first != null) {
            PodiumColumn(
                entry = first,
                height = 175.dp,
                medalColor = Color(0xFFFFD700), // Gold
                rankNumber = 1,
                modifier = Modifier.weight(1.1f)
            )
        }

        if (third != null) {
            PodiumColumn(
                entry = third,
                height = 130.dp,
                medalColor = Color(0xFFCD7F32), // Bronze
                rankNumber = 3,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PodiumColumn(
    entry: LeaderboardEntry,
    height: androidx.compose.ui.unit.Dp,
    medalColor: Color,
    rankNumber: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (entry.isCurrentUser) Blue400 else Neutral800,
                RoundedCornerShape(14.dp)
            ),
        color = if (entry.isCurrentUser) Blue900.copy(alpha = 0.4f) else Neutral900
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Avatar or medal rank
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(medalColor.copy(alpha = 0.2f))
                    .border(1.5.dp, medalColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (entry.avatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = entry.avatarUrl,
                        contentDescription = entry.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        text = "$rankNumber",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = medalColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = entry.name,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (entry.isCurrentUser) Blue400 else Neutral100,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${entry.xp} XP",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Green400
            )

            if (entry.isCurrentUser) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Blue500,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "YOU",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.ExtraBold),
                        color = Neutral950,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RankRowItem(entry: LeaderboardEntry) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (entry.isCurrentUser) Blue400 else Neutral800,
                RoundedCornerShape(12.dp)
            ),
        color = if (entry.isCurrentUser) Blue900.copy(alpha = 0.3f) else Neutral900
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "#${entry.rank}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Neutral400,
                    modifier = Modifier.width(32.dp)
                )

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (entry.isCurrentUser) Blue500 else Neutral800),
                    contentAlignment = Alignment.Center
                ) {
                    if (entry.avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = entry.avatarUrl,
                            contentDescription = entry.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Text(
                            text = entry.name.firstOrNull()?.uppercase() ?: "U",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (entry.isCurrentUser) Neutral950 else Neutral200
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = entry.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = if (entry.isCurrentUser) Blue400 else Neutral100
                        )
                        if (entry.isCurrentUser) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = Blue500,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "YOU",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.ExtraBold),
                                    color = Neutral950,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = entry.languageBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral400
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = Orange400,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${entry.streakDays}d",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Orange400
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "XP",
                        tint = Green400,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${entry.xp}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Green400
                    )
                }
            }
        }
    }
}
