package domain.usecase

import domain.model.WorkSettings
import java.time.LocalTime

class CalculateEarnedMoneyUseCase {
    operator fun invoke(settings: WorkSettings, currentTime: LocalTime): Long {
        val startTime = LocalTime.parse(settings.startTime)
        
        if (currentTime < startTime) {
            return 0
        }
        
        val workedSeconds = currentTime.toSecondOfDay() - startTime.toSecondOfDay()
        val workedMinutes = workedSeconds / 60
        val breakMinutes = settings.getBreakTimeMinutes()
        val actualWorkedMinutes = (workedMinutes - breakMinutes).coerceAtLeast(0)
        
        val hourlyWage = settings.getHourlyWage()
        return (hourlyWage * actualWorkedMinutes / 60)
    }
}
