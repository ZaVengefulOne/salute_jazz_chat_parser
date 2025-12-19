package org.vengeful.salute_chat_parser.model

import kotlinx.datetime.LocalDateTime

data class AttendanceRecord(
    val studentName: String,
    val timestamp: LocalDateTime,
    val message: String
) {
    val normalizedName: String
        get() = studentName.lowercase().trim().replace(Regex("\\s+"), " ")
}

