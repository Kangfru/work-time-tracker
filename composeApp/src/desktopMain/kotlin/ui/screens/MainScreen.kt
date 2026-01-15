package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.MainViewModel
import ui.components.SalaryCard
import ui.components.WorkTimeCard
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        viewModel.startTimer()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopTimer()
        }
    }
    
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("퇴근시간 추적기") },
                actions = {
                    IconButton(onClick = { viewModel.openSettingsDialog() }) {
                        Icon(Icons.Default.Settings, "설정")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WorkTimeCard(
                startTime = viewModel.settings.startTime,
                expectedEndTime = viewModel.settings.getExpectedEndTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm")),
                remainingTime = viewModel.timeRemaining,
                earnedMoney = viewModel.earnedMoney
            )
            
            SalaryCard(
                daysUntil = viewModel.daysUntilPayday,
                nextPaydayDate = viewModel.nextPaydayDate
                    .format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))
            )
        }
    }
    
    if (viewModel.isSettingsDialogOpen) {
        SettingsDialog(
            currentSettings = viewModel.settings,
            onDismiss = { viewModel.closeSettingsDialog() },
            onSave = { newSettings ->
                viewModel.updateSettings(newSettings)
                viewModel.closeSettingsDialog()
            }
        )
    }
}
