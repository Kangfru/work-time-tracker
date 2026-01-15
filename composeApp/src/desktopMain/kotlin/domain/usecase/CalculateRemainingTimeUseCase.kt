package domain.usecase

import domain.model.TimeRemaining
import domain.model.WorkSettings
import java.time.LocalTime

class CalculateRemainingTimeUseCase {
    operator fun invoke(settings: WorkSettings, currentTime: LocalTime): TimeRemaining {
        val expectedEndTime = settings.getExpectedEndTime()
        
        if (currentTime >= expectedEndTime) {
            return TimeRemaining(0, 0, 0, isWorkEnded = true)
        }
        
        val secondsRemaining = (expectedEndTime.toSecondOfDay() - currentTime.toSecondOfDay())
        val hours = secondsRemaining / 3600
        val minutes = (secondsRemaining % 3600) / 60
        val seconds = secondsRemaining % 60
        
        return TimeRemaining(hours, minutes, seconds)
    }
}
