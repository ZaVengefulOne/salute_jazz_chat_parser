package org.vengeful.salute_chat_parser.parser

import kotlinx.datetime.LocalDateTime
import org.vengeful.salute_chat_parser.model.AttendanceRecord

object ChatLogParser {
    private val chatLineRegex = Regex(
        """(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) - ([^:]+): (.+)"""
    )

    /**
     * Парсит лог чата и извлекает записи о посещаемости
     */
    fun parseChatLog(content: String): List<AttendanceRecord> {
        val records = mutableListOf<AttendanceRecord>()
        val lines = content.lines()

        for (line in lines) {
            val record = parseLine(line) ?: continue
            val attendanceRecord = extractAttendanceRecord(record)
            if (attendanceRecord != null) {
                records.add(attendanceRecord)
            }
        }

        return records
    }

    /**
     * Парсит одну строку лога
     */
    private fun parseLine(line: String): AttendanceRecord? {
        val match = chatLineRegex.find(line) ?: return null

        val timestampStr = match.groupValues[1]
        val studentName = match.groupValues[2].trim()
        val message = match.groupValues[3].trim()

        val timestamp = parseTimestamp(timestampStr) ?: return null

        return AttendanceRecord(
            studentName = studentName,
            timestamp = timestamp,
            message = message
        )
    }

    /**
     * Извлекает запись о посещаемости, пытаясь определить имя студента из сообщения или отправителя
     */
    private fun extractAttendanceRecord(record: AttendanceRecord): AttendanceRecord? {
        val normalizedSender = record.studentName.lowercase().trim()

        if (normalizedSender.contains("РГГУ", ignoreCase = true)) {
            return null
        }

        val normalizedMessage = record.message.lowercase().trim()

        val nameFromMessage = extractNameFromMessage(record.message)
        if (nameFromMessage != null) {
            return record.copy(studentName = nameFromMessage)
        }
        if (isAttendanceMessage(record.studentName, record.message)) {
            return record
        }

        return null
    }

    /**
     * Пытается извлечь имя из сообщения
     * Имя должно содержать 2-4 слова (фамилия, имя, отчество)
     */
    private fun extractNameFromMessage(message: String): String? {
        val cleaned = message
            .replace(Regex("[*]+$"), "")
            .replace(Regex("\\s+тут\\s*$", RegexOption.IGNORE_CASE), "")
            .trim()

        val words = cleaned.split(Regex("\\s+")).filter { it.isNotBlank() }

        if (words.size !in 2..4) {
            return null
        }

        val validWords = words.filter { word ->
            word.length in 2..20 && word.all { it.isLetter() || it == '-' }
        }

        if (validWords.size >= 2) {
            return validWords.joinToString(" ")
        }

        return null
    }

    /**
     * Парсит timestamp в LocalDateTime
     */
    private fun parseTimestamp(timestampStr: String): LocalDateTime? {
        return try {
            val parts = timestampStr.split(" ")
            if (parts.size != 2) return null

            val dateParts = parts[0].split("-")
            val timeParts = parts[1].split(":")

            if (dateParts.size != 3 || timeParts.size != 3) return null

            LocalDateTime(
                year = dateParts[0].toInt(),
                monthNumber = dateParts[1].toInt(),
                dayOfMonth = dateParts[2].toInt(),
                hour = timeParts[0].toInt(),
                minute = timeParts[1].toInt(),
                second = timeParts[2].toInt()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Проверяет, является ли сообщение отметкой о присутствии
     * Сообщение считается отметкой, если оно содержит имя студента
     */
    private fun isAttendanceMessage(studentName: String, message: String): Boolean {
        val normalizedName = studentName.lowercase().trim()
        val normalizedMessage = message.lowercase().trim()

        val nameWords = normalizedName.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (nameWords.isEmpty()) return false
        val messageWords = normalizedMessage.split(Regex("\\s+")).filter { it.isNotBlank() }
        var matchCount = 0
        for (nameWord in nameWords) {
            if (messageWords.any { it.contains(nameWord) || nameWord.contains(it) }) {
                matchCount++
            }
        }
        return matchCount >= minOf(2, nameWords.size)
    }
}