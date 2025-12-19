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
            // Проверяем, что сообщение содержит имя студента (отметка о присутствии)
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

        // Игнорируем сообщения от admin
        if (normalizedSender.contains("admin", ignoreCase = true)) {
            return null
        }

        val normalizedMessage = record.message.lowercase().trim()

        // Пытаемся извлечь имя из сообщения
        val nameFromMessage = extractNameFromMessage(record.message)

        // Если в сообщении найдено имя (2-3 слова, похожие на ФИО)
        if (nameFromMessage != null) {
            return record.copy(studentName = nameFromMessage)
        }

        // Иначе проверяем, содержит ли сообщение имя отправителя
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
        // Удаляем лишние символы в конце (звездочки, "тут" и т.д.)
        val cleaned = message
            .replace(Regex("[*]+$"), "") // Удаляем звездочки в конце
            .replace(Regex("\\s+тут\\s*$", RegexOption.IGNORE_CASE), "") // Удаляем "тут" в конце
            .trim()

        val words = cleaned.split(Regex("\\s+")).filter { it.isNotBlank() }

        // Имя должно содержать от 2 до 4 слов
        if (words.size !in 2..4) {
            return null
        }

        // Проверяем, что слова похожи на имена (не слишком короткие, не слишком длинные)
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

        // Разбиваем имя на слова
        val nameWords = normalizedName.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (nameWords.isEmpty()) return false

        // Проверяем, содержит ли сообщение хотя бы одно слово из имени
        val messageWords = normalizedMessage.split(Regex("\\s+")).filter { it.isNotBlank() }

        // Проверяем наличие хотя бы двух слов из имени в сообщении
        var matchCount = 0
        for (nameWord in nameWords) {
            if (messageWords.any { it.contains(nameWord) || nameWord.contains(it) }) {
                matchCount++
            }
        }

        // Если совпало хотя бы 2 слова или все слова (для коротких имен)
        return matchCount >= minOf(2, nameWords.size)
    }
}