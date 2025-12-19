package org.vengeful.salute_chat_parser.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.vengeful.salute_chat_parser.viewmodel.AttendanceViewModel
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun MainScreen(
    viewModel: AttendanceViewModel = viewModel()
) {
    val state = viewModel.studentAttendances
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    var showHelpDialog by remember { mutableStateOf(false) }

    val presentStudents = state.filter { it.isPresent }
    val absentStudents = state.filter { !it.isPresent }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Парсер посещаемости",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.weight(1f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { showHelpDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Справка"
                    )
                }

                if (state.isNotEmpty() || errorMessage != null) {
                    IconButton(
                        onClick = { viewModel.clearAll() },
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Очистить"
                        )
                    }
                }
            }
        }

        Text(
            text = "Загрузите лог чата и список студентов для анализа посещаемости",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { loadChatLogFile(viewModel) },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                Text("Загрузить лог чата (.txt)")
            }

            Button(
                onClick = { loadStudentListFile(viewModel) },
                modifier = Modifier.weight(1f),
                enabled = !isLoading
            ) {
                Text("Загрузить список студентов (.txt/.xlsx)")
            }
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Закрыть")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Всего студентов",
                    value = state.size.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Присутствуют",
                    value = presentStudents.size.toString(),
                    modifier = Modifier.weight(1f),
                    isPositive = true
                )
                StatCard(
                    title = "Отсутствуют",
                    value = absentStudents.size.toString(),
                    modifier = Modifier.weight(1f),
                    isPositive = false
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (state.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Присутствующие
                if (presentStudents.isNotEmpty()) {
                    item {
                        Text(
                            text = "Присутствующие (${presentStudents.size})",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                        )
                    }

                    items(presentStudents) { attendance ->
                        StudentCard(attendance = attendance)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Отсутствующие
                if (absentStudents.isNotEmpty()) {
                    item {
                        Text(
                            text = "Отсутствующие (${absentStudents.size})",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                        )
                    }

                    items(absentStudents) { attendance ->
                        StudentCard(attendance = attendance)
                    }
                }
            }
        } else {
            // Пустое состояние
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Загрузите файлы для начала анализа",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Диалог справки
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text(
                    text = "Требования к файлам",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Лог чата (.txt):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• Формат строки: ГГГГ-ММ-ДД ЧЧ:ММ:СС - Имя Отправителя: Сообщение\n" +
                                "• Пример: 2025-12-01 19:44:01 - Иванов Иван: Иванов Иван\n" +
                                "• Сообщение должно содержать имя студента в формате \"Фамилия Имя\"\n" +
                                "• Строки без отметки о присутствии будут проигнорированы",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Список студентов (.txt или .xlsx):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• .txt: одна строка на студента в формате \"Фамилия Имя\"\n" +
                                "• .xlsx: поддерживаются форматы с отдельными колонками \"Фамилия\" и \"Имя\" или одной колонкой \"ФИО\"\n" +
                                "• Пример .txt:\n" +
                                "  Иван Иванов\n" +
                                "  Сидоров Виктор Петрович\n" +
                                "  Петров Пётр",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Понятно")
                }
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    isPositive: Boolean? = null
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = when (isPositive) {
                    true -> MaterialTheme.colorScheme.primary
                    false -> MaterialTheme.colorScheme.error
                    null -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun loadChatLogFile(viewModel: AttendanceViewModel) {
    val fileChooser = JFileChooser().apply {
        fileFilter = FileNameExtensionFilter("Text files (.txt)", "txt")
        isMultiSelectionEnabled = false
    }
    
    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        val file = fileChooser.selectedFile
        try {
            val content = file.readText(Charsets.UTF_8)
            viewModel.loadChatLog(content)
        } catch (e: Exception) {
            // Ошибка будет обработана в ViewModel
        }
    }
}

private fun loadStudentListFile(viewModel: AttendanceViewModel) {
    val fileChooser = JFileChooser().apply {
        fileFilter = FileNameExtensionFilter("Text and Excel files (.txt, .xlsx)", "txt", "xlsx")
        isMultiSelectionEnabled = false
    }
    
    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
        val file = fileChooser.selectedFile
        try {
            when (file.extension.lowercase()) {
                "txt" -> {
                    val content = file.readText(Charsets.UTF_8)
                    viewModel.loadStudentListTxt(content)
                }
                "xlsx" -> {
                    val content = file.readBytes()
                    viewModel.loadStudentListXlsx(content)
                }
            }
        } catch (e: Exception) {
            // Ошибка будет обработана в ViewModel
        }
    }
}

