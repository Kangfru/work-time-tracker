package domain.model

import kotlinx.serialization.Serializable
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Serializable
data class WorkSettings(
    val startTime: String = "08:00",
    val endTime: String = "17:00",
    val monthlySalary: Long = 0,
    val paymentDay: Int = 25,
    val monthlyWorkHours: Int = 209,
    val lunchTime: String = "11:30",
    val enableHourlyNotification: Boolean = true,
    val enableLunchNotification: Boolean = true
) {
    fun getWorkDurationMinutes(): Int {
        val start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"))
        val end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"))
        return ((end.toSecondOfDay() - start.toSecondOfDay()) / 60)
    }
    
    fun getBreakTimeMinutes(): Int {
        val workMinutes = getWorkDurationMinutes()
        return when {
            workMinutes > 480 -> 60  // 8시간 초과 -> 1시간
            workMinutes > 240 -> 30  // 4시간 초과 -> 30분
            else -> 0
        }
    }
    
    fun getExpectedEndTime(): LocalTime {
        return LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"))
    }
    
    fun getHourlyWage(): Long {
        return if (monthlyWorkHours > 0) {
            monthlySalary / monthlyWorkHours
        } else 0
    }
    
    fun getLunchTimeAsLocalTime(): LocalTime {
        return LocalTime.parse(lunchTime, DateTimeFormatter.ofPattern("HH:mm"))
    }
    
    fun getLunchNotificationTime(): LocalTime {
        return getLunchTimeAsLocalTime().minusMinutes(5)
    }
}
