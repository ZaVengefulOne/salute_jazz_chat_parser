package org.vengeful.salute_chat_parser.model

data class Student(
    val firstName: String,
    val lastName: String
) {
    val fullName: String
        get() = "$lastName $firstName"
    
    val normalizedName: String
        get() = fullName.lowercase().trim().replace(Regex("\\s+"), " ")
}

