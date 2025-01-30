package habittracker.userservice.dto.habit;

public enum CreateHabitServiceStatusDto {
    SUCCESS("Успешно"),
    INTERNAL_ERROR("Ошибка на стороне сервера");

    public final String description;

    CreateHabitServiceStatusDto(String description) {
        this.description = description;
    }

}
