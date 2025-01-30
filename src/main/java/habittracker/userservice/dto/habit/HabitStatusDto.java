package habittracker.userservice.dto.habit;

// это DTO = Status enum
public enum HabitStatusDto {
    ACTIVE,        // Привычка активна и отслеживается
    INACTIVE,      // Привычка временно не активна
    COMPLETED,     // Привычка выполнена
    OVERDUE,       // Привычка просрочена
    RECURRING,     // Привычка выполняется по расписанию
    FAILED,
}
