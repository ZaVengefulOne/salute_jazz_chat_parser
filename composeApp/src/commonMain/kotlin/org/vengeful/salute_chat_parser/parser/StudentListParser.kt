package org.vengeful.salute_chat_parser.parser

import org.vengeful.salute_chat_parser.model.Student

expect class StudentListParser {
    companion object {
        fun parseTxt(content: String): List<Student>
        fun parseXlsx(content: ByteArray): List<Student>
    }
}

