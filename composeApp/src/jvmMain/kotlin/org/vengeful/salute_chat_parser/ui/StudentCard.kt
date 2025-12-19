package org.vengeful.salute_chat_parser.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.vengeful.salute_chat_parser.model.StudentAttendance

@Composable
fun StudentCard(
    attendance: StudentAttendance,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (attendance.isPresent) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = if (attendance.isPresent) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = attendance.student.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                
                if (attendance.isPresent && attendance.attendanceRecord != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val timestamp = attendance.attendanceRecord.timestamp
                    val timeStr = String.format(
                        "%04d-%02d-%02d %02d:%02d:%02d",
                        timestamp.year,
                        timestamp.monthNumber,
                        timestamp.dayOfMonth,
                        timestamp.hour,
                        timestamp.minute,
                        timestamp.second
                    )
                    Text(
                        text = "Подключился: $timeStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Отсутствует",
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (attendance.isPresent) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}

