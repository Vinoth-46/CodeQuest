package com.codequest.learn.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.codequest.learn.model.UserProgress
import com.codequest.learn.ui.theme.*

@Composable
fun SettingsScreen(
    apiKey: String,
    progress: UserProgress,
    onSaveApiKey: (String) -> Unit,
    onUpdateUserName: (String) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit = {},
    onSetDailyGoal: (Int) -> Unit = {},
    onResetProgress: () -> Unit,
    onCheckForUpdates: () -> Unit = {},
    onSignInWithGoogle: (Activity) -> Unit = {},
    onSignInWithPlayerProfile: (name: String, email: String) -> Unit = { _, _ -> },
    onSignOut: () -> Unit = {},
    onSaveGoogleClientId: (String) -> Unit = {},
    authMessage: String? = null,
    isAuthenticating: Boolean = false,
    modifier: Modifier = Modifier
) {
    var nameInput by remember(progress.userName) { mutableStateOf(progress.userName) }
    var clientIdInput by remember(progress.googleClientId) { mutableStateOf(progress.googleClientId) }
    var showClientIdConfig by remember { mutableStateOf(false) }
    var showQuickLoginDialog by remember { mutableStateOf(false) }

    var updateCheckMessage by remember { mutableStateOf<String?>(null) }
    var isCheckingUpdate by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // ── 1. Cloud Leaderboard & Account (MongoDB Atlas) ──
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        if (progress.isLoggedIn) Green400.copy(alpha = 0.4f) else Blue400.copy(alpha = 0.3f),
                        RoundedCornerShape(16.dp)
                    ),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (progress.isLoggedIn) Green500.copy(alpha = 0.15f) else Blue500.copy(alpha = 0.15f))
                                .border(
                                    1.5.dp,
                                    if (progress.isLoggedIn) Green400 else Blue400,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (progress.userAvatarUrl.isNotBlank()) {
                                AsyncImage(
                                    model = progress.userAvatarUrl,
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = if (progress.isLoggedIn) Icons.Default.CloudDone else Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = if (progress.isLoggedIn) Green400 else Blue400,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (progress.isLoggedIn) (progress.userName.ifBlank { "Cloud Player" }) else "Live Cloud Account",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (progress.isLoggedIn) (progress.userEmail.ifBlank { "Connected to MongoDB Atlas" }) else "Sign in to join real players on the live leaderboard",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (progress.isLoggedIn) Green400 else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (progress.isLoggedIn) {
                        // Logged-in State
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Green500.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Green500.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
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
                                        text = "Live Sync Active • MongoDB Atlas",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Green400
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = onSignOut,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Red400),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Sign Out")
                            }
                        }
                    } else {
                        // Not logged in State
                        Button(
                            onClick = {
                                if (activity != null) {
                                    onSignInWithGoogle(activity)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Blue500),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isAuthenticating
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAuthenticating) "Connecting to Google..." else "Sign in with Google",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { showQuickLoginDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Join as Challenger (Quick Profile)")
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Advanced Client ID toggle
                        TextButton(
                            onClick = { showClientIdConfig = !showClientIdConfig },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = if (showClientIdConfig) "Hide OAuth Client ID" else "Configure Google Web Client ID",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        AnimatedVisibility(visible = showClientIdConfig) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                OutlinedTextField(
                                    value = clientIdInput,
                                    onValueChange = { clientIdInput = it },
                                    label = { Text("Google Web Client ID") },
                                    placeholder = { Text("xxxxxx.apps.googleusercontent.com") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { onSaveGoogleClientId(clientIdInput) },
                                    modifier = Modifier.align(Alignment.End),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Save Client ID")
                                }
                            }
                        }
                    }

                    authMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (msg.contains("failed", ignoreCase = true) || msg.contains("error", ignoreCase = true)) Red500.copy(alpha = 0.12f) else Green500.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (msg.contains("failed", ignoreCase = true) || msg.contains("error", ignoreCase = true)) Red400 else Green400,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── 2. Learner Profile Card ──
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(1.dp, Blue400.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Blue400,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = progress.userName.ifBlank { "Novice Coder" },
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Level ${progress.level} • ${progress.rankTitle}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Blue400
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Display Name") },
                        placeholder = { Text("Enter your name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { onUpdateUserName(nameInput) },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue500),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Name")
                    }
                }
            }
        }

        // ── 3. Appearance & Theme (Dark / Light Mode) ──
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (progress.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (progress.isDarkMode) Purple400 else Orange400,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Appearance",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (progress.isDarkMode) "Dark Theme Active" else "Light Theme Active",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = progress.isDarkMode,
                            onCheckedChange = { onToggleDarkMode(it) },
                            thumbContent = {
                                Icon(
                                    imageVector = if (progress.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = progress.isDarkMode,
                            onClick = { onToggleDarkMode(true) },
                            label = { Text("🌙 Dark Mode") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = !progress.isDarkMode,
                            onClick = { onToggleDarkMode(false) },
                            label = { Text("☀️ Light Mode") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // ── 4. Daily Learning Goal ──
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = Green400,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Daily Challenge Goal",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Currently: ${progress.dailyGoalTarget} challenges per day",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(3, 5, 10, 15).forEach { target ->
                            val isSelected = progress.dailyGoalTarget == target
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { onSetDailyGoal(target) }
                                    .border(
                                        1.5.dp,
                                        if (isSelected) Blue500 else MaterialTheme.colorScheme.outlineVariant,
                                        RoundedCornerShape(10.dp)
                                    ),
                                color = if (isSelected) Blue500.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$target",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Blue400 else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── 5. App Updates & Version ──
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = Blue400,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "CodeQuest v${com.codequest.learn.BuildConfig.VERSION_NAME}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "OTA Updates Enabled",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                isCheckingUpdate = true
                                onCheckForUpdates()
                                updateCheckMessage = "✅ You're on the latest version! (v${com.codequest.learn.BuildConfig.VERSION_NAME})"
                                isCheckingUpdate = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Blue500),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Check",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    updateCheckMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Green500.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Green500.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Green400,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── 6. Reset Progress Card ──
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Red500.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reset Progress",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Red400
                        )
                        Text(
                            text = "Clear XP, level, and solved history",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Red400),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reset")
                    }
                }
            }
        }
    }

    if (showQuickLoginDialog) {
        var quickName by remember { mutableStateOf(progress.userName.ifBlank { "" }) }
        var quickEmail by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showQuickLoginDialog = false },
            title = { Text("Join Live Leaderboard") },
            text = {
                Column {
                    Text(
                        text = "Enter your display name and email to save your place on the MongoDB Atlas live leaderboard with other real challengers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = quickName,
                        onValueChange = { quickName = it },
                        label = { Text("Display Name / Nickname") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = quickEmail,
                        onValueChange = { quickEmail = it },
                        label = { Text("Email (for avatar & ranking)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (quickName.isNotBlank() && quickEmail.isNotBlank()) {
                            onSignInWithPlayerProfile(quickName, quickEmail)
                            showQuickLoginDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Blue500)
                ) {
                    Text("Join Leaderboard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuickLoginDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Progress?") },
            text = { Text("Are you sure? This will wipe your completed challenges, streaks, and XP back to zero.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Red500)
                ) {
                    Text("Reset Everything")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
