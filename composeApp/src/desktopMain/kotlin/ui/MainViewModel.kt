package ui

import androidx.compose.runtime.*
import data.local.PreferencesRepository
import domain.model.TimeRemaining
import domain.model.WorkSettings
import domain.notification.NotificationManager
import domain.usecase.CalculateDaysUntilPaydayUseCase
import domain.usecase.CalculateEarnedMoneyUseCase
import domain.usecase.CalculateRemainingTimeUseCase
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalTime

class MainViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val calculateRemainingTime: CalculateRemainingTimeUseCase,
    private val calculateEarnedMoney: CalculateEarnedMoneyUseCase,
    private val calculateDaysUntilPayday: CalculateDaysUntilPaydayUseCase,
    private val notificationManager: NotificationManager
) {
    var settings by mutableStateOf(preferencesRepository.loadSettings())
        private set
    
    var currentTime by mutableStateOf(LocalTime.now())
        private set
    
    var timeRemaining by mutableStateOf(TimeRemaining(0, 0, 0))
        private set
    
    var earnedMoney by mutableStateOf(0L)
        private set
    
    var daysUntilPayday by mutableStateOf(0L)
        private set
    
    var nextPaydayDate by mutableStateOf(LocalDate.now())
        private set
    
    var isSettingsDialogOpen by mutableStateOf(false)
        private set
    
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    init {
        updateCalculations()
    }
    
    fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive) {
                currentTime = LocalTime.now()
                updateCalculations()
                
                // 알림 체크
                notificationManager.checkAndSendNotifications(
                    settings = settings,
                    currentTime = currentTime,
                    timeRemaining = timeRemaining,
                    earnedMoney = earnedMoney
                )
                
                delay(1000)
            }
        }
    }
    
    fun stopTimer() {
        timerJob?.cancel()
    }
    
    private fun updateCalculations() {
        timeRemaining = calculateRemainingTime(settings, currentTime)
        earnedMoney = calculateEarnedMoney(settings, currentTime)
        val (days, nextPayday) = calculateDaysUntilPayday(settings.paymentDay)
        daysUntilPayday = days
        nextPaydayDate = nextPayday
    }
    
    fun updateSettings(newSettings: WorkSettings) {
        settings = newSettings
        preferencesRepository.saveSettings(newSettings)
        updateCalculations()
    }
    
    fun openSettingsDialog() {
        isSettingsDialogOpen = true
    }
    
    fun closeSettingsDialog() {
        isSettingsDialogOpen = false
    }
    
    fun dispose() {
        stopTimer()
        notificationManager.dispose()
        scope.cancel()
    }
}
