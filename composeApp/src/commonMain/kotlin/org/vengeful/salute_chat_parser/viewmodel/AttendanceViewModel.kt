package org.vengeful.salute_chat_parser.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.vengeful.salute_chat_parser.matcher.StudentMatcher
import org.vengeful.salute_chat_parser.model.AttendanceRecord
import org.vengeful.salute_chat_parser.model.Student
import org.vengeful.salute_chat_parser.model.StudentAttendance
import org.vengeful.salute_chat_parser.parser.ChatLogParser
import org.vengeful.salute_chat_parser.parser.StudentListParser

class AttendanceViewModel : ViewModel() {
    var allStudents by mutableStateOf<List<Student>>(emptyList())
        private set
    
    var attendanceRecords by mutableStateOf<List<AttendanceRecord>>(emptyList())
        private set
    
    var studentAttendances by mutableStateOf<List<StudentAttendance>>(emptyList())
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    fun loadChatLog(content: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                isLoading = true
                errorMessage = null
                
                val records = ChatLogParser.parseChatLog(content)
                attendanceRecords = records
                
                updateStudentAttendances()
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке лога чата: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    fun loadStudentListTxt(content: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                isLoading = true
                errorMessage = null
                
                val students = StudentListParser.parseTxt(content)
                allStudents = students
                
                updateStudentAttendances()
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке списка студентов: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    fun loadStudentListXlsx(content: ByteArray) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                isLoading = true
                errorMessage = null
                
                val students = StudentListParser.parseXlsx(content)
                allStudents = students
                
                updateStudentAttendances()
            } catch (e: Exception) {
                errorMessage = "Ошибка при загрузке списка студентов: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    private fun updateStudentAttendances() {
        if (allStudents.isEmpty()) {
            studentAttendances = emptyList()
            return
        }

        val attendanceMap = mutableMapOf<Student, AttendanceRecord>()
        
        for (record in attendanceRecords) {
            val matchedStudent = StudentMatcher.findBestMatch(record, allStudents)
            if (matchedStudent != null) {
                val existing = attendanceMap[matchedStudent]
                if (existing == null || record.timestamp < existing.timestamp) {
                    attendanceMap[matchedStudent] = record
                }
            }
        }

        studentAttendances = allStudents.map { student ->
            StudentAttendance(
                student = student,
                attendanceRecord = attendanceMap[student]
            )
        }.sortedBy { it.student.fullName }
    }

    fun clearError() {
        errorMessage = null
    }

    fun clearAll() {
        allStudents = emptyList()
        attendanceRecords = emptyList()
        studentAttendances = emptyList()
        errorMessage = null
    }
}

