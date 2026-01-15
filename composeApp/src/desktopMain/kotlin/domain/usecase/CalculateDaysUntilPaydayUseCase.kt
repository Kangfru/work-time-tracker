package domain.usecase

import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CalculateDaysUntilPaydayUseCase {
    operator fun invoke(paymentDay: Int): Pair<Long, LocalDate> {
        val today = LocalDate.now()
        var nextPayday = LocalDate.of(today.year, today.month, paymentDay.coerceIn(1, today.lengthOfMonth()))
        
        if (today > nextPayday || today == nextPayday) {
            nextPayday = nextPayday.plusMonths(1)
            nextPayday = nextPayday.withDayOfMonth(paymentDay.coerceIn(1, nextPayday.lengthOfMonth()))
        }
        
        val daysUntil = ChronoUnit.DAYS.between(today, nextPayday)
        return Pair(daysUntil, nextPayday)
    }
}
