package domain.notification

import data.notification.NotificationService
import domain.model.TimeRemaining
import domain.model.WorkSettings
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class NotificationManager(
    private val notificationService: NotificationService
) {
    private var lastHourlyNotificationHour: Int = -1
    private var lunchNotificationSent: Boolean = false
    private var lastNotificationDate: String = ""
    private var lastQuickInfoUpdate: Long = 0
    
    fun checkAndSendNotifications(
        settings: WorkSettings,
        currentTime: LocalTime,
        timeRemaining: TimeRemaining,
        earnedMoney: Long
    ) {
        val currentDate = java.time.LocalDate.now().toString()
        
        // 날짜가 바뀌면 알림 상태 초기화
        if (lastNotificationDate != currentDate) {
            lastNotificationDate = currentDate
            lastHourlyNotificationHour = -1
            lunchNotificationSent = false
        }
        
        // 퀵 정보 업데이트 (3초마다)
        val now = System.currentTimeMillis()
        if (now - lastQuickInfoUpdate > 3000) {
            lastQuickInfoUpdate = now
            updateQuickInfo(settings, timeRemaining, earnedMoney)
        }
        
        // 퇴근 시간 이후면 알림 안 보냄
        if (timeRemaining.isWorkEnded) {
            return
        }
        
        // 1. 정각 알림 체크
        if (settings.enableHourlyNotification) {
            checkHourlyNotification(currentTime, timeRemaining)
        }
        
        // 2. 점심 알림 체크
        if (settings.enableLunchNotification && !lunchNotificationSent) {
            checkLunchNotification(settings, currentTime)
        }
    }
    
    private fun updateQuickInfo(
        settings: WorkSettings,
        timeRemaining: TimeRemaining,
        earnedMoney: Long
    ) {
        val expectedEndTime = settings.getExpectedEndTime()
            .format(DateTimeFormatter.ofPattern("HH:mm"))
        
        notificationService.updateQuickInfo(
            timeRemaining = timeRemaining,
            earnedMoney = earnedMoney,
            expectedEndTime = expectedEndTime
        )
    }
    
    private fun checkHourlyNotification(currentTime: LocalTime, timeRemaining: TimeRemaining) {
        val currentHour = currentTime.hour
        val currentMinute = currentTime.minute
        val currentSecond = currentTime.second
        
        if (currentMinute == 0 && currentSecond in 0..5 && lastHourlyNotificationHour != currentHour) {
            lastHourlyNotificationHour = currentHour
            
            val message = if (timeRemaining.hours > 0) {
                "퇴근까지 ${timeRemaining.hours}시간 ${timeRemaining.minutes}분 남았습니다"
            } else {
                "퇴근까지 ${timeRemaining.minutes}분 남았습니다"
            }
            
            notificationService.sendNotification(
                "⏰ 퇴근 알림",
                message
            )
        }
    }
    
    private fun checkLunchNotification(settings: WorkSettings, currentTime: LocalTime) {
        val lunchNotificationTime = settings.getLunchNotificationTime()
        
        if (currentTime.hour == lunchNotificationTime.hour &&
            currentTime.minute == lunchNotificationTime.minute &&
            currentTime.second in 0..5) {
            
            lunchNotificationSent = true
            
            notificationService.sendNotification(
                "🍽️ 점심시간 알림",
                "5분 후 점심시간입니다"
            )
        }
    }
    
    fun dispose() {
        notificationService.dispose()
    }
}
