package com.codequest.learn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codequest.learn.ui.screens.CodeQuestApp
import com.codequest.learn.ui.theme.CodeQuestTheme
import com.codequest.learn.viewmodel.CodeQuestViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: CodeQuestViewModel = viewModel()
            val progress by vm.progress.collectAsState(initial = com.codequest.learn.model.UserProgress())
            CodeQuestTheme(darkTheme = progress.isDarkMode) {
                CodeQuestApp(viewModel = vm)
            }
        }
    }
}
