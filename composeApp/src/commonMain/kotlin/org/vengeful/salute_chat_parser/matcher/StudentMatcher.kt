package org.vengeful.salute_chat_parser.matcher

import org.vengeful.salute_chat_parser.model.AttendanceRecord
import org.vengeful.salute_chat_parser.model.Student

object StudentMatcher {
    private const val SIMILARITY_THRESHOLD = 0.7
    
    /**
     * Находит наиболее подходящего студента для записи посещаемости
     */
    fun findBestMatch(
        record: AttendanceRecord,
        students: List<Student>
    ): Student? {
        if (students.isEmpty()) return null
        
        val recordWords = normalizeAndSplit(record.normalizedName)
        if (recordWords.isEmpty()) return null
        
        var bestMatch: Student? = null
        var bestScore = 0.0
        
        for (student in students) {
            val studentWords = normalizeAndSplit(student.normalizedName)
            val score = calculateSimilarity(recordWords, studentWords)
            
            if (score > bestScore && score >= SIMILARITY_THRESHOLD) {
                bestScore = score
                bestMatch = student
            }
        }
        
        return bestMatch
    }
    
    /**
     * Нормализует и разбивает строку на слова
     */
    private fun normalizeAndSplit(name: String): List<String> {
        return name.lowercase()
            .trim()
            .replace(Regex("\\s+"), " ")
            .split(" ")
            .filter { it.isNotBlank() }
    }
    
    /**
     * Вычисляет схожесть между двумя наборами слов
     */
    private fun calculateSimilarity(words1: List<String>, words2: List<String>): Double {
        if (words1.isEmpty() || words2.isEmpty()) return 0.0
        
        // Проверяем точное совпадение
        if (words1 == words2) return 1.0
        
        // Проверяем совпадение без учета порядка
        val words1Set = words1.toSet()
        val words2Set = words2.toSet()
        
        if (words1Set == words2Set) return 0.95
        
        // Подсчитываем общие слова
        val commonWords = words1Set.intersect(words2Set)
        val allWords = words1Set.union(words2Set)
        
        if (allWords.isEmpty()) return 0.0
        
        val wordMatchScore = commonWords.size.toDouble() / allWords.size
        
        // Дополнительная проверка на частичное совпадение слов
        var partialMatchScore = 0.0
        for (word1 in words1) {
            for (word2 in words2) {
                val similarity = calculateWordSimilarity(word1, word2)
                if (similarity > partialMatchScore) {
                    partialMatchScore = similarity
                }
            }
        }
        
        // Комбинируем оценки
        return (wordMatchScore * 0.7 + partialMatchScore * 0.3).coerceIn(0.0, 1.0)
    }
    
    /**
     * Вычисляет схожесть между двумя словами (расстояние Левенштейна)
     */
    private fun calculateWordSimilarity(word1: String, word2: String): Double {
        if (word1 == word2) return 1.0
        if (word1.isEmpty() || word2.isEmpty()) return 0.0
        
        // Простая проверка на включение
        if (word1.contains(word2) || word2.contains(word1)) {
            val minLen = minOf(word1.length, word2.length)
            val maxLen = maxOf(word1.length, word2.length)
            return minLen.toDouble() / maxLen
        }
        
        // Расстояние Левенштейна
        val distance = levenshteinDistance(word1, word2)
        val maxLen = maxOf(word1.length, word2.length)
        return 1.0 - (distance.toDouble() / maxLen)
    }
    
    /**
     * Вычисляет расстояние Левенштейна между двумя строками
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        
        for (i in 1..m) {
            for (j in 1..n) {
                if (s1[i - 1] == s2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1]
                } else {
                    dp[i][j] = minOf(
                        dp[i - 1][j] + 1,      // удаление
                        dp[i][j - 1] + 1,      // вставка
                        dp[i - 1][j - 1] + 1   // замена
                    )
                }
            }
        }
        
        return dp[m][n]
    }
}

