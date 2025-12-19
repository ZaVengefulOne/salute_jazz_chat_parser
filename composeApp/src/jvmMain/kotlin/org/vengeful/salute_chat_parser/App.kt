package org.vengeful.salute_chat_parser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.vengeful.salute_chat_parser.ui.MainScreen
import org.vengeful.salute_chat_parser.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
            MainScreen()
        }
    }
}