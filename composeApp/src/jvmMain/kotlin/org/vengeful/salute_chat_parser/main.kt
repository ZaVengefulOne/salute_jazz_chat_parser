package org.vengeful.salute_chat_parser

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Парсер посещаемости SaluteJazz",
    ) {
        App()
    }
}