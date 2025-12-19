package org.vengeful.salute_chat_parser.parser

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.vengeful.salute_chat_parser.model.Student
import java.io.ByteArrayInputStream

actual class StudentListParser {
    actual companion object {
        actual fun parseTxt(content: String): List<Student> {
            val students = mutableListOf<Student>()
            val lines = content.lines()
            
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.isBlank()) continue
                
                val student = parseStudentLine(trimmed) ?: continue
                students.add(student)
            }
            
            return students
        }
        
        actual fun parseXlsx(content: ByteArray): List<Student> {
            val students = mutableListOf<Student>()
            
            try {
                val workbook = WorkbookFactory.create(ByteArrayInputStream(content))
                val sheet = workbook.getSheetAt(0)
                
                // Пытаемся определить формат файла
                val firstRow = sheet.getRow(0) ?: return emptyList()
                
                // Ищем колонки с фамилией и именем
                var lastNameCol = -1
                var firstNameCol = -1
                var fullNameCol = -1
                
                // Проверяем заголовки
                for (cell in firstRow) {
                    val header = cell.stringCellValue.lowercase().trim()
                    when {
                        header.contains("фамилия") || header.contains("surname") || header.contains("lastname") -> {
                            lastNameCol = cell.columnIndex
                        }
                        header.contains("имя") || header.contains("name") || header.contains("firstname") -> {
                            firstNameCol = cell.columnIndex
                        }
                        header.contains("фио") || header.contains("fullname") || header.contains("name") -> {
                            fullNameCol = cell.columnIndex
                        }
                    }
                }
                
                // Если нашли отдельные колонки
                if (lastNameCol >= 0 && firstNameCol >= 0) {
                    for (rowIndex in 1 until sheet.lastRowNum + 1) {
                        val row = sheet.getRow(rowIndex) ?: continue
                        val lastName = row.getCell(lastNameCol)?.stringCellValue?.trim() ?: continue
                        val firstName = row.getCell(firstNameCol)?.stringCellValue?.trim() ?: continue
                        
                        if (lastName.isNotBlank() && firstName.isNotBlank()) {
                            students.add(Student(firstName = firstName, lastName = lastName))
                        }
                    }
                } else if (fullNameCol >= 0) {
                    // Если одна колонка с ФИО
                    for (rowIndex in 1 until sheet.lastRowNum + 1) {
                        val row = sheet.getRow(rowIndex) ?: continue
                        val fullName = row.getCell(fullNameCol)?.stringCellValue?.trim() ?: continue
                        
                        if (fullName.isNotBlank()) {
                            val student = parseStudentLine(fullName) ?: continue
                            students.add(student)
                        }
                    }
                } else {
                    // Пытаемся использовать первую колонку как ФИО
                    for (rowIndex in 0 until sheet.lastRowNum + 1) {
                        val row = sheet.getRow(rowIndex) ?: continue
                        val cell = row.getCell(0) ?: continue
                        val fullName = cell.stringCellValue.trim()
                        
                        if (fullName.isNotBlank()) {
                            val student = parseStudentLine(fullName) ?: continue
                            students.add(student)
                        }
                    }
                }
                
                workbook.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            return students
        }
        
        /**
         * Парсит строку в формате "Фамилия Имя" или "Имя Фамилия"
         */
        private fun parseStudentLine(line: String): Student? {
            val parts = line.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
            if (parts.isEmpty()) return null
            
            // Предполагаем, что первое слово - фамилия, остальные - имя
            // Это стандартный формат для русских имен
            if (parts.size == 1) {
                return Student(firstName = parts[0], lastName = parts[0])
            }
            
            val lastName = parts[0]
            val firstName = parts.subList(1, parts.size).joinToString(" ")
            
            return Student(firstName = firstName, lastName = lastName)
        }
    }
}

