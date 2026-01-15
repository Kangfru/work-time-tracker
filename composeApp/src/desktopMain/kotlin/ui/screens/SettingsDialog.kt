package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import domain.model.WorkSettings

@Composable
fun SettingsDialog(
    currentSettings: WorkSettings,
    onDismiss: () -> Unit,
    onSave: (WorkSettings) -> Unit
) {
    var startTime by remember { mutableStateOf(currentSettings.startTime) }
    var endTime by remember { mutableStateOf(currentSettings.endTime) }
    var monthlySalary by remember { mutableStateOf(currentSettings.monthlySalary.toString()) }
    var paymentDay by remember { mutableStateOf(currentSettings.paymentDay.toString()) }
    var monthlyWorkHours by remember { mutableStateOf(currentSettings.monthlyWorkHours.toString()) }
    var lunchTime by remember { mutableStateOf(currentSettings.lunchTime) }
    var enableHourlyNotification by remember { mutableStateOf(currentSettings.enableHourlyNotification) }
    var enableLunchNotification by remember { mutableStateOf(currentSettings.enableLunchNotification) }
    
    var startTimeError by remember { mutableStateOf(false) }
    var endTimeError by remember { mutableStateOf(false) }
    var lunchTimeError by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("⚙️ 설정") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
                    .verticalScroll(scrollState)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "근무 시간",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { 
                            startTime = it
                            startTimeError = !isValidTimeFormat(it)
                        },
                        label = { Text("출근 시간") },
                        placeholder = { Text("09:00") },
                        isError = startTimeError,
                        supportingText = if (startTimeError) {
                            { Text("HH:mm 형식") }
                        } else null,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { 
                            endTime = it
                            endTimeError = !isValidTimeFormat(it)
                        },
                        label = { Text("퇴근 시간") },
                        placeholder = { Text("18:00") },
                        isError = endTimeError,
                        supportingText = if (endTimeError) {
                            { Text("HH:mm 형식") }
                        } else null,
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                
                Divider()
                
                Text(
                    text = "급여 정보",
                    style = MaterialTheme.typography.titleMedium
                )
                
                OutlinedTextField(
                    value = monthlySalary,
                    onValueChange = { 
                        if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                            monthlySalary = it
                        }
                    },
                    label = { Text("월급 (원)") },
                    placeholder = { Text("3000000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = paymentDay,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                                paymentDay = it
                            }
                        },
                        label = { Text("월급날 (일)") },
                        placeholder = { Text("25") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = monthlyWorkHours,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() } || it.isEmpty()) {
                                monthlyWorkHours = it
                            }
                        },
                        label = { Text("월 근로시간") },
                        placeholder = { Text("209") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                
                Text(
                    text = "* 주 40시간 기준: 209시간",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Divider()
                
                Text(
                    text = "알림 설정",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("정각 알림 (매시간)")
                    Switch(
                        checked = enableHourlyNotification,
                        onCheckedChange = { enableHourlyNotification = it }
                    )
                }
                
                OutlinedTextField(
                    value = lunchTime,
                    onValueChange = { 
                        lunchTime = it
                        lunchTimeError = !isValidTimeFormat(it)
                    },
                    label = { Text("점심시간") },
                    placeholder = { Text("11:30") },
                    isError = lunchTimeError,
                    supportingText = if (lunchTimeError) {
                        { Text("HH:mm 형식") }
                    } else {
                        { Text("5분 전에 알림이 옵니다") }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("점심시간 알림")
                    Switch(
                        checked = enableLunchNotification,
                        onCheckedChange = { enableLunchNotification = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!startTimeError && !endTimeError && !lunchTimeError &&
                        isValidTimeFormat(startTime) && isValidTimeFormat(endTime) && isValidTimeFormat(lunchTime)) {
                        onSave(
                            WorkSettings(
                                startTime = startTime,
                                endTime = endTime,
                                monthlySalary = monthlySalary.toLongOrNull() ?: 0,
                                paymentDay = paymentDay.toIntOrNull()?.coerceIn(1, 31) ?: 25,
                                monthlyWorkHours = monthlyWorkHours.toIntOrNull() ?: 209,
                                lunchTime = lunchTime,
                                enableHourlyNotification = enableHourlyNotification,
                                enableLunchNotification = enableLunchNotification
                            )
                        )
                    }
                },
                enabled = !startTimeError && !endTimeError && !lunchTimeError &&
                         isValidTimeFormat(startTime) && isValidTimeFormat(endTime) && isValidTimeFormat(lunchTime)
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

private fun isValidTimeFormat(time: String): Boolean {
    val regex = Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    return regex.matches(time)
}
