package domain.model

data class TimeRemaining(
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val isWorkEnded: Boolean = false
) {
    fun toFormattedString(): String {
        return if (isWorkEnded) {
            "퇴근 완료! 🎉"
        } else {
            "${hours}시간 ${minutes}분 ${seconds}초"
        }
    }
}
