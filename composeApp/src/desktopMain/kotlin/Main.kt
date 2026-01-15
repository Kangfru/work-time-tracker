import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.local.PreferencesRepository
import data.notification.NotificationService
import domain.notification.NotificationManager
import domain.usecase.CalculateDaysUntilPaydayUseCase
import domain.usecase.CalculateEarnedMoneyUseCase
import domain.usecase.CalculateRemainingTimeUseCase
import ui.MainViewModel
import ui.screens.MainScreen
import java.awt.Window as AwtWindow

fun main() = application {
    val windowState = rememberWindowState(width = 500.dp, height = 700.dp)
    var isVisible by remember { mutableStateOf(true) }
    
    val notificationService = remember { NotificationService() }
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "퇴근시간 추적기",
        state = windowState,
        visible = isVisible
    ) {
        // 트레이 아이콘 클릭 시 창 보이기/앞으로 가져오기
        LaunchedEffect(Unit) {
            notificationService.initialize {
                isVisible = true
                // AWT Window를 찾아서 앞으로 가져오기
                AwtWindow.getWindows().firstOrNull()?.let { awtWindow ->
                    awtWindow.toFront()
                    awtWindow.requestFocus()
                }
            }
        }
        
        val viewModel = remember {
            MainViewModel(
                preferencesRepository = PreferencesRepository(),
                calculateRemainingTime = CalculateRemainingTimeUseCase(),
                calculateEarnedMoney = CalculateEarnedMoneyUseCase(),
                calculateDaysUntilPayday = CalculateDaysUntilPaydayUseCase(),
                notificationManager = NotificationManager(notificationService)
            )
        }
        
        MaterialTheme {
            MainScreen(viewModel)
        }
    }
}
