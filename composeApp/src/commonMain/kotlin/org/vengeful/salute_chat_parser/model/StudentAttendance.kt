package org.vengeful.salute_chat_parser.model

data class StudentAttendance(
    val student: Student,
    val attendanceRecord: AttendanceRecord?
) {
    val isPresent: Boolean
        get() = attendanceRecord != null
}

